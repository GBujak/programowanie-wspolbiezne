package instrukcja15;

import java.io.File;
import java.security.cert.CollectionCertStoreParameters;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

class ForkJoinDiskUsage extends RecursiveTask<Long> {
    private File rootFile;

    public ForkJoinDiskUsage(File rootFile) {
        this.rootFile = rootFile;
    }

    @Override
    protected Long compute() {
        if (rootFile.isFile()) return rootFile.length();
        var sum = 0L;
        var subFiles = List.of(rootFile.listFiles());
        var subDirs = new ArrayList<File>();
        for (var file : subFiles) {
            if (file.isDirectory()) {
                subDirs.add(file);
                continue;
            }
            sum += file.length();
        }

        var subResults = subDirs.stream()
                .map(it -> new ForkJoinDiskUsage(it).fork())
                .collect(Collectors.toList());

        for (var result : subResults) sum += result.join();
        return sum;
    }
}

class ForkJoinTableSort extends RecursiveTask<List<Integer>> {
    private List<Integer> input;

    public ForkJoinTableSort(List<Integer> input) {
        this.input = input;
    }

    @Override
    protected List<Integer> compute() {
        if (input.size() == 0) throw new IllegalArgumentException("Podano pustą listę");
        if (input.size() == 1) return input;
        if (input.size() == 2)
            if (input.get(0) > input.get(1))
                return List.of(input.get(1), input.get(0));
            else return input;

        var subFutures = List.of(
                new ForkJoinTableSort(input.subList(0, input.size() / 2)),
                new ForkJoinTableSort(input.subList(input.size() / 2, input.size()))
        ).stream().map(ForkJoinTask::fork).collect(Collectors.toList());

        var subResults = subFutures.stream()
                .map(ForkJoinTask::join)
                .map(ArrayDeque::new)
                .collect(Collectors.toList());

        var left = subResults.get(0);
        var right = subResults.get(1);
        var result = new ArrayList<Integer>();

        while (true) {
            // Nie sprawdzam null, bo w wynikach nigdy nie będzie pustej listy
            if (left.peekFirst() < right.peekFirst())
                result.add(left.pollFirst());
            else result.add(right.pollFirst());

            if (left.size() == 0) {
                result.addAll(right);
                return result;
            }

            if (right.size() == 0) {
                result.addAll(left);
                return result;
            }
        }
    }
}

public class PrzykladyForkJoin {
    public static void main(String[] args) {
        var forkJoinPool = new ForkJoinPool();
        var filesize = forkJoinPool.invoke(new ForkJoinDiskUsage(new File(".")));
        System.out.println("Filesize: " + filesize / 1_000 + "KB");

        var random = new Random();
        var inputList = new ArrayList<Integer>();
        for (int i = 0; i < 20; i++) inputList.add(random.nextInt(100));

        var sorted = forkJoinPool.invoke(new ForkJoinTableSort(inputList));
        System.out.println("Sorted: " + sorted);
    }
}
