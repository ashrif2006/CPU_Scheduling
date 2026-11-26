package simulation;

import java.util.List;
import model.ProcessResult;

public class SimulationResult {

    public final List<ProcessResult> processes;

    public SimulationResult(List<ProcessResult> processes) {
        this.processes = List.copyOf(processes);
    }

    public double averageWaitingTime() {
        return processes.stream()
                .mapToInt(ProcessResult::waitingTime)
                .average()
                .orElse(0.0);
    }

    public double averageTurnaroundTime() {
        return processes.stream()
                .mapToInt(ProcessResult::turnaroundTime)
                .average()
                .orElse(0.0);
    }
}
