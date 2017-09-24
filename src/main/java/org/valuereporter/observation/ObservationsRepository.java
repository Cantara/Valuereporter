package org.valuereporter.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Repository
public class ObservationsRepository {
    private static final Logger log = LoggerFactory.getLogger(ObservationsRepository.class);
    private ObservationDao observationDao;
    private ConcurrentMap<String, PrefixCollection> serviceNamees = new ConcurrentHashMap<>();

    @Autowired
    public ObservationsRepository(ObservationDao observationDao) {
        this.observationDao = observationDao;
    }

    public void updateStatistics(String serviceName, Long intervalInSec,List<ObservedMethod> methods) {
        PrefixCollection serviceNameCollection = getCollection(serviceName, intervalInSec);

        for (ObservedMethod method : methods) {
            serviceNameCollection.updateStatistics(method);
        }
    }

    /**
     *
     * @param serviceName
     * @return Will always return a serviceNameCollection, unless serviceName is null or empty
     * @throws IllegalArgumentException If serviceName is null or empty.
     */
    synchronized PrefixCollection getCollection(String serviceName, long intervalInSec) throws IllegalArgumentException{
        if (serviceName == null || serviceName.isEmpty()) {
            throw new IllegalArgumentException("Prefix may not be null, nor empty.");
        }
        PrefixCollection serviceNameCollection;
        if (!serviceNamees.containsKey(serviceName)) {
            serviceNameCollection = new PrefixCollection(serviceName, intervalInSec);
            serviceNamees.putIfAbsent(serviceName, serviceNameCollection);
        }
        serviceNameCollection = serviceNamees.get(serviceName);
        return serviceNameCollection;
    }

    public synchronized void persistAndResetStatistics(String serviceName, long intervalInSec) {
        log.trace("persistStatistics starts");
        List<ObservedInterval> intervals = fetchAndClear(serviceName, intervalInSec);
        if (intervals.size() > 0) {
            // log.debug("Got serviceNameCollection {}", serviceNameCollection.toString());
            log.debug("Got intervals size {}", intervals.size());
            int keysUpdated = updateMissingKeys(serviceName, intervals);
            log.trace("updated {} keys", keysUpdated);
            int intervalsUpdated = observationDao.updateStatistics(serviceName, intervals);
            log.trace("updated {} intervals", intervalsUpdated);
        } else {
            log.trace("Nothing to presist.");
        }

    }

    synchronized List<ObservedInterval> fetchAndClear(String serviceName, long intervalInSec) {
        PrefixCollection newInterval = new PrefixCollection(serviceName, intervalInSec);
        PrefixCollection harvestedStats = serviceNamees.get(serviceName);
        serviceNamees.replace(serviceName,newInterval);
        List<ObservedInterval> statisticalIntervals = harvestedStats.getIntervals();
        return statisticalIntervals;
    }

    private int updateMissingKeys(String serviceName, List<ObservedInterval> intervals) {
        log.debug("updateMissingKeys intervalSize {}", intervals.size());
        List<String> methodNames = new ArrayList<>(intervals.size());
        for (ObservedInterval interval : intervals) {
            methodNames.add(interval.getMethodName());
        }

        int keysUpdated = 0;
        List<String> existingKeys = observationDao.findObservedKeys(serviceName);
        //Iterate over intervals.
        String methodName = null;
        for (ObservedInterval interval : intervals) {
            methodName = interval.getMethodName();
            if (!existingKeys.contains(methodName)) {
                int count = observationDao.insertObservedKey(serviceName, methodName);
                //log.debug("Updated rows {} for serviceName {}, methodName {}", count,serviceName, methodName);
                keysUpdated =+ count;
            }
        }
        //If key not found, insert key
        //keysUpdated = observationDao.ensureObservedKeys(serviceName, methodNames);
        return keysUpdated;
    }

    public Map<String, PrefixCollection> getPrefixes() {
        return serviceNamees;
    }

    private void clearCollection(String serviceName) {
        serviceNamees.remove(serviceName);
    }
}
