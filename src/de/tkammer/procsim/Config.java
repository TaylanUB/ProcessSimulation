package de.tkammer.procsim;

import de.tkammer.procsim.dispatchers.*;

import java.io.File;
import java.io.PrintWriter;

public class Config {
    private String resultsDir = System.getProperty("user.home") + File.separator + "Desktop";

    private Class<? extends Dispatcher> dispatcherClass = SmartDispatcher.class;

    private PrintWriter printWriter = new PrintWriter(System.out);

    public void setResultsDir(String resultsDir) {
        this.resultsDir = resultsDir;
    }

    public String getWaitDurationsFile() {
        return resultsDir + "\\wait-durations.csv";
    }

    public String getExecDurationsFile() {
        return resultsDir + "\\exec-durations.csv";
    }

    public String getIdleDurationsFile() {
        return resultsDir + "\\idle-durations.csv";
    }

    public void setDispatcherClass(Class<? extends Dispatcher> dispatcherClass) {
        this.dispatcherClass = dispatcherClass;
    }

    public Class<? extends Dispatcher> getDispatcherClass() {
        return dispatcherClass;
    }

    public void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    /*
     * For process generation and execution to break even, you should ensure the following equation:
     *
     *     AverageProcessCost / (processorCount * averageProcessorSpeed) = processGenerationPeriod.
     *
     * (The AverageProcessCost is, obviously, the average of minProcessCost and maxProcessCost.)
     *
     * Also, the following division should result in an integer value:
     *
     *     (averageProcessorSpeed * 2) / (processorCount + 1)
     */

    public int minProcessCost = 1000;
    public int maxProcessCost = 19000;

    public int processorCount = 4;
    public int averageProcessorSpeed = 250;

    public int minPriority = 0;
    public int maxPriority = 99;

    public int processGenerationPeriod = 10;

    // Random seed for cost and priority.
    public long processGenerationSeed = 0L;

    public int generatedProcessLimit = 2000;
}
