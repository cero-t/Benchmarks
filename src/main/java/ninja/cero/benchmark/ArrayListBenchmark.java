package ninja.cero.benchmark;

import org.openjdk.jmh.annotations.Benchmark;

import java.util.ArrayList;
import java.util.List;

public class ArrayListBenchmark {
    private static final int size = 1000000;

    @Benchmark
    public void withInitialSize() {
        List<Integer> sizedList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            sizedList.add(i);
        }
    }

    @Benchmark
    public void withoutInitialSize() {
        List<Integer> defaultList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            defaultList.add(i);
        }
    }
}
