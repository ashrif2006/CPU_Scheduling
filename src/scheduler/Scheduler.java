package scheduler;

import org.w3c.dom.stylesheets.LinkStyle;
import simulation.SimulationResult;
import model.Process;
import java.util.List;

public abstract class Scheduler {

    public abstract SimulationResult simulate(
            List<Process> processes,
            int quantum,
            int contextSwitch
    );

    public abstract String getName();
}
