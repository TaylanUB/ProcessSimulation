package de.tkammer;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static final Random random = new Random(Config.ProcessGenerationSeed);

    private static int randomCost() {
        return Config.MinProcessCost + random.nextInt(Config.MaxProcessCost - Config.MinProcessCost + 1);
    }

    private static int randomPriority() {
        return Config.MinPriority + random.nextInt(Config.MaxPriority - Config.MinPriority + 1);
    }

    public static void main(String[] args) {
        Supervisor supervisor = new Supervisor();

        List<Processor> processors = new ArrayList<>();
        for (int i = 1; i <= Config.ProcessorCount; ++i) {
            int speedUnit = Config.AverageProcessorSpeed * 2 / (Config.ProcessorCount + 1);
            processors.add(new Processor(i, i * speedUnit, supervisor));
        }

        Dispatcher dispatcher;
        try {
            Constructor<? extends Dispatcher> constructor = Config.DispatcherClass.getDeclaredConstructor(
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

        for (long i = 0; i < Config.GeneratedProcessLimit; ++i) {
            Process process = new Process(i, randomCost(), randomPriority());
            try {
                dispatcher.dispatchProcess(process);
                Thread.sleep(Config.ProcessGenerationPeriod);
            } catch (InterruptedException exception) {
                throw new RuntimeException("Main thread interrupted.", exception);
            }

            if (i % 100 == 0) {
                System.out.printf("Generated %d out of %d processes.\n", i, Config.GeneratedProcessLimit);
            }
        }

        for (Processor p : processors) {
            p.stopExecution();
        }

        supervisor.finish();
    }
}
