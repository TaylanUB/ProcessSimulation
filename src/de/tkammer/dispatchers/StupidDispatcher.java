package de.tkammer.dispatchers;

import de.tkammer.Dispatcher;
import de.tkammer.Process;
import de.tkammer.Processor;
import de.tkammer.Supervisor;

import java.util.List;

public class StupidDispatcher extends Dispatcher {
    public StupidDispatcher(List<Processor> processors, Supervisor supervisor) {
        super(processors, supervisor);
    }

    int index = 0;

    @Override
    protected Processor chooseProcessor(List<Processor> processors, Process process) {
        Processor processor = processors.get(index);
        index = (index + 1) % processors.size();
        return processor;
    }
}
