package org.valuereporter.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class UpdateObservationsToServiceRunner extends Thread {

    private static final String SERVICE_NAME = ThreadSafeObservationsRepositoryVerification.SERVICE_NAME;
    private final Logger log = LoggerFactory.getLogger(UpdateObservationsToServiceRunner.class);
    private final ObservationsService observationsService;

    UpdateObservationsToServiceRunner(String methodName, ObservationsService observationsService) {
        super(methodName);
        this.observationsService = observationsService;
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            if (getName().equals(ThreadSafeObservationsRepositoryVerification.FIRST_METHOD)) {
                try {
                    Thread.sleep((int)(Math.random() * 1000));
                } catch (InterruptedException e) {
                    log.trace("Interupted {}", getName());
                }
                observationsService.addObservations(SERVICE_NAME, getFirstMethods());
            } else {
                try {
                    Thread.sleep((int)(Math.random() * 1000));
                } catch (InterruptedException e) {
                    log.trace("Interupted {}", getName());
                }
                observationsService.addObservations(SERVICE_NAME, getSecondMethods());
            }
        }
    }

    List<ObservedMethod> getFirstMethods() {
        List<ObservedMethod> firstMethods = new ArrayList<>();
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 1000;
        long duration = 1000;
        firstMethods.add(new ObservedMethod(SERVICE_NAME, ThreadSafeObservationsRepositoryVerification.FIRST_METHOD, startTime, endTime, duration));
        return firstMethods;
    }

    List<ObservedMethod> getSecondMethods() {
        List<ObservedMethod> firstMethods = new ArrayList<>();
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 200;
        long duration = 200;
        firstMethods.add(new ObservedMethod(SERVICE_NAME, ThreadSafeObservationsRepositoryVerification.SECOND_METHOD, startTime, endTime, duration));
        return firstMethods;
    }
}