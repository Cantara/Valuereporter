package org.valuereporter.activity;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.valuereporter.activity.timeseries.CommandSendActivities;
import org.valuereporter.activity.timeseries.TimeseriesConnection;
import org.valuereporter.whydah.LogonDao;

import java.net.URI;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;
import static org.valuereporter.helper.Configuration.getString;
import static org.valuereporter.helper.StringHelper.hasContent;

/**
 * Created by t-blind5-01 on 02.03.2016.
 */
@Service
public class ActivitiesService {
    private static final Logger log = getLogger(ActivitiesService.class);
    public static final String DEFAULT_DATABASE = "default";

    private final ActivitiesDao activitiesDao;
    private final LogonDao logonDao;
    private final URI timeseriesUri;
    private final String timeseriesDatabaseName;
    private final String timeseriesUsername;
    private final String timeseriesPassword;
    private Map<String, TimeseriesConnection> timeseriesConfigs = new HashMap<>();

    @Autowired
    public ActivitiesService(ActivitiesDao activitiesDao, LogonDao logonDao, @Value("${timeseries.uri}") String timeseriesUrl ,
                             @Value("${timeseries.databasename}") String timeseriesDatabaseName,
                             @Value("${timeseries.username}") String timeseriesUsername,
                             @Value("${timeseries.password}") String timeseriesPassword) {
        this.activitiesDao = activitiesDao;
        this.logonDao = logonDao;
        this.timeseriesUri = URI.create(timeseriesUrl);
        this.timeseriesDatabaseName = timeseriesDatabaseName;
        this.timeseriesUsername = timeseriesUsername;
        this.timeseriesPassword = timeseriesPassword;
        TimeseriesConnection defaultTSDb = new TimeseriesConnection(DEFAULT_DATABASE, timeseriesDatabaseName,timeseriesUsername,timeseriesPassword);
        timeseriesConfigs.put(defaultTSDb.getPrefix(), defaultTSDb);

    }

    public long updateActivities(String prefix, List<ObservedActivity> observedActivities) {
        long updatedActivities = 0;

        if (observedActivities != null && observedActivities.size() > 0) {
            log.trace("Try to update {} activities for {}", observedActivities.size(), prefix);
            //TODO split into separate lambda/clojure expressions depending on activity.name
            Map<String,List<ObservedActivity>> activitiesByName = new HashMap<>();
            String name = null;
            for (ObservedActivity observedActivity : observedActivities) {
                name = observedActivity.getName();
                if (!activitiesByName.containsKey(name)){
                    ArrayList<ObservedActivity> namedActivites = new ArrayList<>();
                    namedActivites.add(observedActivity);
                    activitiesByName.put(name, namedActivites);
                } else {
                    activitiesByName.get(name).add(observedActivity);
                }
            }
            Set<String> names = activitiesByName.keySet();
            for (String activityName : names) {
                List<ObservedActivity> activities = activitiesByName.get(activityName);
                //FIXME write userlogon to hsqldb
                // long updateCount = updateActivitiesByName(activities);
                long updateCount = 0;
                log.warn("Write of userLogon to database is disabled.");
                updatedActivities += updateCount;
            }

            sendActivities(prefix, observedActivities);

        }
        return updatedActivities;
    }

    protected void sendActivities(String prefix, List<ObservedActivity> observedActivities) {
        log.trace("Send {} activities to Timeseries", observedActivities.size());
        TimeseriesConnection connection = findConnection(prefix);
        if (connection != null) {
            CommandSendActivities sendActivities = new CommandSendActivities(timeseriesUri, connection, observedActivities);
            String result = sendActivities.execute();
            log.trace("Result from sending activities to Timeseries: {}", result);
        } else {
            log.warn("No connection was found for prefix {}. Could not send {} activities to Timeseries", prefix, observedActivities.size());
        }

    }

    protected TimeseriesConnection findConnection(String prefix) {
        TimeseriesConnection connection = null;
        connection = timeseriesConfigs.get(prefix);
        if (connection == null) {
            connection = findConnectionInProperties(prefix);
            if (connection == null) {
                connection = timeseriesConfigs.get(DEFAULT_DATABASE);
            } else {
                timeseriesConfigs.put(prefix, connection);
            }
        }

        return connection;
    }

    protected TimeseriesConnection findConnectionInProperties(String prefix) {
        TimeseriesConnection connection = null;
        if (hasContent(prefix)) {
            String timeseriesDatabaseName = getString("timeseries." + prefix + ".databasename");
            String timeseriesUsername = getString("timeseries." + prefix + ".username");
            String timeseriesPassword = getString("timeseries." + prefix + ".password");
            connection = new TimeseriesConnection(prefix, timeseriesDatabaseName, timeseriesUsername, timeseriesPassword);
        }
        return connection;
    }

    protected long updateActivitiesByName(List<ObservedActivity> observedActivities) {
        long updatedActivities = 0;
        ObservedActivity activity = observedActivities.get(0);
        String tableName = activity.getName();
        Map<String, Object> data = activity.getData();
        if (data != null && data.keySet() != null) {
            Set<String> keys = data.keySet();
            ArrayList<String> columnNames = new ArrayList<>(keys.size());
            for (String key : keys) {
                columnNames.add(key);
            }
            try {
                updatedActivities = activitiesDao.insertActivities(tableName, columnNames, observedActivities);
            } catch (DataAccessException de) {
                log.warn("Insert into {} failed, trying createTable",tableName);
                if (isMissingTablexeption(de)) {
                    createTable(tableName, columnNames, observedActivities);
                    updatedActivities = activitiesDao.insertActivities(tableName, columnNames, observedActivities);
                }
            }
        }
        return updatedActivities;
    }

    private void createTable(String tableName, ArrayList<String> columnNames, List<ObservedActivity> observedActivities) {
        activitiesDao.createTable(tableName, columnNames, observedActivities.get(0));
    }

    public List<Long> findLogonsByUserid(String userid) {
        return logonDao.findLogonsByUserId(userid);
    }

    private boolean isMissingTablexeption(DataAccessException de) {
        boolean missingTable = false;
        if (de.getCause()!= null) {
            missingTable = de.getCause().getMessage().contains("object not found");
        }
        return missingTable;
    }


}
