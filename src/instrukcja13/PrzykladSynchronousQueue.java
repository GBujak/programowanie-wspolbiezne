package instrukcja13;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Function;

class UnorderedConcurrentMapper<T, R> {
    private SynchronousQueue<R> queue = new SynchronousQueue<>();
    private Function<T, R> function;
    private List<T> args;
    private List<R> results;

    public UnorderedConcurrentMapper(Function<T, R> function, List<T> args) {
        this.function = function;
        this.args = args;
        this.results = new ArrayList<>(args.size());
    }

    public List<R> map() throws InterruptedException {
        for (var i : args) new Thread(() -> {
            try {
                queue.put(function.apply(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        for (int i = 0; i < args.size(); i++)
            results.add(queue.take());

        return results;
    }
}

public class PrzykladSynchronousQueue {
    public static void main(String[] args) throws Exception {
        var arguments = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Function<Integer, String> function = x -> "Hello " + x;
        var mapper = new UnorderedConcurrentMapper<>(function, arguments);
        System.out.println(mapper.map());

        // [Hello 2, Hello 5, Hello 7, Hello 3, Hello 9, Hello 6, Hello 10, Hello 4, Hello 1, Hello 8]
    }
}
