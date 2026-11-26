package scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import model.DemotionSnapshot;
import model.Process;
import model.ProcessResult;
import model.QueueSnapshot;
import model.SchedulerSnapshot;
import simulation.SimulationResult;
import simulation.SnapshotBuffer;
import utils.OrderedQueue;

public class ACSScheduler extends Scheduler {

    private static final int AGING_INTERVAL = 4;

    // Simulation configuration
    protected int contextSwitch;
    protected List<Process> processes;

    // Simulation state
    protected int tick;
    protected int runningQuantum;
    protected int switchingStartedAt;
    protected SchedulerStatus status;
    protected Process runningProcess;
    protected boolean isPreemptiveProcess;

    // Scheduling queues
    OrderedQueue<Process> pendingProcesses;
    OrderedQueue<Process> tier1;
    OrderedQueue<Process> tier2;
    OrderedQueue<Process> completedProcesses;

    private Map<Process, Integer> demotionTimes;

    // Snapshot management
    private final SnapshotBuffer snapshotBuffer;
    List<DemotionSnapshot> demotedProcesses;

    public ACSScheduler(SnapshotBuffer buffer) {
        snapshotBuffer = buffer;
    }

    @Override
    public SimulationResult simulate(
            List<Process> processes,
            int quantum,
            int contextSwitch) {

        this.contextSwitch = contextSwitch;
        this.processes = processes;

        tick = 0;
        switchingStartedAt = 0;
        runningQuantum = quantum;
        isPreemptiveProcess = true;
        status = SchedulerStatus.IDLE;
        runningProcess = null;

        pendingProcesses = new OrderedQueue<>(
                Comparator.comparingInt(Process::getArrivalTime)
        );

        tier1 = new OrderedQueue<>((p1, p2) -> 0);

        tier2 = new OrderedQueue<>(
                Comparator.comparingInt(Process::getPriority)
        );

        completedProcesses = new OrderedQueue<>(
                Comparator.comparingInt(Process::getCompletionTime)
        );

        demotedProcesses = new ArrayList<>();
        demotionTimes = new IdentityHashMap<>();

        for (Process process : processes) {
            pendingProcesses.offer(process);
        }

        while (isWorking()) {

            // 1. Admit arriving processes.
            admitArrivingProcesses();

            // 2. Check for completion or preemption.
            if (status == SchedulerStatus.PROCESSING) {
                if (isCompleted()) {
                    runningProcess.makeCompleted(tick);
                    completeProcess();
                    makeSwitching();

                } else if (shouldPreempt()) {
                    preemptProcess();
                    makeSwitching();
                }
            }

            // 3. Check whether the context switch has finished.
            if (status == SchedulerStatus.SWITCHING) {
                if (tick - switchingStartedAt >= this.contextSwitch) {
                    status = SchedulerStatus.IDLE;
                }
            }

            // 4. Dispatch the next process if the CPU is available.
            if (status == SchedulerStatus.IDLE) {
                runningProcess = getNextProcess();

                if (runningProcess != null) {
                    runningQuantum = quantum;
                    status = SchedulerStatus.PROCESSING;
                }
            }

            // 5. Capture the state at the beginning of this time unit.
            takeSnapshot();

            // 6. Execute the running process for one time unit.
            if (status == SchedulerStatus.PROCESSING) {
                executeTick();
            }

            // 7. Advance the clock and update waiting processes.
            tick++;
            updateWaitingProcesses();
        }

        SimulationResult result = createSimulationResult();

        snapshotBuffer.publish(createFinalSnapshot(result));

        return result;
    }

    protected boolean isWorking() {
        return completedProcesses.size() < processes.size();
    }

    private void admitArrivingProcesses() {
        while (!pendingProcesses.isEmpty()
                && pendingProcesses.peek().getArrivalTime() <= tick) {

            tier1.offer(pendingProcesses.poll());
        }
    }

    protected Process getNextProcess() {
        if (!tier1.isEmpty()) {
            isPreemptiveProcess = true;
            return tier1.poll();
        }

        if (!tier2.isEmpty()) {
            isPreemptiveProcess = false;
            return tier2.poll();
        }

        return null;
    }

    protected void executeTick() {
        runningProcess.decreaseRemainingTime();
        runningQuantum--;
    }

    protected void updateWaitingProcesses() {
        boolean priorityChanged = false;

        for (Process process : tier2.snapshot()) {
            int waitingTime = tick - demotionTimes.get(process);

            if (waitingTime > 0 && waitingTime % AGING_INTERVAL == 0) {
                process.setPriority(process.getPriority() - 1);
                priorityChanged = true;
            }
        }

        if (priorityChanged) {
            tier2.reorder();
        }
    }

    protected boolean isCompleted() {
        return runningProcess.getRemainingTime() == 0;
    }

    protected void completeProcess() {
        completedProcesses.offer(runningProcess);
    }

    protected boolean shouldPreempt() {
        return isPreemptiveProcess && runningQuantum == 0;
    }

    protected void preemptProcess() {
        demotionTimes.put(runningProcess, tick);

        tier2.offer(runningProcess);

        demotedProcesses.add(new DemotionSnapshot(
                runningProcess.getProcessId(),
                "Tier 1",
                "Tier 2"
        ));
    }

    private void makeSwitching() {
        runningProcess = null;
        switchingStartedAt = tick;
        status = SchedulerStatus.SWITCHING;
    }

    private void takeSnapshot() {
        snapshotBuffer.publish(createSnapshot());
    }

    protected SchedulerSnapshot createSnapshot() {
        return createSnapshot(null);
    }

    protected SchedulerSnapshot createSnapshot(SimulationResult result) {
        QueueSnapshot arrivingSnapshot = new QueueSnapshot(
                "0",
                "Tier 1",
                pendingProcesses.snapshot()
        );

        QueueSnapshot tier1Snapshot = new QueueSnapshot(
                "1",
                "Tier 1",
                tier1.snapshot()
        );

        QueueSnapshot tier2Snapshot = new QueueSnapshot(
                "2",
                "Tier 2",
                tier2.snapshot()
        );

        return new SchedulerSnapshot(
                tick,
                status,
                runningProcess,
                isPreemptiveProcess ? "Tier 1" : "Tier 2",
                List.of(arrivingSnapshot, tier1Snapshot, tier2Snapshot),
                demotedProcesses,
                result
        );
    }

    protected SchedulerSnapshot createFinalSnapshot(SimulationResult result) {
        status = SchedulerStatus.COMPLETED;
        return createSnapshot(result);
    }

    protected SimulationResult createSimulationResult() {
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
        return "Adaptive Container Scheduler";
    }
}
