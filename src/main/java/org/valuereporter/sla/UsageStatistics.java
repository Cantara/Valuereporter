package org.valuereporter.sla;

/**
 * @author <a href="mailto:bard.lind@gmail.com">Bard Lind</a>
 */
public class UsageStatistics {
    private final String serviceName;
    private final String methodName;
    private final long duration;
    private final long startTime;
    private final long count;
    private final long max;
    private final long min;
    private final double mean;
    private final double median;
    private final double stdDev;
    private final double p95;
    private final double p98;
    private final double p99;

    public UsageStatistics(String serviceName, String methodName, long duration, long startTime, long count, long max, long min, double mean, double median, double stdDev, double p95, double p98, double p99) {
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.duration = duration;
        this.startTime = startTime;
        this.count = count;
        this.max = max;
        this.min = min;
        this.mean = mean;
        this.median = median;
        this.stdDev = stdDev;
        this.p95 = p95;
        this.p98 = p98;
        this.p99 = p99;
    }

    public String getPrefix() {
        return serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public long getDuration() {
        return duration;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getCount() {
        return count;
    }

    public long getMax() {
        return max;
    }

    public long getMin() {
        return min;
    }

    public double getMean() {
        return mean;
    }

    public double getMedian() {
        return median;
    }

    public double getStdDev() {
        return stdDev;
    }

    public double getP95() {
        return p95;
    }

    public double getP98() {
        return p98;
    }

    public double getP99() {
        return p99;
    }

    @Override
    public String toString() {
        return "UsageStatistics{" +
                "serviceName='" + serviceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", count=" + count +
                ", max=" + max +
                ", min=" + min +
                ", mean=" + mean +
                ", median=" + median +
                ", stdDev=" + stdDev +
                ", p95=" + p95 +
                ", p98=" + p98 +
                ", p99=" + p99 +
                '}';
    }
}
