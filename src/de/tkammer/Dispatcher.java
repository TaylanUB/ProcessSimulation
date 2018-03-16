package de.tkammer;

import java.util.List;

public abstract class Dispatcher {
    private final List<Processor> processors;
    private final Supervisor supervisor;

    public Dispatcher(List<Processor> processors, Supervisor supervisor) {
        this.processors = processors;
        this.supervisor = supervisor;
    }

    public void dispatchProcess(Process process) throws InterruptedException {
        Processor processor = chooseProcessor(processors, process);
        supervisor.recordDispatch(processor, process);
        processor.enqueueProcess(process);
    }

    protected abstract Processor chooseProcessor(List<Processor> processors, Process process);
}
