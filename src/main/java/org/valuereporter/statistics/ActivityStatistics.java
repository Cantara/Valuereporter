package org.valuereporter.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baardl on 05.03.16.
 */
public class ActivityStatistics {

    private String serviceName = "ALL";
    private String activityName;
    private Long startTime;
    private Long endTime;
    private Map<String, Object> activities = new HashMap<>();

    public ActivityStatistics(String activityName) {
        this.activityName = activityName;
    }

    public ActivityStatistics(String serviceName, String activityName, Long startTime, Long endTime) {
        this.serviceName = serviceName;
        this.activityName = activityName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getPrefix() {
        return serviceName;
    }

    public void setPrefix(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public void add(String key, Object values) {
        if (activities == null){
            activities = new HashMap<String,Object>();
        }
        activities.put(key,values);
    }

    public Map<String, Object> getActivities() {
        return activities;
    }
}
