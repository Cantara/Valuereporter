package org.valuereporter.activity;

import java.util.Map;

/**
 * Created by baardl on 01.08.17.
 */
public class CountedActivity extends ObservedActivity{
    private long count = 1;
    public CountedActivity(String name, long startTime, Map<String, Object> data) {
        super(name, startTime, data);
    }

    public long getCount() {
        return count;
    }
}
