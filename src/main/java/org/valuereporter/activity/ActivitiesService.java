package org.valuereporter.activity;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.valuereporter.whydah.LogonDao;

import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by t-blind5-01 on 02.03.2016.
 */
@Service
public class ActivitiesService {
    private static final Logger log = getLogger(ActivitiesService.class);

    private final ActivitiesDao activitiesDao;
    private final LogonDao logonDao;

    @Autowired
    public ActivitiesService(ActivitiesDao activitiesDao, LogonDao logonDao) {
        this.activitiesDao = activitiesDao;
        this.logonDao = logonDao;
    }

    public long updateActivities(String prefix, ArrayList<ObservedActivityJson> observedActivities) {
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
                long updateCount = updateActivitiesByName(activities);
                updatedActivities += updateCount;
            }
        }
        return updatedActivities;
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
                log.error("Exception:", de);
                if (isMissingTablexeption(de)) {
                    createTable(tableName);
                    updatedActivities = activitiesDao.insertActivities(tableName, columnNames, observedActivities);
                }
            }
        }
        return updatedActivities;
    }

    private void createTable(String tableName) {
        activitiesDao.createTable(tableName);
    }

    public List<Long> findLogonsByUserid(String userid) {
        return logonDao.findLogonsByUserId(userid);
    }

    private boolean isMissingTablexeption(DataAccessException de) {
        log.error("isMissingTablexeption", de);
        if (1 == 1) return true;
        boolean missingTable = false;
        if (de.getCause()!= null) {
            missingTable = de.getCause().getMessage().contains("object not found");
        }
        return missingTable;
    }


}
