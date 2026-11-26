package model;

import java.util.List;

public class QueueSnapshot {

    public String id;
    public String name;
    public List<Process> processes;

    public QueueSnapshot(
            String id,
            String name,
            List<Process> processes
    ) {
        this.id = id;
        this.name = name;
        this.processes = processes.stream().map(Process::new).toList();
    }

}
