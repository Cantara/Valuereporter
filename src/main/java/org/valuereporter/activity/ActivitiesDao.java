package org.valuereporter.activity;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by t-blind5-01 on 02.03.2016.
 */
@Component
public class ActivitiesDao {
    private static final Logger log = getLogger(ActivitiesDao.class);
    public static final String START_TIME_COLUMN = "starttime";

    private final JdbcTemplate jdbcTemplate;
    private final JdbcTemplate jdbcAdminTemplate;

    @Autowired
    public ActivitiesDao(JdbcTemplate jdbcTemplate, JdbcTemplate jdbcAdminTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcAdminTemplate = jdbcAdminTemplate;
    }

    protected long insertActivities(String tableName, final List<String> columnNames, final List<ObservedActivity> activities) {
        log.trace("Try to insert activities. TableName {}, ColumnNames {}, ObservedActivities {}", tableName,columnNames, activities);
        if (tableName == null || tableName.isEmpty()) {
            throw new IllegalArgumentException("tableName must have a value");
        }
        if (columnNames == null) {
            throw new IllegalArgumentException("columntNames must not be null");
        }
        String sql = buildSql(tableName, columnNames);

        int[] updatePrStatement = null;
        try {
            updatePrStatement = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ObservedActivity activity = activities.get(i);
                    ps.setTimestamp(1, new java.sql.Timestamp(activity.getStartTime()));
                    int paramNum = 2;
                    for (String columnName : columnNames) {
                        ps.setObject(paramNum, activity.getValue(columnName));
                        paramNum ++;
                    }
//                ps.setObject(1, activity.getValue(columnNames.get(0)));
//                ps.setLong(1, customer.getCustId());
//                ps.setString(2, customer.getName());
//                ps.setInt(3, customer.getAge() );
                }

                @Override
                public int getBatchSize() {
                    return activities.size();
                }
            });
        } catch (DataAccessException de) {
            log.warn("Failed to update activities {}, reason {}", activities, de.getMessage(), de);
            throw de;
        }

        int sum = 0;
        if (updatePrStatement != null) {
            sum = IntStream.of(updatePrStatement).sum();
        }
        log.trace("Updated {} to table {}", sum, tableName);
        return sum;
    }

    public List<ObservedActivity> findUserSessions(DateTime startPeriod, DateTime endPeriod) {
        String sql = "Select * from userSession where starttime > ? and starttime < ?";
        long millisFrom = startPeriod.minusMillis(1).getMillis() ;
        long millisTo = endPeriod.plusMillis(1).getMillis() ;
        List<ObservedActivity> userSessions = new ArrayList<>();
        try {
            userSessions = jdbcTemplate.query(sql, new Object[]{new Timestamp(millisFrom), new Timestamp(millisTo)}, new UserSessionMapper());
        } catch (BadSqlGrammarException e) {
            log.info("Failed to query the database for Sql {} Reason: {}", sql, e.getMessage());
        }
        return userSessions;
    }

    public List<ObservedActivity> findUserSessionsByUserid(String userid, DateTime startPeriod, DateTime endPeriod) {
        String sql = "Select * from userSession where userid=? and starttime > ? and starttime < ?";
        long millisFrom = startPeriod.minusMillis(1).getMillis() ;
        long millisTo = endPeriod.plusMillis(1).getMillis() ;
        List<ObservedActivity> userSessions = jdbcTemplate.query(sql, new Object[]{userid, new Timestamp(millisFrom), new Timestamp(millisTo)}, new UserSessionMapper());
        return userSessions;
    }

    protected String buildSql(String tableName, List<String> columnNames) {
        String sql = "INSERT INTO " + tableName +
                "(" + START_TIME_COLUMN +", ";
        for (String columnName : columnNames) {
            sql += columnName + ", ";
        }
        sql = sql.substring(0,sql.length() - 2);
        sql += ") VALUES (?,";
        for (int i = 0; i < columnNames.size(); i++) {
            if (i < (columnNames.size() - 1)) {
                sql += "?,";
            } else {
                sql += "?";
            }
        }
        sql += ")";
        return sql;
    }

    public void createTable(String tableName, ArrayList<String> columnNames, ObservedActivity observedActivity) {
        //TODO
        String tableSql = "";
        if (tableName.equalsIgnoreCase("userlogon")){
            tableSql = "CREATE TABLE IF NOT EXISTS userlogon (\n" +
                "      id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,\n" +
//                "      prefix varchar(255) NOT NULL,\n" +
                "      starttime TIMESTAMP  NOT NULL,\n" +
                "      userid varchar(255)  NOT NULL,\n" +
                "    );";
        } else if (tableName.equalsIgnoreCase("userSession")){
            tableSql = "CREATE TABLE IF NOT EXISTS userSession (\n" +
                    "      id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,\n" +
//                "      prefix varchar(255) NOT NULL,\n" +
                    "      starttime TIMESTAMP  NOT NULL,\n" +
                    "      userid varchar(255)  NOT NULL,\n" +
                    "      userSessionfunction varchar(255),\n" +
                    "      applicationtokenid varchar(255)  NOT NULL,\n" +
                    "      applicationid varchar(255)  NOT NULL,\n" +
                    "    );";
        }
        jdbcAdminTemplate.execute(tableSql);
    }
}
