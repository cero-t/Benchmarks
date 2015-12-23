package ninja.cero.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class Main {
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + JmxBenchmark.class.getSimpleName() + ".*")
                .warmupIterations(10)
                .measurementIterations(5)
                .forks(2)
                .build();
        new Runner(opt).run();
    }
}
