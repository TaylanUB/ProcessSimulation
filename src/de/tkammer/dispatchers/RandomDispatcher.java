package de.tkammer.dispatchers;

import de.tkammer.Dispatcher;
import de.tkammer.Process;
import de.tkammer.Processor;
import de.tkammer.Supervisor;

import java.util.List;
import java.util.Random;

public class RandomDispatcher extends Dispatcher {
    private Random random = new Random();

    public RandomDispatcher(List<Processor> processors, Supervisor supervisor) {
        super(processors, supervisor);
    }

    @Override
    protected Processor chooseProcessor(List<Processor> processors, Process process) {
        return processors.get(random.nextInt(processors.size()));
    }
}
