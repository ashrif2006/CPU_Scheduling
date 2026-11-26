package model;

import java.util.List;
import scheduler.SchedulerStatus;
import simulation.SimulationResult;

public class SchedulerSnapshot {

    public int tick;
    public SchedulerStatus status;
    public Process runningProcess;
    public String runningQueueId;
    public List<QueueSnapshot> queues;
    public List<DemotionSnapshot> demotions;
    public SimulationResult result;

    public SchedulerSnapshot(
            int tick,
            SchedulerStatus status,
            Process runningProcess,
            String runningQueueId,
            List<QueueSnapshot> queues,
            List<DemotionSnapshot> demotions,
            SimulationResult result
    ) {
        this.tick = tick;
        this.status = status;
        this.runningProcess = runningProcess == null ? null : new Process(runningProcess);
        this.runningQueueId = runningQueueId;
        this.queues = List.copyOf(queues);
        this.demotions = List.copyOf(demotions);
        this.result = result;

    }

    public boolean isFinal() {
        return result != null;
    }
}
