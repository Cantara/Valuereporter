package org.valuereporter.tools;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.valuereporter.Main;
import org.valuereporter.observation.ObservationDao;
import org.valuereporter.observation.ObservedInterval;
import org.valuereporter.observation.ObservedMethod;
import org.valuereporter.observation.PrefixCollection;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class ObservedMethodDataMigration {
    private static final Logger log = LoggerFactory.getLogger(ObservedMethodDataMigration.class);
    public static final int INTERVAL_MINUTES = 15;

    private ObservationDao observationDao = null;

    public ObservedMethodDataMigration(ObservationDao observationDao) {
        this.observationDao = observationDao;
    }

    public static void main(String[] args) {
        log.info("Initializing Spring context.");

        Properties properties = findProperties();
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/tools/applicationContext-datamigration.xml");
        ObservationDao dao = (ObservationDao) applicationContext.getBean("observationDao");
        String serviceName = properties.getProperty("datamigration.serviceName");
        ObservedMethodDataMigration migrator = new ObservedMethodDataMigration(dao);
        log.info("Spring context initialized.");

        List<String> methodNames = findMethodsToParse(properties);
        DateTime start = new DateTime(2014,9, 29, 0,0,0 );
        //DateTime end = new DateTime(2014,6, 5, 9,45,0 );
        DateTime end = new DateTime(2014,9, 29, 23,45,0 );
        //Do some work.
        migrator.createSummary(serviceName, methodNames, start, end );


    }

    private void createSummary(String serviceName, List<String> methodNames, DateTime firstDate, DateTime lastDate) {
        PrefixCollection serviceNameCollection = new PrefixCollection(serviceName);
        log.info("CreateSummary from {} to {}, for methodNames {}", firstDate, lastDate, methodNames);
        //(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, int secondOfMinute)
        DateTime start = new DateTime(firstDate); // new DateTime(2014,9, 23, 13,40,0 );
        DateTime end = null;
        long countIntervals = 0;
        long countMethods = 0;

        do {
            end = start.plusMinutes(INTERVAL_MINUTES);
            log.info("Finding statistics from {} to {}", start, end);

            for (String methodName : methodNames) {
                log.trace("Find ObservedMethods. serviceName {}, methodName {}, start {}, end {}", serviceName, methodName, start, end);
                List<ObservedMethod> observedMethods = observationDao.findObservedMethods(serviceName, methodName, start, end);
                if (observedMethods.size() > 0) {
                    countMethods = countMethods + observedMethods.size();
                    log.trace("Found {} methods. ", observedMethods.size());
                    long interval = end.getMillis() - start.getMillis();
                    serviceNameCollection.updateStatisticsList(observedMethods, start.getMillis(), interval);
                }
            }

            List<ObservedInterval> observedIntervals = serviceNameCollection.getIntervals();
            log.trace("Found {} intervals.", observedIntervals.size());
            if (observedIntervals.size() > 0) {
                countIntervals = countIntervals + observedIntervals.size();
                int countStat = observationDao.updateStatistics(serviceName, observedIntervals);
                log.trace("Updated interval to statistics. count: {}" + countStat);
            }
            serviceNameCollection = new PrefixCollection(serviceName);
            //Loop for next interval.
            start = start.plusMinutes(INTERVAL_MINUTES);
        }  while (start.isBefore(lastDate));
        log.info("-Finished- CreateSummary from {} to {}. Inserted {} intervals for {} method calls. " , firstDate, end, countIntervals, countMethods);

    }


    private static List<String> findMethodsToParse(Properties properties) {
        String propValue = properties.getProperty("datamigration.methodNames");
        String[] methodNames = propValue.split(",");
        return new ArrayList<>(Arrays.asList(methodNames));
    }

    protected static Properties findProperties() {
        Properties resourceBundle = findPropertiesFile("/valuereporter.properties");
        Properties overridesBundle = findPropertiesFile("/config_override/valuereporter.properties");
        Properties properties = new Properties();
        if (resourceBundle != null) {
            Enumeration<Object> keys = resourceBundle.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                properties.put(key, resourceBundle.getProperty(key));
            }
        }
        if (overridesBundle != null) {
            Enumeration<Object> keys = overridesBundle.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                properties.put(key,overridesBundle.getProperty(key));
            }
        }
        return properties;
    }

    protected static Properties findPropertiesFile(String filename) {
        Properties prop = new Properties();
        InputStream inputFile = null;
        try {
            inputFile = Main.class.getResourceAsStream(filename);
            if (inputFile != null) {
                prop.load(inputFile);
            } else {
                log.info("Could not find properties file: {}", filename);
            }

        } catch (Exception e) {
            log.warn("Could not load data from properties file: {}", filename, e);
        } finally {
            if (inputFile != null) {
                try {
                    inputFile.close();
                } catch (IOException e) {
                    //Do nothing.
                    log.debug("Failed to close stream.");
                }
            }
        }
        return prop;
    }

}
