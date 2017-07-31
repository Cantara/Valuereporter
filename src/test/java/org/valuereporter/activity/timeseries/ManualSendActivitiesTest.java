package org.valuereporter.activity.timeseries;

import org.valuereporter.activity.ObservedActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baardl on 31.07.17.
 */
public class ManualSendActivitiesTest {

    public static void main(String[] args) {
        String influxDbUri = "http://influxdb-component-ox6b3xp9td0-772793266.eu-west-1.elb.amazonaws.com:8086/write?db=";
        String database = "shareproc";

        List<ObservedActivity> observedActivities = new ArrayList<>();
        CommandSendActivities sendActivities = new CommandSendActivities(influxDbUri, database, observedActivities);
        sendActivities.execute();
    }

}