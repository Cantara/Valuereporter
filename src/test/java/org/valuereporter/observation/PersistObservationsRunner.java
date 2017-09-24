package org.valuereporter.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class PersistObservationsRunner extends Thread {

    private final Logger log = LoggerFactory.getLogger(PersistObservationsRunner.class);
    private final ObservationsRepository observationsRepository;
    private final String serviceName;

    PersistObservationsRunner(String serviceName, ObservationsRepository observationsRepository) {
        super("PersistObservationsRunner");
        this.observationsRepository = observationsRepository;
        this.serviceName = serviceName;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.trace("Interupted {}", getName());
            }
            observationsRepository.persistAndResetStatistics(serviceName,1L);
        }
    }
}
