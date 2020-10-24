package instrukcja13;

import java.util.concurrent.Phaser;

class PhasePrinter implements Runnable {
    private Phaser phaser;
    private int phaseCount;

    public PhasePrinter(Phaser phaser, int phaseCount) {
        this.phaser = phaser;
        this.phaseCount = phaseCount;

        phaser.register();
    }

    @Override
    public void run() {
        for (int i = 1; i <= phaseCount; i++) {
            System.out.println(
                    Thread.currentThread().getId() + ": " + i);
            phaser.arriveAndAwaitAdvance();
        }
        phaser.arriveAndDeregister();
    }
}

public class PrzykladPhaser {
    public static void main(String[] args) {
        var phaser = new Phaser(0);
        for (int i = 0; i < 4; i++)
            new Thread(new PhasePrinter(phaser, 4)).start();

        // 15: 1
        // 13: 1
        // 14: 1
        // 16: 1
        // 14: 2
        // 15: 2
        // 13: 2
        // 16: 2
        // 16: 3
        // 13: 3
        // 15: 3
        // 14: 3
        // 14: 4
        // 13: 4
        // 16: 4
        // 15: 4
    }
}
