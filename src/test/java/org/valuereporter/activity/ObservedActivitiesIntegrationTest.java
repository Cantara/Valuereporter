package org.valuereporter.activity;

import org.valuereporter.client.MonitorReporter;
import org.valuereporter.client.activity.ObservedActivityDistributer;

/**
 * Created by baardl on 05.03.16.
 */
public class ObservedActivitiesIntegrationTest {

    public static void main(String[] args) throws InterruptedException {

        String reporterHost = "localhost";
        String reporterPort = "4901";
        String prefix = "test";
        int cacheSize = 1;
        int forwardInterval = 10;
        new Thread(ObservedActivityDistributer.getInstance(reporterHost, reporterPort, prefix, cacheSize, forwardInterval)).start();


        String userid = "TODO";
        do {
            org.valuereporter.activity.ObservedActivity observedActivity = new UserLogonObservedActivity(userid);
            MonitorReporter.reportActivity(observedActivity);
            Thread.sleep(100);
        } while (true);
    }
}
