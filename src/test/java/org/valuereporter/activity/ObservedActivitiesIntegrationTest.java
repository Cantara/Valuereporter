package org.valuereporter.activity;


import org.valuereporter.client.MonitorReporter;
import org.valuereporter.client.activity.ObservedActivityDistributer;

import static org.valuereporter.activity.ObservedActivity.Builder.observe;

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
            ObservedActivity observedActivity = observe("logon").addContext("userid", userid).build();
            MonitorReporter.reportActivity(observedActivity);
            Thread.sleep(100);
        } while (true);
    }
}
