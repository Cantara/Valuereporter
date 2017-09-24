package org.valuereporter.observation;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.valuereporter.StatusType;
import org.valuereporter.ValuereporterException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Component
public class ObservationDao {
    private static final Logger log = LoggerFactory.getLogger(ObservationDao.class);
    private static final String MAX_RESULTS = "1000";
    private static String usernameSql = "SELECT  id FROM UserTable where username = ?";
    private static String emailSql = "SELECT  id FROM UserTable where email = ?";
    private static String phoneSql = "SELECT  id FROM UserTable where phone = ?";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ObservationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int findOwnerId(String userId) {
        log.debug("OwnerId: Try to find single owner with userId:" + userId);
        int ownerId = 0;
        String msg = "";
        if (StringUtils.isNotEmpty(userId) && StringUtils.isNotBlank(userId)) {
            ownerId = findSingleItem(usernameSql, userId);
            if (ownerId < 1) {
                msg = "No single result from:" + usernameSql +". Parameter: " + userId +".";
                ownerId = findSingleItem(emailSql, userId);
            } if (ownerId < 1) {
                msg = msg +  ". No single result from:" + emailSql +". Parameter: " + userId +".";
                ownerId = findSingleItem(phoneSql, userId);
            }
        }
        log.debug("OwnerId:" + ownerId + ": from userId:" + userId + "." + msg);
        return ownerId;
    }

    /**
     *
     * @param sql
     * @param parameter
     * @return if of identified owner. -1 if more than one result, or 0 if no results are found.
     */
    private int findSingleItem(String sql, String parameter) {
        List<Integer> ownerIds = jdbcTemplate.queryForList(sql, Integer.class, parameter);
        if (ownerIds.size() == 1) {
            return ownerIds.get(0).intValue();
        } else if (ownerIds.size() > 1) {
            log.warn("Search on sql:" + sql + ": parameter:" + parameter + ". Returned " + ownerIds.size() + ". Elements Only one should be returned.");
            return -1;
        }
        return 0;

    }
    public List<ObservedMethod> findObservedMethods(String serviceName, String name) {

        //TODO Alter time
        //String sql = "SELECT serviceName,methodName, startTime, endTime, duration  FROM ObservedMethod WHERE serviceName = ? AND methodName = ? AND startTime > DATEADD(month,-2,GETDATE() ) ORDER BY startTime ASC ";
        String sql = "SELECT serviceName,methodName, startTime, endTime, duration  FROM ObservedMethod WHERE serviceName = ? AND methodName = ? ORDER BY startTime ASC ";
        Object[] parameters = new Object[] {serviceName,name};
        List<ObservedMethod> observedMethods = jdbcTemplate.query(sql, parameters, new RowMapper<ObservedMethod>() {
            @Override
            public ObservedMethod mapRow(ResultSet resultSet, int i) throws SQLException {
                //log.debug("Returned values: {},{},{},{},{}", resultSet.getObject(1),resultSet.getObject(2),resultSet.getObject(3),resultSet.getObject(4),resultSet.getObject(5));

                ObservedMethod observedMethod = new ObservedMethod(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getTimestamp(3).getTime(),
                        resultSet.getTimestamp(4).getTime(),
                        resultSet.getLong(5));
                return observedMethod;
            }
        });
        log.trace("findObservedMethods. Found {}", observedMethods.size());
        return observedMethods;
    }

    public List<ObservedMethod> findObservedMethods(String serviceName, String name, DateTime start, DateTime end) {

        String sql = "SELECT serviceName,methodName, startTime, endTime, duration  FROM ObservedMethod WHERE startTime >= ? and startTime <= ? AND serviceName = ? AND methodName = ? ORDER BY startTime ASC ";
        Timestamp endTime = new Timestamp(end.getMillis());
        Timestamp startTime = new Timestamp(start.getMillis());
        Object[] parameters = new Object[] {startTime , endTime, serviceName,name};
        List<ObservedMethod> observedMethods = jdbcTemplate.query(sql, parameters, new RowMapper<ObservedMethod>() {
            @Override
            public ObservedMethod mapRow(ResultSet resultSet, int i) throws SQLException {

                ObservedMethod observedMethod = new ObservedMethod(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getTimestamp(3).getTime(),
                        resultSet.getTimestamp(4).getTime(),
                        resultSet.getLong(5));
                return observedMethod;
            }
        });
        return observedMethods;
    }

    public void addAll(final String serviceName, final List<ObservedMethod> observedMethods) {
        if (observedMethods != null && observedMethods.size() > 0) {
            try {
                String sql = "INSERT INTO "
                        + "ObservedMethod "
                        + "(serviceName,methodName, startTime, endTime, duration) "
                        + "VALUES " + "(?,?,?,?,?)";

                jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {

                        ObservedMethod observedMethod = observedMethods.get(i);
                        ps.setString(1, serviceName);
                        ps.setString(2, observedMethod.getName());
                        ps.setTimestamp(3, new Timestamp(observedMethod.getStartTime()));
                        ps.setTimestamp(4, new Timestamp(observedMethod.getEndTime()));
                        ps.setLong(5, observedMethod.getDuration());

                    }

                    @Override
                    public int getBatchSize() {
                        return observedMethods.size();
                    }
                });
            } catch (DataAccessException dae) {
                log.warn("Failed to update via batch. Batch size {}, reason {}",observedMethods.size(),dae.getMessage());
                throw new ValuereporterException("Failed to update via batch. Batch size " + observedMethods.size() +". Reason: " + dae.getMessage(), dae,StatusType.data_error);
            }
        }


    }

    public synchronized int updateStatistics(final String serviceName, final List<ObservedInterval> intervals) {
        log.trace("Calling update statistics. serviceName {}, intervals count {}", serviceName, intervals.size());

        String sql = "insert into ObservedInterval (observedKeysId, startTime, duration, vrCount, vrMax, vrMin, vrMean, vrMedian, stdDev, p95, p98, p99)\n" +
                "select o.id," +
                "?, ?,?,?,?,?,?,?,?,?,? " +
                "  from ObservedKeys o\n" +
                "  where serviceName= ? and methodName = ?;";

        int[] intervalsUpdated;
        intervalsUpdated = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                ObservedInterval interval = intervals.get(i);
                Timestamp sqlDate = new Timestamp(interval.getStartTime());
                ps.setTimestamp(1, sqlDate);
                ps.setLong(2, interval.getInterval());
                ps.setLong(3, interval.getCount());
                ps.setDouble(4, interval.getMax());
                ps.setDouble(5, interval.getMin());
                ps.setDouble(6, interval.getMean());
                ps.setDouble(7, interval.getMedian());
                ps.setDouble(8, interval.getStandardDeviation());
                ps.setDouble(9, interval.getP95());
                ps.setDouble(10, interval.getP98());
                ps.setDouble(11, interval.getP99());
                ps.setString(12,serviceName);
                ps.setString(13, interval.getMethodName());

            }

            @Override
            public int getBatchSize() {
                return intervals.size();
            }
        });

        return intervalsUpdated.length;

        /*
        String sql = "insert into ObservedInterval (observedKeysId, startTime, duration, count, max, min, mean, median, stdDev, p95, p98, p99)\n" +
                "  select o.id, '" + sqlDate + "', "+duration +","+ count +"," + max +"," +min + "," + mean + "," + median + "," + standardDeviation +
                "," + p95+"," + p98+"," + p99+ "\n" +
                "  from ObservedKeys o\n" +
                "  where serviceName='" + serviceName + "' and methodName = '" + methodName + "';";
        return sql;
        */
    }

    public int[] ensureObservedKeys(final String serviceName, final List<String> methodNames) {
        log.debug("ensureObservedKeys. MethodNames {}", methodNames);
        String sql = "insert ignore into ObservedKeys (serviceName, methodName)"
                + "VALUES " + "(?,?)";

        int[] keysUpdated = null;
        try {
            keysUpdated = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i)
                        throws SQLException {

                    ps.setString(1, serviceName);
                    ps.setString(2, methodNames.get(i));

                }

                @Override
                public int getBatchSize() {
                    return methodNames.size();
                }
            });
            log.debug("keysupdated {}", keysUpdated);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        return keysUpdated;

    }

    


    public List<String> findObservedKeys(String serviceName) {
        List<String> observedKeys = new ArrayList<>();
        String sql = "SELECT methodName  FROM ObservedKeys WHERE serviceName = ?";
        observedKeys= jdbcTemplate.queryForList(sql, String.class, serviceName);
        return observedKeys;
    }

    public int insertObservedKey(String serviceName, String methodName) {
        int rowsUpdated = 0;
        try {
            String sql = "INSERT into ObservedKeys (serviceName, methodName) values (?,?)";
            rowsUpdated = jdbcTemplate.update(sql, serviceName, methodName);
        } catch (DuplicateKeyException e) {
            log.trace("Key exists, not inserted. Prefix {}, methodName {}", serviceName, methodName);
        }
        return rowsUpdated;
    }
}
