package de.tkammer;

import de.tkammer.dispatchers.*;

public class Config {
    // Set this to a writable directory on your PC.
    public static final String ResultsDir = "C:\\Users\\tkammer\\Documents\\Umschulung\\11ITS3\\Programmierung";

    // Change this to test different Dispatcher implementations.
    public static final Class<? extends Dispatcher> DispatcherClass = StupidDispatcher.class;

    public static final String WaitDurationsFile = ResultsDir + "\\wait-durations.csv";
    public static final String ExecDurationsFile = ResultsDir + "\\exec-durations.csv";
    public static final String IdleDurationsFile = ResultsDir + "\\idle-durations.csv";

    // For process generation and execution to break even, set these values to fulfill:
    // AverageProcessCost / (AverageProcessorSpeed * ProcessorCount) = ProcessGenerationPeriod.
    // (The AverageProcessCost is, obviously, the average of MinProcessCost and MaxProcessCost.)
    public static final int MinProcessCost = 25000;
    public static final int MaxProcessCost = 75000;
    public static final int AverageProcessorSpeed = 50;
    public static final int ProcessGenerationPeriod = 250;

    // To avoid integer division issues, this should be set so that the following results in an integer:
    // (AverageProcessorSpeed * 2) / (ProcessorCount + 1)
    public static final int ProcessorCount = 4;

    public static final int MinPriority = 0;
    public static final int MaxPriority = 99;
}
