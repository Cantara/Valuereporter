package org.valuereporter.value;

import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public interface QueryOperations {
    List<ValuableMethod> findValuableMethods(String serviceName);
    List<ValuableMethod> findValuableDistribution(String serviceName, String filterOnName);
}
