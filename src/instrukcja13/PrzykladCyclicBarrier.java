package instrukcja13;

import java.util.concurrent.CyclicBarrier;

class MyMessageChannel<T> {
    private CyclicBarrier barrier = new CyclicBarrier(2);
    private T message = null;

    public void send(T message) {
        synchronized (this) {
            if (this.message != null) throw new IllegalStateException("sending on full channel");
            this.message = message;
        }

        try {
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public T receive() {
        try {
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (this) { return this.message; }
    }
}

public class PrzykladCyclicBarrier {
    public static void main(String[] args) {
        var chan = new MyMessageChannel<String>();
        new Thread(() -> System.out.println(chan.receive())).start();
        try {
            Thread.sleep(1000, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        chan.send("Hello world");
        // Hello world
    }
}
