package simulation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import model.SchedulerSnapshot;

public final class SnapshotBuffer {

    private final BlockingQueue<SchedulerSnapshot> buffer;

    public SnapshotBuffer() {
        buffer = new LinkedBlockingQueue<>();
    }

    public void publish(SchedulerSnapshot snapshot) {
        buffer.add(snapshot);
    }

    public SchedulerSnapshot consume() throws InterruptedException {
        return buffer.take();
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    public int size() {
        return buffer.size();
    }

    public void finish(SchedulerSnapshot finalSnapshot) {
        buffer.add(finalSnapshot);
    }
}
