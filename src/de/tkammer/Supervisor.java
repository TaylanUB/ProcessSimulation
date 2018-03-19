package de.tkammer;

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

    private final AtomicLong totalIdleTime = new AtomicLong();

    public void start() {
        try {
            waitDurationsWriter = new OutputStreamWriter(new FileOutputStream(Config.WaitDurationsFile));
            execDurationsWriter = new OutputStreamWriter(new FileOutputStream(Config.ExecDurationsFile));
            idleDurationsWriter = new OutputStreamWriter(new FileOutputStream(Config.IdleDurationsFile));

            waitDurationsWriter.write("Priority;WaitDuration\n");
            execDurationsWriter.write("Priority;ExecDuration;Cost\n");
            idleDurationsWriter.write("CPU;IdleTime\n");
        } catch (IOException exception) {
            throw new RuntimeException("Could not initialize Supervisor.", exception);
        }

        startTime = Instant.now();
    }

    public void recordDispatch(Processor processor, Process process) {
        process.setDispatchTime();
        int len = processor.getQueueLength();
        if (processor.getCurrentProcess() != null) {
            len += 1;
        }
        System.out.printf("%s was assigned %s, has %d processes to wait for.\n", processor, process, len);

        if (len == 0 && processor.getStartIdleTime() != null) {
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
    }

    public void recordProcessStart(Processor processor, Process process) {
        process.setStartTime();
        long duration = Duration.between(process.getDispatchTime(), process.getStartTime()).toMillis();
        System.out.printf("%s started %s after %d ms.\n", processor, process, duration);

        try {
            waitDurationsWriter.write(String.format("%s;%d\n", process.getPriority(), duration));
        } catch (IOException exception) {
            throw new RuntimeException("Write failed.", exception);
        }
    }

    public void recordProcessEnd(Processor processor, Process process) {
        process.setEndTime();
        long duration = Duration.between(process.getStartTime(), process.getEndTime()).toMillis();
        int len = processor.getQueueLength();
        System.out.printf("%s finished %s after %d ms, leaving %d processes.\n", processor, process, duration, len);

        try {
            execDurationsWriter.write(String.format("%d;%d;%d\n", process.getPriority(), duration, process.getCost()));
        } catch (IOException exception) {
            throw new RuntimeException("Write failed.", exception);
        }

        if (len == 0) {
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

        Duration runTime = Duration.between(startTime, Instant.now());
        System.out.printf("Finished %d processes (cost %d) in %d ms, idle time %d ms.\n",
                processCount, totalProcessCost, runTime.toMillis(), totalIdleTime.get());
    }
}
