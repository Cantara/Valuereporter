package org.valuereporter;

import org.valuereporter.observation.ObservedMethodJson;

import java.util.ArrayList;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public interface WriteOperations {
    long addObservations(String prefix, ArrayList<ObservedMethodJson> observedMethods);
}
