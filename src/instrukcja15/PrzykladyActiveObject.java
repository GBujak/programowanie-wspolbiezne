package instrukcja15;

import javax.xml.stream.events.EntityReference;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.*;

abstract class MyBaseActiveObject {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected <T> Future<T> submit(Callable<T> callable) {
        return executor.submit(callable);
    }
    public void shutdown() { executor.shutdown(); }
}

class WindaSynchronized {
    final static int WAIT_TIME = 500; // Milliseconds

    public synchronized int call(int floor) {
        try {
            TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
            return floor;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}

class WindaActiveObject extends MyBaseActiveObject {
    final static int WAIT_TIME = 500; // Milliseconds

    public Future<Integer> call(int floor) {
        return this.submit(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(WAIT_TIME);
                return floor;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        });
    }
}

public class PrzykladyActiveObject {
    public static void main(String[] args) {
        var windaSynchronized = new WindaSynchronized();

        for (int i = 0; i < 5; i++) {
            var floor = i;
            new Thread(() -> {
                windaSynchronized.call(floor);
                System.out.println("Winda przyjechała");
            }).start();
        }

        var windaActive = new WindaActiveObject();

        for (int i = 0; i < 5; i++) {
            var floor = i;
            new Thread(() -> {
                try {
                    windaActive.call(floor).get();
                    System.out.println("Winda active object przyjechała");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            TimeUnit.SECONDS.sleep(5);
            windaActive.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

