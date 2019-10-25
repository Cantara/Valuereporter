package org.valuereporter.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class UpdateObservationsToServiceRunner extends Thread {

    private static final String PREFIX = ThreadSafeObservationsRepositoryVerification.PREFIX;
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
                observationsService.addObservations(PREFIX, getFirstMethods());
            } else {
                try {
                    Thread.sleep((int)(Math.random() * 1000));
                } catch (InterruptedException e) {
                    log.trace("Interupted {}", getName());
                }
                observationsService.addObservations(PREFIX, (ArrayList<ObservedMethodJson>) getSecondMethods());
            }
        }
    }

    ArrayList<ObservedMethodJson> getFirstMethods() {
        List<ObservedMethodJson> firstMethods = new ArrayList<>();
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 1000;
        long duration = 1000;
        firstMethods.add((ObservedMethodJson) new ObservedMethod(PREFIX, ThreadSafeObservationsRepositoryVerification.FIRST_METHOD, startTime, endTime, duration));
        return (ArrayList<ObservedMethodJson>) firstMethods;
    }

    List<ObservedMethodJson> getSecondMethods() {
        List<ObservedMethodJson> firstMethods = new ArrayList<>();
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 200;
        long duration = 200;
        firstMethods.add((ObservedMethodJson) new ObservedMethod(PREFIX, ThreadSafeObservationsRepositoryVerification.SECOND_METHOD, startTime, endTime, duration));
        return firstMethods;
    }
}