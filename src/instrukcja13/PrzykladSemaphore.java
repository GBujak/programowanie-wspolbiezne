package instrukcja13;

import java.util.concurrent.Semaphore;

public class PrzykladSemaphore {
    public static void main(String[] args) {
        var semaphore = new Semaphore(2);
        for (int i = 0; i < 20; i++)
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getId() + " entered...");
                    Thread.sleep(200, 0);
                    System.out.println(Thread.currentThread().getId() + " exiting...");
                    semaphore.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        // 15 entered...
        // 13 entered...
        // 13 exiting...
        // 17 entered...
        // 15 exiting...
        // 14 entered...
        // 17 exiting...
        // 16 entered...
        // 14 exiting...
        // 18 entered...
        // 16 exiting...
        // 19 entered...
        // 18 exiting...
        // 20 entered...
        // 19 exiting...
        // 21 entered...
        // 20 exiting...
        // 22 entered...
        // 21 exiting...
        // 23 entered...
        // 22 exiting...
        // 24 entered...
        // 23 exiting...
        // 25 entered...
        // 24 exiting...
        // 26 entered...
        // 25 exiting...
        // 26 exiting...
        // 27 entered...
        // 28 entered...
        // 27 exiting...
        // 28 exiting...
        // 29 entered...
        // 30 entered...
        // 29 exiting...
        // 30 exiting...
        // 31 entered...
        // 32 entered...
        // 31 exiting...
        // 32 exiting...
    }
}
