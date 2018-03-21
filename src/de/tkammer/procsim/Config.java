package de.tkammer.procsim;

import de.tkammer.procsim.dispatchers.*;

public class Config {
    // Set this to a writable directory on your PC.
    public String ResultsDir = "C:\\Users\\tkammer\\Documents\\Umschulung\\11ITS3\\Programmierung";

    // Change this to test different Dispatcher implementations.
    public Class<? extends Dispatcher> DispatcherClass = SmartDispatcher.class;

    public String getWaitDurationsFile() {
        return ResultsDir + "\\wait-durations.csv";
    }
    public String getExecDurationsFile() {
        return ResultsDir + "\\exec-durations.csv";
    }
    public String getIdleDurationsFile() {
        return ResultsDir + "\\idle-durations.csv";
    }

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

    public int MinProcessCost =  1000;
    public int MaxProcessCost = 19000;

    public int ProcessorCount = 4;
    public int AverageProcessorSpeed = 250;

    public int MinPriority = 0;
    public int MaxPriority = 99;

    public int ProcessGenerationPeriod = 10;

    // Random seed for cost and priority.
    public long ProcessGenerationSeed = 0L;

    public int GeneratedProcessLimit = 2000;
}
