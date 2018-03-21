package de.tkammer.procsim.dispatchers;

import de.tkammer.procsim.Dispatcher;
import de.tkammer.procsim.Process;
import de.tkammer.procsim.Processor;
import de.tkammer.procsim.Supervisor;

import java.util.List;
import java.util.Random;

public class RandomDispatcher extends Dispatcher {
    private final Random random = new Random(0L);

    public RandomDispatcher(List<Processor> processors, Supervisor supervisor) {
        super(processors, supervisor);
    }

    @Override
    protected Processor chooseProcessor(List<Processor> processors, Process process) {
        return processors.get(random.nextInt(processors.size()));
    }
}
