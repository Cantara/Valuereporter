package org.valuereporter.implemented;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
public class ImplementedMethodDao {
    private static final Logger log = LoggerFactory.getLogger(ImplementedMethodDao.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ImplementedMethodDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ImplementedMethod> findImplementedMethodsByPrefix(String serviceName) {
        String sql = "SELECT serviceName,methodName FROM ImplementedMethod WHERE serviceName = ? ORDER BY methodName ASC ";
        Object[] parameters = new Object[] {serviceName};
        List<ImplementedMethod> implementedMethods = jdbcTemplate.query(sql, parameters, new RowMapper<ImplementedMethod>() {
            @Override
            public ImplementedMethod mapRow(ResultSet resultSet, int i) throws SQLException {
                //log.debug("Returned values: {},{}", resultSet.getObject(1),resultSet.getObject(2));

                ImplementedMethod implementedMethod = new ImplementedMethod(
                        resultSet.getString(1),
                        resultSet.getString(2));
                return implementedMethod;
            }
        });
        return implementedMethods;
    }
    public List<ImplementedMethod> findImplementedMethods(String serviceName, String name) {
        String sql = "SELECT serviceName,methodName FROM ImplementedMethod WHERE serviceName = ? AND methodName = ? ORDER BY methodName ASC ";
        Object[] parameters = new Object[] {serviceName,name};
        List<ImplementedMethod> implementedMethods = jdbcTemplate.query(sql, parameters, new RowMapper<ImplementedMethod>() {
            @Override
            public ImplementedMethod mapRow(ResultSet resultSet, int i) throws SQLException {
                //log.debug("Returned values: {},{}", resultSet.getObject(1),resultSet.getObject(2));

                ImplementedMethod implementedMethod = new ImplementedMethod(
                        resultSet.getString(1),
                        resultSet.getString(2));
                return implementedMethod;
            }
        });
        return implementedMethods;
    }



    public int addAll(final List<ImplementedMethod> implementedMethods) {
        String sql = "INSERT INTO "
                + "ImplementedMethod "
                + "(serviceName,methodName) "
                + "VALUES " + "(?,?)";

        int inserted = 0;
        for (ImplementedMethod implementedMethod : implementedMethods) {
            try {
                log.debug("Insert {}, serviceName {}, methodName {}", sql, implementedMethod.getPrefix(), implementedMethod.getName());
                jdbcTemplate.update(sql, implementedMethod.getPrefix(), implementedMethod.getName());
                inserted ++;
            } catch (DuplicateKeyException dke) {
                //do nothing
                log.trace("Ignored exception. ", dke.getMessage());
            }
        }
        return inserted;
    }

    public List<String> findImplementedPrefixes() {
        String sql = "SELECT DISTINCT serviceName FROM ImplementedMethod ORDER BY serviceName ASC ;";
        List<String> implementedMethods = jdbcTemplate.queryForList(sql, String.class);
        return implementedMethods;

    }
}
