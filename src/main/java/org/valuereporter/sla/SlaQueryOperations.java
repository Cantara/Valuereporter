package org.valuereporter.sla;

import org.joda.time.DateTime;

/**
 * @author <a href="mailto:bard.lind@gmail.com">Bard Lind</a>
 */
public interface SlaQueryOperations {

    SlaStatisticsRepresentation findSlaStatistics(String serviceName, String methodName, DateTime start, DateTime end);
}
