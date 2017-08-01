package org.valuereporter.activity.timeseries;

import org.valuereporter.activity.CountedActivity;
import org.valuereporter.activity.ObservedActivity;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baardl on 31.07.17.
 */
public class ManualSendActivitiesTest {

    public static void main(String[] args) {
        URI influxDbUri = URI.create("http://influxdb-component-ox6b3xp9td0-772793266.eu-west-1.elb.amazonaws.com:8086"); ///write?db=";
        String database = "shareproc";

        List<ObservedActivity> observedActivities = new ArrayList<>();
        //client-access,host=dev.shareproc.com,service=api,function=login,ip=127.0.0.1 count=1
        String name = "client-access";
        long startTime = System.nanoTime();
        Map<String, Object> data = new HashMap<>();
        data.put("host", "whydahdev.cantara.no");
        data.put("service", "sts");
        data.put("function", "login");
        data.put("ip", "127.0.0.1");
        ObservedActivity activity = new CountedActivity(name,startTime,data);
        observedActivities.add(activity);
        CommandSendActivities sendActivities = new CommandSendActivities(influxDbUri, database, observedActivities);
        sendActivities.execute();
    }

}