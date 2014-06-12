package org.valuereporter.value;

import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public interface QueryOperations {
    List<ValuableMethod> findValuableMethods(String prefix);
    List<ValuableMethod> findValuableDistribution(String prefix);
}
