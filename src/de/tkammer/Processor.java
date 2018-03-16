package de.tkammer;

import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class Processor {
    private enum Status {
        Idle, Busy
    }

    private final long id;
    private final int speed;
    private final Supervisor supervisor;

    private final AtomicReference<Status> status;
    private final BlockingQueue<Process> processQueue;
    private final AtomicReference<Process> currentProcess;

    // For use by Supervisor.
    private final AtomicReference<Instant> startIdleTime = new AtomicReference<>();

    public Processor(int id, int speed, Supervisor supervisor) {
        this.id = id;
        this.speed = speed;
        this.supervisor = supervisor;

        status = new AtomicReference<>(Status.Idle);
        processQueue = new LinkedBlockingQueue<>();
        currentProcess = new AtomicReference<>();

        new Thread(this::executionLoop).start();
    }

    public long getId() {
        return id;
    }

    public Status getStatus() {
        return status.get();
    }

    public int getQueueLength() {
        return processQueue.size();
    }

    public int getQueueCost() {
        int cost = 0;
        for (Process process : processQueue) {
            cost += process.getCost();
        }
        return cost;
    }

    public void enqueueProcess(Process process) throws InterruptedException {
        processQueue.put(process);
    }

    public Process getCurrentProcess() {
        return currentProcess.get();
    }

    public void setStartIdleTime() {
        startIdleTime.set(Instant.now());
    }

    public Instant getStartIdleTime() {
        return startIdleTime.get();
    }

    private void executionLoop() {
        while (true) {
            try {
                Process process = processQueue.take();
                status.set(Status.Busy);
                process.setStatus(Process.Status.Started);
                supervisor.recordProcessStart(this, process);
                currentProcess.set(process);
                executeProcess(process);
                status.set(Status.Idle);
                process.setStatus(Process.Status.Finished);
                supervisor.recordProcessEnd(this, process);
                currentProcess.set(null);
            } catch (InterruptedException exception) {
                throw new RuntimeException("Processor thread interrupted.", exception);
            }
        }
    }

    private void executeProcess(Process process) throws InterruptedException {
        Thread.sleep(process.getCost() / speed);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[CPU %d (%d Hz)]", id, speed);
    }
}
