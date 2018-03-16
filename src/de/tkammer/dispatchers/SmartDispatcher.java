package de.tkammer.dispatchers;

import de.tkammer.Dispatcher;
import de.tkammer.Process;
import de.tkammer.Processor;
import de.tkammer.Supervisor;

import java.util.List;

public class SmartDispatcher extends Dispatcher {
    public SmartDispatcher(List<Processor> processors, Supervisor supervisor) {
        super(processors, supervisor);
    }

    @Override
    protected Processor chooseProcessor(List<Processor> processors, Process process) {
        int lowestCost = Integer.MAX_VALUE;
        Processor processor = null;
        for (Processor p : processors) {
            int cost = p.getQueueCost();
            Process currentProcess = p.getCurrentProcess();
            if (currentProcess != null) {
                // We don't know how much it progressed, but on average, it should be half done.
                cost += currentProcess.getCost() / 2;
            }
            cost /= p.getSpeed();
            if (cost < lowestCost) {
                processor = p;
                lowestCost = cost;
            }
        }
        return processor;
    }
}
