package org.valuereporter.sla;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.valuereporter.StatusType;
import org.valuereporter.ValuereporterInputException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author <a href="mailto:bard.lind@gmail.com">Bard Lind</a>
 */
@Component
public class SlaDao {
    private static final Logger log = LoggerFactory.getLogger(SlaDao.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public SlaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UsageStatistics> findUsage(String serviceName, String methodFilter, DateTime start, DateTime end) {
        String sql = "select ok.serviceName, ok.methodName, oi.duration,oi.startTime, oi.vrCount, oi.vrMax, oi.vrMin, oi.vrMean, oi.vrMedian, oi.stdDev, oi.p95, oi.p98, oi.p99  \n" +
                "   from ObservedInterval oi, ObservedKeys ok \n" +
                "   where oi.startTime >= ? and oi.startTime <= ? and ok.serviceName= ? and ok.methodName= ? and ok.id = oi.observedKeysId \n" +
                "   order by oi.startTime asc";


       // end = new DateTime(1411557199907L).plusDays(1);
        Timestamp endTime = null;
        Timestamp startTime = null;
        if (end == null) {
            end = new DateTime();
        }
        if (start == null) {
            start = end.minusDays(7);
        }
        endTime = new Timestamp(end.getMillis());
        startTime = new Timestamp(start.getMillis());

        Object[] parameters = new Object[] {startTime, endTime,serviceName,methodFilter};
        List<UsageStatistics> usageStatisticses = jdbcTemplate.query(sql, parameters, new RowMapper<UsageStatistics>() {
            @Override
            public UsageStatistics mapRow(ResultSet resultSet, int i) throws SQLException {

                UsageStatistics usageStatistics = new UsageStatistics(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getLong(3),
                        resultSet.getTimestamp(4).getTime(),
                        resultSet.getLong(5),
                        resultSet.getLong(6),
                        resultSet.getLong(7),
                        resultSet.getDouble(8),
                        resultSet.getDouble(9),
                        resultSet.getDouble(10),
                        resultSet.getDouble(11),
                        resultSet.getDouble(12),
                        resultSet.getDouble(13));
                return usageStatistics;
            }
        });
        return usageStatisticses;

    }

    public List<SlaStatistics> findSlaStatistics(String serviceName, String methodName, DateTime start, DateTime end){
        String sql = "select  oi.startTime,oi.duration, oi.vrCount, oi.vrMax, oi.vrMin, oi.vrMean, oi.p95  \n" +
                "   from ObservedInterval oi, ObservedKeys ok \n" +
                "   where oi.startTime >= ? and oi.startTime <= ? and ok.serviceName= ? and ok.methodName= ? and ok.id = oi.observedKeysId \n" +
                "   order by oi.startTime desc";

        if (isInValid(serviceName) || isInValid(methodName) || isInValidDate(start) || isInValidDate(end)) {
            log.trace("Invalid values for one of serviceName {}, methodName {}, start {}, end {}. Will throw exception.", serviceName, methodName, start, end);
            throw new ValuereporterInputException("Invalid data for serviceName " + serviceName + ", methodName " + methodName + ",startDate " + start
            + ", or endDate " +end, StatusType.data_error);
        }

        Timestamp endTime = new Timestamp(end.getMillis());
        Timestamp startTime = new Timestamp(start.getMillis());

        Object[] parameters = new Object[] {startTime, endTime,serviceName,methodName};
        List<SlaStatistics> slaStatisticses = jdbcTemplate.query(sql, parameters, new RowMapper<SlaStatistics>() {
            @Override
            public SlaStatistics mapRow(ResultSet resultSet, int i) throws SQLException {
                SlaStatistics slaStatistics = new SlaStatistics(
                        resultSet.getTimestamp(1).getTime(),
                        resultSet.getLong(2),
                        resultSet.getLong(3),
                        resultSet.getDouble(4),
                        resultSet.getDouble(5),
                        resultSet.getDouble(6),
                        resultSet.getDouble(7));
                return slaStatistics;
            }
        });
        return slaStatisticses;

    }

    boolean isValidDate(DateTime date) {
        return (date != null);
    }
    boolean isInValidDate(DateTime date) {
        return !isValidDate(date);
    }

    boolean isValid(String value) {
        return (value != null && !value.trim().isEmpty());
    }

    boolean isInValid(String value) {
        return !isValid(value);
    }


}
