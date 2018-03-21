package de.tkammer.procsim;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class Supervisor {
    private Writer waitDurationsWriter;
    private Writer execDurationsWriter;
    private Writer idleDurationsWriter;

    private long processCount;
    private long totalProcessCost;
    private Instant startTime;
    private long longestQueueLength;
    private long totalQueueLength;
    private long longestWaitTime;
    private long totalWaitTime;
    private final AtomicLong totalIdleTime = new AtomicLong();

    private final Config config;

    public Supervisor(Config config) {
        this.config = config;
    }

    public void start() {
        try {
            waitDurationsWriter = new OutputStreamWriter(new FileOutputStream(config.getWaitDurationsFile()));
            execDurationsWriter = new OutputStreamWriter(new FileOutputStream(config.getExecDurationsFile()));
            idleDurationsWriter = new OutputStreamWriter(new FileOutputStream(config.getIdleDurationsFile()));

            waitDurationsWriter.write("Priority;WaitDuration\n");
            execDurationsWriter.write("Priority;Cost;ExecDuration\n");
            idleDurationsWriter.write("CPU;IdleTime\n");
        } catch (IOException exception) {
            throw new RuntimeException("Could not initialize Supervisor.", exception);
        }

        startTime = Instant.now();
    }

    public void recordDispatch(Processor processor, Process process) {
        process.setDispatchTime();

        int queueLength = processor.getQueueLength();
        if (processor.getCurrentProcess() != null) {
            queueLength += 1;
        }

        if (queueLength == 0 && processor.getStartIdleTime() != null) {
            Duration duration = Duration.between(processor.getStartIdleTime(), Instant.now());
            try {
                idleDurationsWriter.write(String.format("%s;%d\n", processor.getId(), duration.toMillis()));
            } catch (IOException exception) {
                throw new RuntimeException("Write failed.", exception);
            }

            totalIdleTime.addAndGet(duration.toMillis());
        }

        processCount += 1;
        totalProcessCost += process.getCost();
        longestQueueLength = Math.max(longestQueueLength, queueLength);
        totalQueueLength += queueLength;
    }

    public void recordProcessStart(Processor processor, Process process) {
        process.setStartTime();

        int priority = process.getPriority();
        long waitDuration = Duration.between(process.getDispatchTime(), process.getStartTime()).toMillis();

        try {
            waitDurationsWriter.write(String.format("%s;%d\n", priority, waitDuration));
        } catch (IOException exception) {
            throw new RuntimeException("Write failed.", exception);
        }

        longestWaitTime = Math.max(longestWaitTime, waitDuration);
        totalWaitTime += waitDuration;
    }

    public void recordProcessEnd(Processor processor, Process process) {
        process.setEndTime();

        int priority = process.getPriority();
        int cost = process.getCost();
        long execDuration = Duration.between(process.getStartTime(), process.getEndTime()).toMillis();

        try {
            execDurationsWriter.write(String.format("%d;%d;%d\n", priority, cost, execDuration));
        } catch (IOException exception) {
            throw new RuntimeException("Write failed.", exception);
        }

        if (processor.getQueueLength() == 0) {
            processor.setStartIdleTime();
        }
    }

    public void finish() {
        try {
            waitDurationsWriter.close();
            execDurationsWriter.close();
            idleDurationsWriter.close();
        } catch (IOException exception) {
            throw new RuntimeException("Couldn't close writers.", exception);
        }

        System.out.printf("Using %s:\n", config.getDispatcherClass().getSimpleName());

        Duration runTime = Duration.between(startTime, Instant.now());
        System.out.printf("Finished %d processes (cost %d) in %d ms, idle time %d ms.\n",
                processCount, totalProcessCost, runTime.toMillis(), totalIdleTime.get());

        int averageQueueLength = (int) (totalQueueLength * 1.0 / processCount);
        System.out.printf("Longest queue length %d, average %d.\n", longestQueueLength, averageQueueLength);

        int averageWaitTime = (int) (totalWaitTime * 1.0 / processCount);
        System.out.printf("Longest wait time %d ms, average %d ms.\n", longestWaitTime, averageWaitTime);
    }
}
