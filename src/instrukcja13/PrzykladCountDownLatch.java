package instrukcja13;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

class ConcurrentMapper<T, R> {
    private Function<T, R> function;
    private List<T> arguments;
    private List<R> results;
    private CountDownLatch latch;

    public ConcurrentMapper(Function<T, R> function, List<T> arguments) {
        this.function = function;
        this.arguments = arguments;
        this.results = new ArrayList<>(arguments.size());
        for (int i = 0; i < arguments.size(); i++) results.add(null);
        this.latch = new CountDownLatch(arguments.size());
    }

    public List<R> map() {

        for (int i = 0; i < arguments.size(); i++) {
            var current = i;
            new Thread(() -> {
                results.set(current, function.apply(arguments.get(current)));
                latch.countDown();
            }).start();
        }

        try {
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}

public class PrzykladCountDownLatch {
    public static void main(String[] args) {
        var arguments = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        var mapper = new ConcurrentMapper<Integer, Integer>(x -> x * x, arguments);
        System.out.println(mapper.map());
        // [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]
    }
}
