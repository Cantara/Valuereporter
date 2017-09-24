package org.valuereporter.activity;


import org.valuereporter.client.MonitorReporter;
import org.valuereporter.client.activity.ObservedActivityDistributer;

/**
 * Created by baardl on 05.03.16.
 */
public class ObservedActivitiesIntegrationTest {

    public static void main(String[] args) throws InterruptedException {

        String reporterHost = "reporter.valuereporter.org";
        reporterHost = "valuereporter-960449489.eu-west-1.elb.amazonaws.com";
        reporterHost = "localhost";
        String reporterPort = "80";
        reporterPort = "4901";
        String serviceName = "test";
        int cacheSize = 1;
        int forwardInterval = 10;
        new Thread(ObservedActivityDistributer.getInstance(reporterHost, reporterPort, serviceName, cacheSize, forwardInterval)).start();


        String userid = "TODO";
        do {
            ObservedActivity observedActivity = new UserLogonObservedActivity(userid);
            observedActivity.addContextInfo("started-at", new Long(System.currentTimeMillis()).toString());
            MonitorReporter.reportActivity(observedActivity);
            Thread.sleep(100);
        } while (true);
    }
}
