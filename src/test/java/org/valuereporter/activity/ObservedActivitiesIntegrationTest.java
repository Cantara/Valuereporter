package org.valuereporter.activity;

import org.valuereporter.agent.MonitorReporter;
import org.valuereporter.agent.activity.ObservedActivityDistributer;

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
        new Thread(new ObservedActivityDistributer(reporterHost, reporterPort, prefix, cacheSize, forwardInterval)).start();


        String userid = "TODO";
        do {
            org.valuereporter.agent.activity.ObservedActivity observedActivity = new UserLogonObservedActivity(userid);
            MonitorReporter.reportActivity(observedActivity);
            Thread.sleep(100);
        } while (true);
    }
}
