package org.valuereporter;

import org.valuereporter.observation.ObservedMethod;

import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public interface QueryOperations {
    List<ObservedMethod> findObservationsByName(String serviceName, String name);
}
