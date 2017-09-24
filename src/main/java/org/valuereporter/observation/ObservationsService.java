package org.valuereporter.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.valuereporter.QueryOperations;
import org.valuereporter.WriteOperations;
import org.valuereporter.scheduler.StatisticsPersister;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Service
public class ObservationsService implements QueryOperations, WriteOperations {
    private static final Logger log = LoggerFactory.getLogger(ObservationsService.class);
    private static final long MINUTES_15 = 15 * 60;
    private static final long INITIAL_DELAY = MINUTES_15;
    private static final long DELAY_BETWEEN_RUNS = MINUTES_15;
    private final ObservationsRepository observationsRepository;
    private long intervalSeconds = -1;
    private List<ObservedMethod> observedMethodsChache;
    private ObservationDao observationDao;
    private final boolean persistMethodDetails; // = false;
    private ConcurrentMap<String, StatisticsPersister> scheduledPrefixes = new ConcurrentHashMap<>();
    private StatisticsPersister statisticsPersister = null;

    @Autowired
    public ObservationsService(ObservationDao observationDao, ObservationsRepository observationsRepository, @Value("${observation.methods.detailed}") boolean persistMethodDetails, @Value("${observation.interval.seconds}") int intervalSeconds  ) {
        this.observationDao = observationDao;
        this.observationsRepository = observationsRepository;
        this.persistMethodDetails = persistMethodDetails;
        reportPersistDetails();
        if (intervalSeconds > 0) {
            this.intervalSeconds = intervalSeconds;
        }
    }

    @Override
    public List<ObservedMethod> findObservationsByName(String serviceName, String name) {
        List<ObservedMethod> returnResult = getObservedMethods(serviceName,name);
        if (returnResult == null || returnResult.size() < 1) {
            returnResult = new ArrayList<>();
            returnResult.add(new ObservedMethod(serviceName,name +"-template", System.currentTimeMillis(), System.currentTimeMillis() + 1));
        }
        return returnResult;
    }

    @Override
    public long addObservations(String serviceName, List<ObservedMethod> observedMethods) {
        long size = 0;
        if (observedMethods != null) {
            if (!isScheduled(serviceName)) {
                createScheduler(serviceName);
            }
            observationsRepository.updateStatistics(serviceName, intervalSeconds, observedMethods);
            if (isPersistMethodDetails()) {
                observationDao.addAll(serviceName, observedMethods);
            }
            size = observedMethods.size();
        }
        return size;
    }

    synchronized void createScheduler(String serviceName) {
        if (statisticsPersister == null) {
            long initialDelay = INITIAL_DELAY;
            long delayBetweenRuns = DELAY_BETWEEN_RUNS;
            if (intervalSeconds > 0) {
                initialDelay = intervalSeconds;
                delayBetweenRuns = intervalSeconds;
            }
            long shutdownAfter = -1; //Not used
            statisticsPersister = new StatisticsPersister(initialDelay, delayBetweenRuns, shutdownAfter);
        }
        statisticsPersister.startScheduler(observationsRepository, serviceName);
        scheduledPrefixes.put(serviceName, statisticsPersister);

    }

    synchronized boolean isScheduled(String serviceName) {
        boolean isScheduled = false;
        if (scheduledPrefixes.get(serviceName) != null) {
            //TODO And secure that the scheduler is active.
            isScheduled = true;
        }
        return isScheduled;
    }

    synchronized boolean isPersistMethodDetails() {
        return persistMethodDetails;
    }

    private void reportPersistDetails() {
        if (persistMethodDetails) {
            log.info("Will persist every method call into the database.");
        } else {
            log.info("Will only persist summary information of methods, to the database.");
        }

    }

    public List<ObservedMethod> getObservedMethods(String serviceName, String name) {
        List<ObservedMethod> observedMethods = observationDao.findObservedMethods(serviceName, name);
        return observedMethods;

    }
}
