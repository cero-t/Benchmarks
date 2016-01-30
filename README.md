Benchmarks
==========

Benchmarks using JMH

## ArrayListBenchmark.java

Performance comparison between ArrayList with initial size and without it.


## JmxBenchmark.java
Benchmarks of getting information from MXBeans, mainly focus on
caching effects of VirutualMachine, JmxConnection and MXBean instances.

### Result:

|Benchmark                              |Mode |Cnt|Score   |Error    |Units|
|---------------------------------------|-----|---|--------|---------|-----|
|JmxBenchmark.no1_vmAttach|thrpt|10 |653.834 |± 108.790|ops/s|
|JmxBenchmark.no2_vmCachedGetProperties|thrpt|10 |2330.529|± 270.268|ops/s|
|JmxBenchmark.no3_vmCachedGetConnector|thrpt|10|612.652|± 95.547|ops/s|
|JmxBenchmark.no4_connectorCachedGetMBeanCount|thrpt|10|9047.803|± 1480.741|ops/s|
|JmxBenchmark.no5_connectorCachedThreadCount|thrpt|10|8199.887|± 1269.164|ops/s|
|JmxBenchmark.no6_connectorCachedGetThreadCount|thrpt|10|2662.147|± 585.627|ops/s|
|JmxBenchmark.no7_beanCachedGetThreadCount|thrpt|10|8148.967|± 1705.518|ops/s|
