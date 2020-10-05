package instrukcja12;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

class Lokator implements Runnable {
    private int id;
    private int zgloszenieId = 0;
    private long frequency;
    private int probability;
    private Konserwator konserwator;
    private int current = 0;

    public Lokator(int id, long frequency, int probability, Konserwator konserwator) {
        this.id = id;
        this.frequency = frequency;
        this.probability = probability;
        this.konserwator = konserwator;
    }

    @Override
    public void run() {
        var random = new Random();
        while (true) {
            try {
                Thread.sleep(frequency, 0);
                if (random.nextInt() % 100 + 1 <= probability) {
                    konserwator.put(new Integer[]{id, zgloszenieId});
                    zgloszenieId++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}

class Konserwator implements Runnable {
    private BlockingQueue<Integer[]> queue = new ArrayBlockingQueue<>(500);
    private long repairTime;

    public Konserwator(long repairTime) {
        this.repairTime = repairTime;
    }

    public void put(Integer[] zgloszenie) {
        try {
            queue.put(zgloszenie);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        while (true) {
            int lokatorId, zgloszenieId;
            try {
                var i = queue.take();
                lokatorId = i[0];
                zgloszenieId = i[1];
                Thread.sleep(repairTime, 0);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            System.out.println("Naprawiono zgłoszenie "
                    + zgloszenieId + " od lokatora " + lokatorId +
                    " w kolejce " + queue.size());
        }
    }
}

public class Zadanie2 {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);

        System.out.println("Podaj częstotliwość, prawdopodobieństwo awarii, ilość lokatorów, czas naprawy");
        long frequency = scanner.nextLong();
        int probability = scanner.nextInt();
        int count = scanner.nextInt();
        long time = scanner.nextLong();

        var konserwator = new Konserwator(time);

        List<Lokator> lokatorzy = new ArrayList<>();
        for (int i = 0; i < count; i++)
            lokatorzy.add(new Lokator(i + 1, frequency, probability, konserwator));

        var konserwatorThread = new Thread(konserwator);
        var lokatorThreads = lokatorzy
                .stream()
                .map(Thread::new)
                .peek(it -> it.setDaemon(true))
                .collect(Collectors.toList());

        konserwatorThread.setDaemon(true);

        konserwatorThread.start();
        lokatorThreads.forEach(Thread::start);

        scanner.nextLine();
        scanner.nextLine();
    }
}
