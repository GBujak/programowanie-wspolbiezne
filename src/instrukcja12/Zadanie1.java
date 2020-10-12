package instrukcja12;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

class MySynchronizedSet<T> implements Set<T> {
    private Set<T> set = new HashSet<>();

    // Niezaimplementowane metody interfejsu Set

    @Override
    public Spliterator<T> spliterator() { throw new IllegalStateException("unimplemented"); }
    @Override
    public int size() { throw new IllegalStateException("unimplemented"); }
    @Override
    public boolean isEmpty() { throw new IllegalStateException("unimplemented"); }
    @Override
    public Iterator<T> iterator() { throw new IllegalStateException("unimplemented"); }
    @Override
    public Object[] toArray() { throw new IllegalStateException("unimplemented"); }
    @Override
    public <T1> T1[] toArray(T1[] t1s) { throw new IllegalStateException("unimplemented"); }
    @Override
    public boolean remove(Object o) { throw new IllegalStateException("unimplemented"); }
    @Override
    public boolean containsAll(Collection<?> collection) { throw new IllegalStateException("unimplemented"); }
    @Override
    public boolean addAll(Collection<? extends T> collection) { throw new IllegalStateException("unimplemented"); }
    @Override
    public boolean retainAll(Collection<?> collection) { throw new IllegalStateException("unimplemented"); }
    @Override
    public boolean removeAll(Collection<?> collection) { throw new IllegalStateException("unimplemented"); }
    @Override
    public void clear() { throw new IllegalStateException("unimplemented"); }

    @Override
    public synchronized boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public synchronized boolean add(T t) {
        return set.add(t);
    }

    @Override
    public String toString() {
        return set.toString();
    }
}

public class Zadanie1 {
    public static void main(String[] args) {
        List<Set<Integer>> sets = List.of(
                new MySynchronizedSet<Integer>(),
                Collections.synchronizedSet(new HashSet<Integer>()),
                new ConcurrentSkipListSet<Integer>(),
                new CopyOnWriteArraySet<Integer>()
        );

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int threadId = i;
            threads.add(new Thread(() -> {
                for (int j = 0; j < 20; j++) {
                    System.out.println("Thread " + threadId + " adding " + j);
                    var next = j;
                    sets.forEach(it -> it.add(next));
                    System.out.println("State for thread " + threadId + ": " + sets);
                }
            }));
        }

        threads.forEach(Thread::start);
    }
}
