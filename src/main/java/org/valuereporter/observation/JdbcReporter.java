package org.valuereporter.observation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class JdbcReporter  {
    private static final Logger log = LoggerFactory.getLogger(JdbcReporter.class);

    private JdbcTemplate jdbcTemplate;

    public JdbcReporter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void report(long timestamp, String serviceName, String methodName, Object... values) {
        try {
            log.trace("Update for serviceName [{}], methodName [{}]", serviceName, methodName);
            int rowsUpdated = insertObservedInterval(serviceName, methodName,timestamp, values);
            if (rowsUpdated < 1) {
                String updateKeysql = "insert ignore into ObservedKeys (serviceName, methodName) values ('" + serviceName+ "','" + methodName +"')";
                jdbcTemplate.update(updateKeysql);
                rowsUpdated = insertObservedInterval(serviceName, methodName, timestamp, values);
            }
            log.trace("Updated {} rows", rowsUpdated);
        } catch (Exception e) {
            log.error("error", e.getMessage(), e);
        }

    }

    private int insertObservedInterval(String serviceName, String methodName, long timestamp, Object[] values) {
        String sql = JdbcHelper.insertObservedInterval(serviceName, methodName, timestamp, values);
        return jdbcTemplate.update(sql);
    }

}
