package org.valuereporter.value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Component
public class ValueDao {
    private static final Logger log = LoggerFactory.getLogger(ValueDao.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ValueDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ValuableMethod> findUsageByMethod(String serviceName) {

        String sql = "SELECT methodName, COUNT(*) FROM ObservedMethod WHERE serviceName = ? GROUP BY methodName";
        sql = "SELECT ok.SERVICENAME, ok.METHODNAME, count(oi.VRCOUNT) FROM OBSERVEDINTERVAL oi, OBSERVEDKEYS ok " +
                "WHERE oi.OBSERVEDKEYSID = ok.ID AND ok.PREFIX = ? " +
                "GROUP BY ok.ID;";
        Object[] parameters = new Object[] {serviceName};
        List<ValuableMethod> valuableMethods = jdbcTemplate.query(sql, parameters, new RowMapper<ValuableMethod>() {
            @Override
            public ValuableMethod mapRow(ResultSet resultSet, int i) throws SQLException {

                ValuableMethod observedMethod = new ValuableMethod(
                        resultSet.getString(2),
                        resultSet.getLong(3));
                observedMethod.setServiceName(resultSet.getString(1));
                return observedMethod;
            }
        });
        return valuableMethods;
    }
}
