package model;

public class ProcessResult {

    public String processId;
    public int arrivalTime;
    public int burstTime;
    public int completionTime;

    public ProcessResult(
            String processId,
            int arrivalTime,
            int burstTime,
            int completionTime
    ) {
        this.processId = processId;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.completionTime = completionTime;
    }

    public int turnaroundTime() {
        return completionTime - arrivalTime;
    }

    public int waitingTime() {
        return turnaroundTime() - burstTime;
    }
}
