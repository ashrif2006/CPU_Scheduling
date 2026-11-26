package scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import model.Process;
import model.ProcessResult;
import model.QueueSnapshot;
import model.SchedulerSnapshot;
import simulation.SimulationResult;
import simulation.SnapshotBuffer;
import utils.OrderedQueue;


public class SJFScheduler extends Scheduler {

    // Simulation configuration
    private int contextSwitch;
    private int totalProcesses;

    // Simulation state
    private int tick;
    private int switchingStartedAt;
    private SchedulerStatus status;
    private Process runningProcess;

    // Queues
    private OrderedQueue<Process> pendingProcesses;
    private OrderedQueue<Process> readyQueue;
    private OrderedQueue<Process> completedProcesses;

    // Snapshot management
    private final SnapshotBuffer snapshotBuffer;

    public SJFScheduler(SnapshotBuffer buffer) {
        this.snapshotBuffer = buffer;
    }

    @Override
    public SimulationResult simulate(
            List<Process> processes,
            int quantum,
            int contextSwitch) {

        this.contextSwitch = contextSwitch;
        this.totalProcesses = processes.size();

        tick = 0;
        switchingStartedAt = 0;
        status = SchedulerStatus.IDLE;
        runningProcess = null;

        pendingProcesses = new OrderedQueue<>(
                Comparator.comparingInt(Process::getArrivalTime)
        );

        // Shortest remaining time first, ties go to the earlier arrival,
        // then to whoever entered the queue first , OrderedQueue is stable.
        readyQueue = new OrderedQueue<>(
                Comparator.comparingInt(Process::getRemainingTime)
                        .thenComparingInt(Process::getArrivalTime)
        );

        completedProcesses = new OrderedQueue<>(
                Comparator.comparingInt(Process::getCompletionTime)
        );

        for (Process source : processes) {
            pendingProcesses.offer(new Process(
                    source.getProcessId(),
                    source.getArrivalTime(),
                    source.getBurstTime(),
                    source.getPriority()
            ));
        }

        while (completedProcesses.size() < totalProcesses) {

            // 1. Admit arriving processes.
            admitArrivingProcesses();

            // 2. Check for completion or preemption.
            if (status == SchedulerStatus.PROCESSING) {
                if (runningProcess.getRemainingTime() == 0) {
                    runningProcess.makeCompleted(tick);
                    completedProcesses.offer(runningProcess);
                    makeSwitching();

                } else if (shouldPreempt()) {
                    readyQueue.offer(runningProcess);
                    makeSwitching();
                }
            }

            // 3. Check whether the context switch has finished.
            if (status == SchedulerStatus.SWITCHING
                    && tick - switchingStartedAt >= this.contextSwitch) {
                status = SchedulerStatus.IDLE;
            }

            // 4. Dispatch the shortest process if the CPU is available.
            if (status == SchedulerStatus.IDLE) {
                runningProcess = readyQueue.poll();

                if (runningProcess != null) {
                    status = SchedulerStatus.PROCESSING;
                }
            }

            // 5. Capture the state at the beginning of this time unit.
            snapshotBuffer.publish(createSnapshot(null));

            // 6. Execute the running process for one time unit.
            if (status == SchedulerStatus.PROCESSING) {
                runningProcess.decreaseRemainingTime();
            }

            // 7. Advance the clock.
            tick++;
        }

        SimulationResult result = createSimulationResult();

        status = SchedulerStatus.COMPLETED;
        snapshotBuffer.publish(createSnapshot(result));

        return result;
    }

    private void admitArrivingProcesses() {
        while (!pendingProcesses.isEmpty()
                && pendingProcesses.peek().getArrivalTime() <= tick) {

            readyQueue.offer(pendingProcesses.poll());
        }
    }

    private boolean shouldPreempt() {
        return !readyQueue.isEmpty()
                && readyQueue.peek().getRemainingTime()
                        < runningProcess.getRemainingTime();
    }

    private void makeSwitching() {
        runningProcess = null;
        switchingStartedAt = tick;
        status = SchedulerStatus.SWITCHING;
    }

    private SchedulerSnapshot createSnapshot(SimulationResult result) {
        QueueSnapshot arrivingSnapshot = new QueueSnapshot(
                "0",
                "Arriving",
                pendingProcesses.snapshot()
        );

        QueueSnapshot readySnapshot = new QueueSnapshot(
                "1",
                "Ready",
                readyQueue.snapshot()
        );

        return new SchedulerSnapshot(
                tick,
                status,
                runningProcess,
                "Ready",
                List.of(arrivingSnapshot, readySnapshot),
                List.of(),
                result
        );
    }

    private SimulationResult createSimulationResult() {
        List<ProcessResult> results = new ArrayList<>();

        for (Process process : completedProcesses.snapshot()) {
            results.add(new ProcessResult(
                    process.getProcessId(),
                    process.getArrivalTime(),
                    process.getBurstTime(),
                    process.getCompletionTime()
            ));
        }

        return new SimulationResult(results);
    }

    @Override
    public String getName() {
        return "Preemptive Shortest Job First";
    }
}