package scheduler;

import java.util.List;
import model.Process;
import simulation.SimulationResult;

public class SJFScheduler extends Scheduler {

    @Override
    public SimulationResult simulate(
            List<Process> processes,
            int quantum,
            int contextSwitch) {

        return null;
    }

    @Override
    public String getName() {
        return "Adaptive Container Scheduler";
    }
}
