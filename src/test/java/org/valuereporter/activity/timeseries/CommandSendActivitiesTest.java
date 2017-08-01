package org.valuereporter.activity.timeseries;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.valuereporter.activity.CountedActivity;
import org.valuereporter.activity.ObservedActivity;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * Created by baardl on 01.08.17.
 */
public class CommandSendActivitiesTest {
    List<ObservedActivity> observedActivities = null;
    ObservedActivity activity = null;
    CommandSendActivities commandSendActivities = null;
    @BeforeMethod
    public void setUp() throws Exception {
        String name = "client-access";
        long startTime = System.nanoTime();
        Map<String, Object> data = new HashMap<>();
        data.put("host", "whydahdev.cantara.no");
        data.put("service", "sts");
        data.put("function", "login");
        data.put("ip", "127.0.0.1");
        activity = new CountedActivity(name,startTime,data);
        observedActivities = new ArrayList<>();
        observedActivities.add(activity);
        Map<String, Object> data2 = data;
        data2.put("ip", "127.0.0.2");
        observedActivities.add(new CountedActivity(name, System.nanoTime(), data2));


        URI uri = URI.create("http://localhost");
        String databaseName = "testdb";
        commandSendActivities = new CommandSendActivities(uri, databaseName, observedActivities);
    }

    @Test
    public void testBuildBody() throws Exception {
        String body = commandSendActivities.buildBody(observedActivities);
        String expected = "client-access,service=sts,function=login,ip=127.0.0.2,host=whydahdev.cantara.no count=1\n" +
                "client-access,service=sts,function=login,ip=127.0.0.2,host=whydahdev.cantara.no count=1\n";
        assertEquals(body, expected);
    }

    @Test
    public void testBuildMeasurement() throws Exception {

    }

    @Test
    public void testBuildTags() throws Exception {
        String expected = "service=sts,function=login,ip=127.0.0.1,host=whydahdev.cantara.no";
        assertEquals(commandSendActivities.buildTags(activity), expected);
        assertEquals(commandSendActivities.buildTags(null), "");


    }

}