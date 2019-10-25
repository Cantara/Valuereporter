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

    public List<ImplementedMethod> findImplementedMethodsByPrefix(String prefix) {
        String sql = "SELECT prefix,methodName FROM ImplementedMethod WHERE prefix = ? ORDER BY methodName ASC ";
        Object[] parameters = new Object[] {prefix};
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
    public List<ImplementedMethod> findImplementedMethods(String prefix, String name) {
        String sql = "SELECT prefix,methodName FROM ImplementedMethod WHERE prefix = ? AND methodName = ? ORDER BY methodName ASC ";
        Object[] parameters = new Object[] {prefix,name};
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


    public int addAll(final List<ImplementedMethodJson> implementedMethods) {
        String sql = "INSERT INTO "
                + "ImplementedMethod "
                + "(prefix,methodName) "
                + "VALUES " + "(?,?)";

        int inserted = 0;
        for (ImplementedMethod implementedMethod : implementedMethods) {
            try {
                log.debug("Insert {}, prefix {}, methodName {}", sql, implementedMethod.getPrefix(), implementedMethod.getName());
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
        String sql = "SELECT DISTINCT prefix FROM ImplementedMethod ORDER BY prefix ASC ;";
        List<String> implementedMethods = jdbcTemplate.queryForList(sql, String.class);
        return implementedMethods;

    }
}
