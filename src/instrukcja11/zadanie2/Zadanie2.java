package instrukcja11.zadanie2;

import java.util.List;

class Synch {
    private volatile long val;
    private volatile boolean changed = false;

    public Synch(Long val) {
        this.val = val;
    }

    public long readNext() {
        synchronized (this) {
            while (!changed) try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
            changed = false;
            notify();
            return val;
        }
    }

    public synchronized void increment() {
        while (changed) try {
            wait();
        } catch (Exception e) {
            e.printStackTrace();
        }
        val++;
        changed = true;
        notify();
    }
}

public class Zadanie2 {
    public static void main(String[] args) {
        var synch = new Synch(0L);

        var readThread = new Thread(() -> {
            for (int i = 0; i < 10; i++)
                System.out.println(synch.readNext());
        });
        var writeThread = new Thread(() -> {
            for (int i = 0; i < 10; i++)
                synch.increment();
        });

        List.of(readThread, writeThread).forEach(Thread::start);
    }
}
