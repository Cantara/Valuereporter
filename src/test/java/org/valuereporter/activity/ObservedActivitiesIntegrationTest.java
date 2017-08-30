package org.valuereporter.activity;

import org.valuereporter.agent.MonitorReporter;
import org.valuereporter.agent.activity.ObservedActivityDistributer;

/**
 * Created by baardl on 05.03.16.
 */
public class ObservedActivitiesIntegrationTest {

    public static void main(String[] args) throws InterruptedException {

        String reporterHost = "reporter.valuereporter.org";
        reporterHost = "valuereporter-960449489.eu-west-1.elb.amazonaws.com";
        String reporterPort = "80";
        String prefix = "test";
        int cacheSize = 1;
        int forwardInterval = 10;
        new Thread(new ObservedActivityDistributer(reporterHost, reporterPort, prefix, cacheSize, forwardInterval)).start();


        String userid = "TODO";
        do {
            org.valuereporter.agent.activity.ObservedActivity observedActivity = new UserLogonObservedActivity(userid);
            observedActivity.put("started-at", new Long(System.currentTimeMillis()).toString());
            MonitorReporter.reportActivity(observedActivity);
            Thread.sleep(100);
        } while (true);
    }
}
