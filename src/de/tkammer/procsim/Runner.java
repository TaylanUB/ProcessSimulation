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
        random = new Random(config.ProcessGenerationSeed);
    }

    public void run() {
        Supervisor supervisor = new Supervisor(config);

        List<Processor> processors = new ArrayList<>();
        for (int i = 1; i <= config.ProcessorCount; ++i) {
            int speedUnit = config.AverageProcessorSpeed * 2 / (config.ProcessorCount + 1);
            processors.add(new Processor(i, i * speedUnit, supervisor));
        }

        Dispatcher dispatcher;
        try {
            Constructor<? extends Dispatcher> constructor = config.DispatcherClass.getDeclaredConstructor(
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

        for (long i = 0; i < config.GeneratedProcessLimit; ++i) {
            Process process = new Process(i, randomCost(), randomPriority());
            try {
                dispatcher.dispatchProcess(process);
                Thread.sleep(config.ProcessGenerationPeriod);
            } catch (InterruptedException exception) {
                throw new RuntimeException("Main thread interrupted.", exception);
            }

            if ((i + 1) % 100 == 0) {
                System.out.printf("Generated %d out of %d processes.\n", i + 1, config.GeneratedProcessLimit);
            }
        }

        for (Processor p : processors) {
            p.stopExecution();
        }

        supervisor.finish();
    }

    private int randomCost() {
        return config.MinProcessCost + random.nextInt(config.MaxProcessCost - config.MinProcessCost + 1);
    }

    private int randomPriority() {
        return config.MinPriority + random.nextInt(config.MaxPriority - config.MinPriority + 1);
    }
}
