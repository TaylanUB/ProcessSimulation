package de.tkammer;

import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class Process {
    public enum Status {
        Waiting, Started, Finished
    }

    private final long id;
    private final int cost;
    private final int priority;

    private final AtomicReference<Status> status = new AtomicReference<>();

    // For use by Supervisor.
    private final AtomicReference<Instant> dispatchTime = new AtomicReference<>();
    private final AtomicReference<Instant> startTime = new AtomicReference<>();
    private final AtomicReference<Instant> endTime = new AtomicReference<>();

    public Process(long id, int cost, int priority) {
        this.id = id;
        this.cost = cost;
        this.priority = priority;
        status.set(Status.Waiting);
    }

    public long getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public int getPriority() {
        return priority;
    }

    public void setStatus(Status status) {
        this.status.set(status);
    }

    public Status getStatus() {
        return status.get();
    }

    public void setDispatchTime() {
        dispatchTime.set(Instant.now());
    }

    public Instant getDispatchTime() {
        return dispatchTime.get();
    }

    public void setStartTime() {
        startTime.set(Instant.now());
    }

    public Instant getStartTime() {
        return startTime.get();
    }

    public void setEndTime() {
        endTime.set(Instant.now());
    }

    public Instant getEndTime() {
        return endTime.get();
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[Proc %d (cost %d, prio %d)]", id, cost, priority);
    }
}
