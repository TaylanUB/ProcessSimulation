package de.tkammer.procsim;

import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private final Thread executionThread;
    private final AtomicBoolean stopExecution;

    // For use by Supervisor.
    private final AtomicReference<Instant> startIdleTime = new AtomicReference<>();

    public Processor(int id, int speed, Supervisor supervisor) {
        this.id = id;
        this.speed = speed;
        this.supervisor = supervisor;

        status = new AtomicReference<>(Status.Idle);
        processQueue = new LinkedBlockingQueue<>();
        currentProcess = new AtomicReference<>();

        executionThread = new Thread(this::executionLoop);
        stopExecution = new AtomicBoolean(false);
    }

    public long getId() {
        return id;
    }

    public int getSpeed() {
        return speed;
    }

    public void startExecution() {
        executionThread.start();
    }

    public void stopExecution() {
        stopExecution.set(true);
        try {
            executionThread.join();
        } catch (InterruptedException exception) {
            throw new RuntimeException("Interrupt while waiting for thread to end.", exception);
        }
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
                Process process = processQueue.poll(100, TimeUnit.MILLISECONDS);
                if (process == null) {
                    if (stopExecution.get()) {
                        break;
                    }
                    continue;
                }
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

        stopExecution.set(false);
    }

    private void executeProcess(Process process) throws InterruptedException {
        Thread.sleep(process.getCost() / speed);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[CPU %d (%d Hz)]", id, speed);
    }
}
