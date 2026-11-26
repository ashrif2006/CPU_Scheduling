package utils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class OrderedQueue<T> {

    private final LinkedList<T> queue;
    private final Comparator<? super T> comparator;

    public OrderedQueue(Comparator<? super T> comparator) {
        this.queue = new LinkedList<>();
        this.comparator = Objects.requireNonNull(comparator);
    }

    public void offer(T item) {
        Objects.requireNonNull(item);

        ListIterator<T> iterator = queue.listIterator();

        while (iterator.hasNext()) {
            T current = iterator.next();

            if (comparator.compare(item, current) < 0) {
                iterator.previous();
                iterator.add(item);
                return;
            }
        }

        queue.addLast(item);
    }

    public T poll() {
        return queue.pollFirst();
    }

    public T peek() {
        return queue.peekFirst();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }

    public List<T> snapshot() {
        return List.copyOf(queue);
    }

    public void reorder() {
        queue.sort(comparator);
    }
}
