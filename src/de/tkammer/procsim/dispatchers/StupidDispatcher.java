package de.tkammer.procsim.dispatchers;

import de.tkammer.procsim.Dispatcher;
import de.tkammer.procsim.Process;
import de.tkammer.procsim.Processor;
import de.tkammer.procsim.Supervisor;

import java.util.List;

public class StupidDispatcher extends Dispatcher {
    public StupidDispatcher(List<Processor> processors, Supervisor supervisor) {
        super(processors, supervisor);
    }

    private int index = 0;

    @Override
    protected Processor chooseProcessor(List<Processor> processors, Process process) {
        Processor processor = processors.get(index);
        index = (index + 1) % processors.size();
        return processor;
    }
}
