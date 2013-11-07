package jp.co.acroquest.benchmark;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
public class ArrayListBenchmark {
    private static final int size = 100;

    private List<Integer> sizedList;
    private List<Integer> defaultList;

    public static void main(String[] args) {
        Main.main("-i 3 -wi 3 -f 1".split(" "));
    }

    @GenerateMicroBenchmark
    public void withInitialSize() {
        sizedList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            sizedList.add(i);
        }
    }

    @GenerateMicroBenchmark
    public void withoutInitialSize() {
        defaultList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            defaultList.add(i);
        }
    }
}