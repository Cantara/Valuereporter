package org.valuereporter.activity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class ObservedActivityJson extends ObservedActivity {

    @JsonCreator
    public ObservedActivityJson(@JsonProperty("name") String name, @JsonProperty("startTime") long startTime, @JsonProperty("data") Map<String, Object> data) {
        super(name, startTime, data);
    }

    @JsonCreator
    public ObservedActivityJson(@JsonProperty("serviceName") String serviceName, @JsonProperty("name") String name, @JsonProperty("startTime") long startTime, @JsonProperty("data") Map<String, Object> data) {
        super(name + "_" + serviceName, startTime, data);
    }

    public ObservedActivityJson(@JsonProperty("serviceName") String serviceName, @JsonProperty("activityName") String activityName, @JsonProperty("name") String name, @JsonProperty("startTime") long startTime, @JsonProperty("data") Map<String, Object> data) {
        super(name + "_" + serviceName + "_" + activityName, startTime, data);
    }

}
