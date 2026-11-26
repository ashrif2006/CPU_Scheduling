package model;

import java.util.List;

public class SchedulerConfiguration {

    private int contextSwitchTime;
    private int timeQuantum;
    private List<Process> processes;

    public SchedulerConfiguration(int cs, int tq, List<Process> p) {
        contextSwitchTime = cs;
        timeQuantum = tq;
        processes = List.copyOf(p);
    }

    public int getContextSwitchTime() {
        return contextSwitchTime;
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }

    public List<Process> getProcesses() {
        return processes;
    }

}
