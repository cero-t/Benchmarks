package jp.co.acroquest.benchmark;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;

import java.util.ArrayList;
import java.util.List;

public class ArrayListBenchmark {
    private static final int size = 100;

    @GenerateMicroBenchmark
    public void withInitialSize() {
        List<Integer> sizedList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            sizedList.add(i);
        }
    }

    @GenerateMicroBenchmark
    public void withoutInitialSize() {
        List<Integer> defaultList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            defaultList.add(i);
        }
    }

    public static void main(String[] args) {
        Main.main("-i 3 -wi 3 -f 1".split(" "));
    }
}