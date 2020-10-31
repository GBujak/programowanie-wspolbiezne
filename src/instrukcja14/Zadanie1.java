package instrukcja14;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.stream.Collectors;

class VariableCancellable implements Runnable {
    private List<Integer> data;
    private boolean isCancelled = false;

    public VariableCancellable(List<Integer> data) {
        this.data = data;
    }

    public void cancel() { isCancelled = true; }

    @Override
    public void run() {
        try {
            int i;
            for (i = 0; i < data.size() && !isCancelled; i++) {
                data.set(i, data.get(i) * 2);
                System.out.println("state of CancelVariable: " + data);
                Thread.sleep(200, 0);
            }
            if (isCancelled) System.out.println("CancelVariable was cancelled on iteration " + i);
        } catch (Exception e) {
            System.out.println("CancelVariable was cancelled by Thread.interrupt");
        }
    }
}

class FutureCancellable {
    private FutureTask<List<Integer>> listFuture;

    public FutureCancellable(List<Integer> data) {
        listFuture = new FutureTask<>(() -> {
            try {
                for (int i = 0; i < data.size(); i++) {
                    Thread.sleep(200, 0);
                    data.set(i, data.get(i) * 2);
                    System.out.println("FutureCancellable state: " + data);
                }
            } catch (Exception e) {
                System.out.println("FutureCancellable cancelled.");
            }
            return data;
        });
        new Thread(listFuture).start();
    }

    public void cancel() {
        listFuture.cancel(true);
    }
}

class ItemOrPoison {
    boolean isPoison = true;
    Integer item = null;

    public ItemOrPoison() {}
    public ItemOrPoison(Integer item) {
        this.item = item;
        this.isPoison = false;
    }

    @Override
    public String toString() {
        return (isPoison) ? "poison" : item.toString();
    }
}

class PoisonPillCancellable implements Runnable {
    private List<ItemOrPoison> data;

    public PoisonPillCancellable(List<ItemOrPoison> data) {
        this.data = data;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < data.size(); i++) {
                var current = data.get(i);
                if (current.isPoison) {
                    System.out.println("Cancelled by poison pill");
                    return;
                }
                Thread.sleep(200, 0);
                current.item *= 2;
                System.out.println("PoisonPillCancellable state: " + data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Zadanie1 {
    public static void main(String[] args) throws Exception {
        var data = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var variableCancellable = new VariableCancellable(new ArrayList<>(data));
        new Thread(variableCancellable).start();
        Thread.sleep(1000, 0);
        variableCancellable.cancel();

        // Przerywanie za pomocą Thread.interrupt
        variableCancellable = new VariableCancellable(new ArrayList<>(data));
        var cancelThread = new Thread(variableCancellable);
        cancelThread.start();
        Thread.sleep(1000, 0);
        cancelThread.interrupt();

        var futureCancellable = new FutureCancellable(new ArrayList<>(data));
        Thread.sleep(1000, 0);
        futureCancellable.cancel();

        var poisonData = data.stream()
                .map(ItemOrPoison::new)
                .collect(Collectors.toList());
        poisonData.set(5, new ItemOrPoison());

        var poisonPillCancellable = new PoisonPillCancellable(poisonData);
        new Thread(poisonPillCancellable).start();
    }
}
