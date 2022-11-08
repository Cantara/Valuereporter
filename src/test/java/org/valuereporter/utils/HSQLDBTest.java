package org.valuereporter.utils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HSQLDBTest {

    private static final Logger log = LoggerFactory.getLogger(HSQLDBTest.class);
    private final String jdbcDriverClassName = "";
    private final String jdbcUrl = "";
    private final String jdbcUserName = "";
    private final String jdbcPassword = "";
    private final String jdbcAdminPassword = "";

    @Test
    public void testHSQLDB() {
        boolean isExistingDb = false;


        String hsqldbUrl = this.jdbcUrl + ";ifexists=true";
        log.info("Try to connect to existing database with url {}", hsqldbUrl);

        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (Exception e) {
            System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
            e.printStackTrace();

        }

        //DriverManager.getConnection(hsqldbUrl, this.jdbcUserName, this.jdbcPassword);
        //isExistingDb = true;
    }
}
