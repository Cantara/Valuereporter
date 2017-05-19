package org.valuereporter.helper;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.valuereporter.Main;
import org.valuereporter.ValuereporterException;
import org.valuereporter.ValuereporterTechnicalException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Delegate for Main for performing database migrations before application startup.
 */
public class DatabaseMigrationHelper {
    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationHelper.class);
    public static final String ADMIN_USERNAME = "admin.connection.username";
    public static final String ADMIN_PASSWORD = "admin.connection.password";
    public static final String JDBC_URL = "jdbc.url";
    private Flyway flyway;
    private final Properties properties;

    public DatabaseMigrationHelper(Properties resources) {
        this.properties = resources;
        try {
//            DataSource masterDs = createMasterDataSource(resources);
            flyway = new Flyway();
            flyway.setValidateOnMigrate(false);
            String jdbcUrl = (String) properties.get(JDBC_URL);
            String adminUser = (String) properties.get(ADMIN_USERNAME);
            String adminPassword = (String) properties.get(ADMIN_PASSWORD);
            flyway.setDataSource(jdbcUrl, adminUser, adminPassword);
//            flyway.setDataSource(masterDs);
        } catch (Exception e) {
            ValuereporterTechnicalException tte = new ValuereporterTechnicalException("Error migrating the database. Please verify properties are in place " +
                    ", " + ADMIN_USERNAME + ", " + ADMIN_PASSWORD + ",admin.connection.host, admin.connection.port, admin.connection.databasename", e, StatusType.RETRY_NOT_POSSIBLE);
            Marker fatal = MarkerFactory.getMarker("FATAL");
            log.error(fatal, "No-Recoverable Error: " + tte.getMessage(), tte);
            throw tte;
        }
    }

    public void upgradeDatabase() {
        Properties configuration = null;
        try {
            configuration = new Properties();
            configuration.setProperty("flyway.validateOnMigrate", "false");
            String sqlLocations = buildSqlMigrationLocations();
            configuration.setProperty("flyway.locations", sqlLocations);
            flyway.configure(configuration);
            flyway.migrate();
        } catch (FlywayException e) {
            log.error("Failed to upgrade using Flyway. Configuration \n{}. \nReason {}. ", properties,e.getMessage(), e);
            throw new ValuereporterTechnicalException("Database upgrade failed.", e,StatusType.RETRY_NOT_POSSIBLE);
        }
    }

    private String buildSqlMigrationLocations() {
        String defaultLocations = "db/migration/mysql";
        String locations = defaultLocations;
        return locations;

    }

    public void cleanDatabase() {
        try {
            flyway.clean();
        } catch (FlywayException e) {
            throw new RuntimeException("Database upgrade failed.", e);
        }
    }

    /**
     * Create datasource for the admin connection used by the database upgrade framework Flyway.
     *
     * The JtdsDataSource API does not support standard JDBCs URIs directly, so we require host, port and database name to be set in persistence.properties.
     * @return  a new DataSource
     * @param resources
     */
    private DataSource createMasterDataSource(Properties resources) throws ValuereporterException {


        if (resources == null ) {
            log.error("Missing properties for database migration, can not start the application.");
            throw new ValuereporterTechnicalException("No properties for database migration are found. Look for properties like \"" + ADMIN_USERNAME + "\"", StatusType.RETRY_NOT_POSSIBLE );
        }

        String dbUserName = resources.getProperty(ADMIN_USERNAME);
        String dbUserPassword = resources.getProperty(ADMIN_PASSWORD);

        String host = resources.getProperty("admin.connection.host");
        String portAsString = resources.getProperty("admin.connection.port");
        int port = Integer.parseInt(portAsString);
        String databasename = resources.getProperty("admin.connection.databasename");
        if (isNullOrEmpty(dbUserName) || isNullOrEmpty(dbUserPassword) || isNullOrEmpty(host) || isNullOrEmpty(databasename)) {
            StringBuilder strb = new StringBuilder("Master connection properties not set in persistence configuration.\n");
            strb.append(ADMIN_USERNAME + "=").append(dbUserName);
            strb.append(", ");
            strb.append(ADMIN_PASSWORD + "=").append(dbUserPassword);
            strb.append(", ");
            strb.append("admin.connection.host=").append(host);
            strb.append(", ");
            strb.append("admin.connection.port=").append(portAsString);
            strb.append(", ");
            strb.append("admin.connection.databasename=").append(databasename);
            throw new IllegalStateException(strb.toString());
        }

        /*
        JtdsDataSource ds = new JtdsDataSource();
        ds.setUser(dbUserName);
        ds.setPassword(dbUserPassword);
        ds.setServerName(host);
        ds.setPortNumber(port);
        ds.setDatabaseName(databasename);
        return ds;
        */
        throw new NotImplementedException();
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * State which databases that are supported by Flyway.
     * @param resources
     * @return
     */
    public static boolean isFlywaySupported(Properties resources) {
        boolean isSupported = false;
        String url = resources.getProperty(Main.DATABASE_URL);
        if (url != null && url.contains("mysql")) {
            isSupported = true;
        } else {
            log.warn("Currently database migration supports mysql only. Database will not be automatically upgraded.");
        }
        return isSupported;
    }
}
