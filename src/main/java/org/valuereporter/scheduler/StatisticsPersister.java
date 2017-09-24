package org.valuereporter.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.observation.ObservationsRepository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class StatisticsPersister {
    private static final Logger log = LoggerFactory.getLogger(StatisticsPersister.class);

    private static final int NUM_THREADS = 1;
    private final long initialDelay;
    private final long delayBetweenRuns;
    private final long shutdownAfter;
    private final ScheduledExecutorService scheduler;

    public StatisticsPersister(long initialDelay, long delayBetweenRuns, long shutdownAfter){
        this.initialDelay = initialDelay;
        this.delayBetweenRuns = delayBetweenRuns;
        this.shutdownAfter = shutdownAfter;
        this.scheduler = Executors.newScheduledThreadPool(NUM_THREADS);
    }

    public void startScheduler(ObservationsRepository repository, String serviceName){
        log.info("Starting scheduler for serviceName {}, initialDelay {}, delayBetweenRuns {}", serviceName, initialDelay, delayBetweenRuns);
        Runnable persist = new PersistTask(repository, serviceName);
        ScheduledFuture<?> persistenceFuture = scheduler.scheduleWithFixedDelay(
                persist, initialDelay, delayBetweenRuns, TimeUnit.SECONDS
        );
    }

    private class PersistTask implements Runnable {
        private ObservationsRepository repository;
        private String serviceName;

        private PersistTask(ObservationsRepository repository, String serviceName) {
            this.repository = repository;
            this.serviceName = serviceName;
        }
        @Override
        public void run() {
            log.trace("Persist serviceName {}", serviceName);
            repository.persistAndResetStatistics(serviceName, delayBetweenRuns);
        }

    }
}
