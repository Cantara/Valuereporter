package org.valuereporter.activity.whydah;

import org.valuereporter.activity.CommandSendActivities;
import org.valuereporter.activity.ObservedActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Copied from Whydah to ensure testable backward compatibility.
 * Created by baardl on 20.09.17.
 */
public class ManualUserSessionObservedActivityTest {

    private static String useridTemplate = "testuser1";
    private static String userSessionFunction = "someActivity";
    private static String applcationTokenId = "application1";


    public static void main(String[] args) throws InterruptedException {
        String reporterHost = "localhost";
        String reporterPort = "4901";
        String serviceName = "test";
        String userId = useridTemplate + System.currentTimeMillis();
        UserSessionObservedActivity observedActivity = new UserSessionObservedActivity(userId, userSessionFunction,applcationTokenId);
        observedActivity.addContextInfo("started-at", new Long(System.currentTimeMillis()).toString());
        observedActivity.setServiceName(serviceName);
        //2. Push WhydahUserSessionObservedActivity
        List<ObservedActivity> observedActivities = new ArrayList<>();
        observedActivities.add(observedActivity);
        CommandSendActivities sendActivities = new CommandSendActivities(reporterHost,reporterPort,serviceName,observedActivities);
        sendActivities.execute();
        //3. Read back from database.
        Thread.sleep(1000);
    }

}
