package org.valuereporter.sla;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:bard.lind@gmail.com">Bard Lind</a>
 */
@Service
public class ObservedIntervalsService implements ObservedQueryOperations {
    private static final Logger log = LoggerFactory.getLogger(ObservedQueryOperations.class);

    private SlaDao slaDao;

    @Autowired
    public ObservedIntervalsService(SlaDao slaDao) {
        this.slaDao = slaDao;
    }

    @Override
    public List<UsageStatistics> findUsage(String serviceName, String filter, DateTime from, DateTime to) {
        log.trace("findUsage for serviceName {}, filter {}", serviceName, filter);
        String methodFilter = filter;
        List<UsageStatistics> usage = slaDao.findUsage(serviceName, methodFilter, from, to);
        log.trace("findUsage result for serviceName {}, filter {}, from {}, to {}. Result count: {} ",serviceName, filter, from, to,usage.size() );
        return usage;
    }




}
