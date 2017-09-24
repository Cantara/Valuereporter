package org.valuereporter.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class ObservationDaoStub extends ObservationDao {
    private static final Logger log = LoggerFactory.getLogger(ObservationDaoStub.class);
    private int updateStatisticsCount = 0;
    public ObservationDaoStub(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public int updateStatistics(String serviceName, List<ObservedInterval> intervals) {

        for (ObservedInterval interval : intervals) {
            updateStatisticsCount();
            log.info("updateStatistics {}, {}, {}, {}", serviceName, interval.getMethodName(), interval.getCount(), interval.getStartTime());
        }
        return intervals.size();
    }

    private synchronized void updateStatisticsCount() {
        updateStatisticsCount = updateStatisticsCount +1;
    }

    public int getUpdateStatisticsCount() {
        return updateStatisticsCount;
    }
}
