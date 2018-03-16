package de.tkammer;

import de.tkammer.dispatchers.*;

public class Config {
    // Set this to a writable directory on your PC.
    public static final String ResultsDir = "C:\\Users\\tkammer\\Documents\\Umschulung\\11ITS3\\Programmierung";

    // Change this to test different Dispatcher implementations.
    public static final Class<? extends Dispatcher> DispatcherClass = SmartDispatcher.class;

    public static final String WaitDurationsFile = ResultsDir + "\\wait-durations.csv";
    public static final String ExecDurationsFile = ResultsDir + "\\exec-durations.csv";
    public static final String IdleDurationsFile = ResultsDir + "\\idle-durations.csv";

    /*
     * For process generation and execution to break even, you should ensure the following equation:
     *
     *     AverageProcessCost / (ProcessorCount * AverageProcessorSpeed) = ProcessGenerationPeriod.
     *
     * (The AverageProcessCost is, obviously, the average of MinProcessCost and MaxProcessCost.)
     *
     * Also, the following division should result in an integer value:
     *
     *     (AverageProcessorSpeed * 2) / (ProcessorCount + 1)
     */

    public static final int MinProcessCost =  1000;
    public static final int MaxProcessCost = 19000;

    public static final int ProcessorCount = 4;
    public static final int AverageProcessorSpeed = 250;

    public static final int MinPriority = 0;
    public static final int MaxPriority = 99;

    public static final int ProcessGenerationPeriod = 10;

    public static final int GeneratedProcessLimit = 2000;
}
