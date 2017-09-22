package org.valuereporter.activity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class ObservedActivityJsonDeprecated extends ObservedActivity {

    @JsonCreator
    public ObservedActivityJsonDeprecated(@JsonProperty("name") String name, @JsonProperty("startTime") long startTime, @JsonProperty("data") Map<String,Object> data, @JsonProperty("contextInfo") Map<String,Object> contextInfo) {
        super(name, startTime, data);

    }
}
