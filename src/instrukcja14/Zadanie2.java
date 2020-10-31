package instrukcja14;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

interface MyConcurrentInterface {
    int read();
    void write(int value);
}

class MyAtomicConcurrent implements MyConcurrentInterface {
    private AtomicInteger value = new AtomicInteger(0);

    @Override
    public int read() {
        return value.get();
    }

    @Override
    public void write(int value) {
        this.value.set(value);
    }
}

class MyLockConcurrent implements MyConcurrentInterface {
    private Lock lock = new ReentrantLock();
    private int value;

    @Override
    public int read() {
        int ret;
        lock.lock();
        ret = value;
        lock.unlock();
        return ret;
    }

    @Override
    public void write(int value) {
        lock.lock();
        this.value = value;
        lock.unlock();
    }
}

class MyReadWriteLockConcurrent implements MyConcurrentInterface {
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private int value;

    @Override
    public int read() {
        int ret;
        readWriteLock.readLock().lock();
        ret = value;
        readWriteLock.readLock().unlock();
        return ret;
    }

    @Override
    public void write(int value) {
        readWriteLock.writeLock().lock();
        this.value = value;
        readWriteLock.writeLock().unlock();
    }
}

public class Zadanie2 {
    static void testConcurrent(MyConcurrentInterface concurrent) throws Exception {
        var writeThread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                concurrent.write(i);
            }
        });
        var readThread = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                concurrent.read();
            }
        });
        var readThread2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                concurrent.read();
            }
        });

        var before = Instant.now();
        var threads = List.of(writeThread, readThread, readThread2);
        for (var t : threads) t.start();
        for (var t : threads) t.join();
        var after = Instant.now();

        System.out.println("time: " + ChronoUnit.MICROS.between(before, after) + " microseconds");
    }

    public static void main(String[] args) throws Exception {
        var concurrents = List.of(
                new MyAtomicConcurrent(),
                new MyLockConcurrent(),
                new MyReadWriteLockConcurrent()
        );
        for (var t : concurrents) testConcurrent(t);
    }
}

