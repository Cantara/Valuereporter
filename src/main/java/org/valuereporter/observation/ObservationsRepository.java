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
    private ConcurrentMap<String, PrefixCollection> prefixes = new ConcurrentHashMap<>();

    @Autowired
    public ObservationsRepository(ObservationDao observationDao) {
        this.observationDao = observationDao;
    }

    public void updateStatistics(String prefix, Long intervalInSec, ArrayList<ObservedMethodJson> methods) {
        PrefixCollection prefixCollection = getCollection(prefix, intervalInSec);

        for (ObservedMethod method : methods) {
            prefixCollection.updateStatistics(method);
        }
    }

    /**
     *
     * @param prefix
     * @return Will always return a prefixCollection, unless prefix is null or empty
     * @throws IllegalArgumentException If prefix is null or empty.
     */
    synchronized PrefixCollection getCollection(String prefix, long intervalInSec) throws IllegalArgumentException{
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("Prefix may not be null, nor empty.");
        }
        PrefixCollection prefixCollection;
        if (!prefixes.containsKey(prefix)) {
            prefixCollection = new PrefixCollection(prefix, intervalInSec);
            prefixes.putIfAbsent(prefix, prefixCollection);
        }
        prefixCollection = prefixes.get(prefix);
        return prefixCollection;
    }

    public synchronized void persistAndResetStatistics(String prefix, long intervalInSec) {
        log.trace("persistStatistics starts");
        List<ObservedInterval> intervals = fetchAndClear(prefix, intervalInSec);
        if (intervals.size() > 0) {
            // log.debug("Got prefixCollection {}", prefixCollection.toString());
            log.debug("Got intervals size {}", intervals.size());
            int keysUpdated = updateMissingKeys(prefix, intervals);
            log.trace("updated {} keys", keysUpdated);
            int intervalsUpdated = observationDao.updateStatistics(prefix, intervals);
            log.trace("updated {} intervals", intervalsUpdated);
        } else {
            log.trace("Nothing to presist.");
        }

    }

    synchronized List<ObservedInterval> fetchAndClear(String prefix, long intervalInSec) {
        PrefixCollection newInterval = new PrefixCollection(prefix, intervalInSec);
        PrefixCollection harvestedStats = prefixes.get(prefix);
        prefixes.replace(prefix,newInterval);
        List<ObservedInterval> statisticalIntervals = harvestedStats.getIntervals();
        return statisticalIntervals;
    }

    private int updateMissingKeys(String prefix, List<ObservedInterval> intervals) {
        log.debug("updateMissingKeys intervalSize {}", intervals.size());
        List<String> methodNames = new ArrayList<>(intervals.size());
        for (ObservedInterval interval : intervals) {
            methodNames.add(interval.getMethodName());
        }

        int keysUpdated = 0;
        List<String> existingKeys = observationDao.findObservedKeys(prefix);
        //Iterate over intervals.
        String methodName = null;
        for (ObservedInterval interval : intervals) {
            methodName = interval.getMethodName();
            if (!existingKeys.contains(methodName)) {
                int count = observationDao.insertObservedKey(prefix, methodName);
                //log.debug("Updated rows {} for prefix {}, methodName {}", count,prefix, methodName);
                keysUpdated =+ count;
            }
        }
        //If key not found, insert key
        //keysUpdated = observationDao.ensureObservedKeys(prefix, methodNames);
        return keysUpdated;
    }

    public Map<String, PrefixCollection> getPrefixes() {
        return prefixes;
    }

    private void clearCollection(String prefix) {
        prefixes.remove(prefix);
    }
}
