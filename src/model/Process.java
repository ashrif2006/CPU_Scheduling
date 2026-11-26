package model;

public class Process {

    private String processId;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int remainingTime;
    private int completedAt;
    private boolean completed;

    public Process(String processId, int arrivalTime,
            int burstTime, int priority) {

        this.processId = processId;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        completed = false;
        completedAt = -1;
    }

    Process(Process source) {
        this(source.processId, source.arrivalTime, source.burstTime, source.priority);
        remainingTime = source.remainingTime;
        completedAt = source.completedAt;
        completed = source.completed;
    }

    public String getProcessId() {
        return processId;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void decreaseRemainingTime() {
        --remainingTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void makeCompleted(int tick) {
        completed = true;
        completedAt = tick;
    }

    public int getCompletionTime() {
        return completedAt;
    }

    public void setPriority(int p) {
        priority = p;
    }
}
