package org.valuereporter;

import org.valuereporter.observation.ObservedMethod;

import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public interface WriteOperations {
    long addObservations(String serviceName,List<ObservedMethod> observedMethods);
}
