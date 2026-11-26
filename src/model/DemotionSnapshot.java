package model;

public class DemotionSnapshot {

    public String processId;
    public String sourceQueue;
    public String destinationQueue;

    public DemotionSnapshot(
            String processId,
            String sourceQueue,
            String destinationQueue
    ) {
        this.processId = processId;
        this.sourceQueue = sourceQueue;
        this.destinationQueue = destinationQueue;
    }
}
