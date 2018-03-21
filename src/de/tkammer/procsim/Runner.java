package de.tkammer.procsim;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Runner {
    private final Config config;
    private final Random random;

    public Runner(Config config) {
        this.config = config;
        random = new Random(config.processGenerationSeed);
    }

    public void run() {
        Supervisor supervisor = new Supervisor(config);

        List<Processor> processors = new ArrayList<>();
        for (int i = 1; i <= config.processorCount; ++i) {
            int speedUnit = config.averageProcessorSpeed * 2 / (config.processorCount + 1);
            processors.add(new Processor(i, i * speedUnit, supervisor));
        }

        Dispatcher dispatcher;
        try {
            Constructor<? extends Dispatcher> constructor = config.getDispatcherClass().getDeclaredConstructor(
                    List.class, Supervisor.class
            );
            dispatcher = constructor.newInstance(processors, supervisor);
        } catch (Exception exception) {
            throw new RuntimeException("Could not initialize Dispatcher.", exception);
        }

        for (Processor p : processors) {
            p.startExecution();
        }

        supervisor.start();

        for (long i = 0; i < config.generatedProcessLimit; ++i) {
            Process process = new Process(i, randomCost(), randomPriority());
            try {
                dispatcher.dispatchProcess(process);
                Thread.sleep(config.processGenerationPeriod);
            } catch (InterruptedException exception) {
                throw new RuntimeException("Main thread interrupted.", exception);
            }

            if ((i + 1) % 100 == 0) {
                System.out.printf("Generated %d out of %d processes.\n", i + 1, config.generatedProcessLimit);
            }
        }

        for (Processor p : processors) {
            p.stopExecution();
        }

        supervisor.finish();
    }

    private int randomCost() {
        return config.minProcessCost + random.nextInt(config.maxProcessCost - config.minProcessCost + 1);
    }

    private int randomPriority() {
        return config.minPriority + random.nextInt(config.maxPriority - config.minPriority + 1);
    }
}
