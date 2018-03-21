package de.tkammer.procsim.dispatchers;

import de.tkammer.procsim.Dispatcher;
import de.tkammer.procsim.Process;
import de.tkammer.procsim.Processor;
import de.tkammer.procsim.Supervisor;

import java.util.List;

public class BasicDispatcher extends Dispatcher {
    public BasicDispatcher(List<Processor> processors, Supervisor supervisor) {
        super(processors, supervisor);
    }

    @Override
    protected Processor chooseProcessor(List<Processor> processors, Process process) {
        int lowestCost = Integer.MAX_VALUE;
        Processor processor = null;
        for (Processor p : processors) {
            int cost = p.getQueueCost();
            if (cost < lowestCost) {
                processor = p;
                lowestCost = cost;
            }
        }
        return processor;
    }
}
