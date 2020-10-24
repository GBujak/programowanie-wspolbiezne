package instrukcja13;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

class MyConsumer implements Runnable {
    private List<Integer> buffer = new ArrayList<>();
    private Exchanger<List<Integer>> exchanger;

    public MyConsumer(Exchanger<List<Integer>> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100 / 20; i++) {
            try {
                buffer = exchanger.exchange(buffer);
                System.out.println(buffer);
                buffer.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class MyProducer implements Runnable {
    private List<Integer> buffer = new ArrayList<>();
    private Exchanger<List<Integer>> exchanger;

    public MyProducer(Exchanger<List<Integer>> exchanger) {
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 100; i++) {
            try {
                Thread.sleep(50, 0);
                buffer.add(i);
                if (buffer.size() == 20)
                    buffer = exchanger.exchange(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

public class PrzykladExchanger {
    public static void main(String[] args) {
        var exchanger = new Exchanger<List<Integer>>();
        new Thread(new MyConsumer(exchanger)).start();
        new Thread(new MyProducer(exchanger)).start();

        // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
        // [21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40]
        // [41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60]
        // [61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80]
        // [81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100]
    }
}
