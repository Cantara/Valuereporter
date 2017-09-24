package org.valuereporter.value;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class ValuableMethod {

    private String serviceName = "";
    private String name;
    private long usageCount;
    private long startTime;
    private long endTime;

    public ValuableMethod(String name, long usageCount) {
        this.name = name;
        this.usageCount = usageCount;
    }
    public ValuableMethod(String serviceName,String name, long usageCount) {
        this.serviceName = serviceName;
        this.name = name;
        this.usageCount = usageCount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @deprecated Use getServiceName()
     * @return
     */
    @Deprecated
    public String getPrefix() {
        return getServiceName();
    }

    /**
     * @deprecated use setServiceName()
     * @param serviceName
     */
    @Deprecated
    public void setPrefix(String serviceName) {
        setServiceName(serviceName);
    }

    public String getName() {
        return name;
    }

    public long getUsageCount() {
        return usageCount;
    }

    public void addUsageCount(long usageCount) {
        this.usageCount += usageCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String toCSV() {
        if (name == null) {
            name = "";
        }
        if (serviceName == null) {
            serviceName = "";
        }
        return serviceName.trim() + "," + name.trim() + "," + usageCount + "," + startTime + "," + endTime;
    }
}
