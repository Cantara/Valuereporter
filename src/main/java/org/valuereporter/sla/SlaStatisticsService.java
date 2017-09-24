package org.valuereporter.sla;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Service
public class SlaStatisticsService implements SlaQueryOperations{
    private static final Logger log = LoggerFactory.getLogger(SlaStatisticsService.class);

    private SlaDao slaDao;

    @Autowired
    public SlaStatisticsService(SlaDao slaDao) {
        this.slaDao = slaDao;
    }

    @Override
    public SlaStatisticsRepresentation findSlaStatistics(String serviceName, String methodName, DateTime start, DateTime end) {
        log.trace("findSlaStatistics for serviceName {}, filter {}", serviceName, methodName);
        if (end == null) {
            end = new DateTime();
        }
        if (start == null) {
            start = end.minusDays(7);
        }
        List<SlaStatistics> statistics = slaDao.findSlaStatistics(serviceName, methodName, start, end);
        SlaStatisticsRepresentation representation = new SlaStatisticsRepresentation(serviceName, methodName, start.getMillis(), end.getMillis(),statistics);
        log.trace("findSlaStatistics result for serviceName {}, filter {}, from {}, to {}. Result count: {} ", serviceName, methodName, start, end, statistics.size());
        return representation;
    }
}
