package org.valuereporter.helper;


import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.ValuereporterException;
import org.valuereporter.ValuereporterTechnicalException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Setting up the embedding database. Hsqldb.
 * See http://hsqldb.org/doc/guide/running-chapt.html#rgc_new_db
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class EmbeddedDatabaseHelper {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedDatabaseHelper.class);
    private static final String USE_EMBEDDED_KEY = "jdbc.useEmbedded";
    private static final String DEFAULT_JDBC_URL = "jdbc:hsqldb:file:db/hsqldb/ValueReporter";
    private static final String JDBC_URL_KEY = "jdbc.url";
    private static final String DEFAULT_JDBC_USERNAME = "vr";
    private static final String JDBC_USERNAME_KEY = "jdbc.username";
    private static final String DEFAULT_JDBC_PASSWORD = "vr1234";
    private static final String JDBC_PASSWORD_KEY = "jdbc.password";
    private static final String DEFAULT_JDBC_ADMIN_USERNAME = "vrAdmin";
    private static final String JDBC_ADMIN_USERNAME_KEY = "admin.connection.username";
    private static final String DEFAULT_JDBC_ADMIN_PASSWORD = "vrAdmin1234";
    private static final String JDBC_ADMIN_PASSWORD_KEY = "admin.connection.password";
    private static final String DEFAULT_JDBC_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
    private static final String JDBC_DRIVER_NAME_KEY = "jdbc.driverClassName";
    private static final String SETUP_NEW_DATABASE = "jdbc.setupNewDb";
    private final QueryRunner queryRunner;
    private final String jdbcDriverClassName;
    private final String jdbcUrl;
    private final String jdbcUserName;
    private final String jdbcPassword;
    private final String jdbcAdminPassword;
    private boolean useEmbeddedDb = true;
    private boolean setupNewDatabase = true;
    private String jdbcAdminUserName;


    public EmbeddedDatabaseHelper(Properties resources) throws ClassNotFoundException {
        queryRunner = new QueryRunner();
        useEmbeddedDb = useEmbeddedDb(resources);
        setupNewDatabase = doSetupNewDatabase(resources);
        log.info("Using embedded database {}", useEmbeddedDb);
        this.jdbcDriverClassName = resources.getProperty(JDBC_DRIVER_NAME_KEY, DEFAULT_JDBC_DRIVER);
        this.jdbcUrl = resources.getProperty(JDBC_URL_KEY, DEFAULT_JDBC_URL);
        this.jdbcUserName = resources.getProperty(JDBC_USERNAME_KEY, DEFAULT_JDBC_USERNAME);
        this.jdbcPassword = resources.getProperty(JDBC_PASSWORD_KEY, DEFAULT_JDBC_PASSWORD);
        this.jdbcAdminUserName = resources.getProperty(JDBC_ADMIN_USERNAME_KEY, DEFAULT_JDBC_ADMIN_USERNAME);
        this.jdbcAdminPassword = resources.getProperty(JDBC_ADMIN_PASSWORD_KEY, DEFAULT_JDBC_ADMIN_PASSWORD);

        try {
            Class.forName(jdbcDriverClassName);
        } catch (ClassNotFoundException e) {
            log.warn("Database could not be opened. Class for database driver {} could not be found.", jdbcDriverClassName);
            throw e;
        }

    }



    public void initializeDatabase() {
        if (useEmbeddedDb && isHSQLdbAvailable()) {
            log.info("Embedded Database is selected, and available at url {}", jdbcUrl);
            log.info("Automatic upgrade of Embedded Database is currently not supported. To update to a new schema, you will need to delete the database at {}, and restart ValueReporter. ", jdbcUrl);
            return;
        }

        if (useEmbeddedDb) {
            log.info("Creating new database at url {}", jdbcUrl);
            Connection connection = connectToHsqldb();
            try {
                if (connection == null || connection.isClosed()){
                  throw  new ValuereporterTechnicalException("Failed to open database at URL " + jdbcUrl, StatusType.connection_error);
                }
            } catch (SQLException e) {
                throw  new ValuereporterTechnicalException("Failed to open database at URL " + jdbcUrl, e,StatusType.connection_error);
            }
            if (setupNewDatabase) {
                createUsers(connection);
                createTables(connection);
                insertValues(connection);
            }

        }

    }

    public static boolean useEmbeddedDb(Properties resources) {
        boolean useEmbedded = true;
        String useEmbeddedDbValue = resources.getProperty(USE_EMBEDDED_KEY);
        if (useEmbeddedDbValue != null && useEmbeddedDbValue.equalsIgnoreCase("false")) {
            useEmbedded =  false;
        }
        return useEmbedded;
    }

    public static boolean doSetupNewDatabase(Properties resources) {
        boolean setupNewDb = false;
        String setupNewDbValue = resources.getProperty(SETUP_NEW_DATABASE);
        if (setupNewDbValue != null && setupNewDbValue.equalsIgnoreCase("true")) {
            setupNewDb =  true;
        }
        return setupNewDb;
    }

    public Connection connectToHsqldb() {

        Connection c = null;
        try {
           // log.warn("Using DriverManager {}
            log.info("ConnectToHsqldb on url {}, using driver {}.", jdbcUrl,jdbcDriverClassName);
//            Class.forName(jdbcDriverClassName);
            c = DriverManager.getConnection(jdbcUrl, jdbcUserName, jdbcPassword);
        } catch (SQLException e) {
            log.info("Database is not available {}, user {}, jdbcDriver {}, reason {}", jdbcUrl, jdbcUserName,jdbcDriverClassName, e);
        }
        return c;

    }

    public boolean isHSQLdbAvailable() {
        boolean isExistingDb = false;
        try {
            String hsqldbUrl = jdbcUrl + ";ifexists=true";
            log.info("Try to connect to existing database with url {}", hsqldbUrl);
            try {
                Class.forName("org.hsqldb.jdbc.JDBCDriver" );
            } catch (Exception e) {
                System.err.println("ERROR: failed to load HSQLDB JDBC driver.");
                e.printStackTrace();
                return false;
            }

            
            DriverManager.getConnection(hsqldbUrl, jdbcUserName, jdbcPassword);
            isExistingDb = true;
        } catch (SQLException e) {
            log.info("Database does not exits, or is not available to the user. It will have to be created. url {}, user {}.", jdbcUrl, jdbcUserName);
        }
        return isExistingDb;
    }

    protected void createUsers(Connection connection) {
        log.info("Creating Basic users.");
        try {
            queryRunner.update(connection, "CREATE USER " + jdbcUserName + " PASSWORD " + jdbcPassword + ";");
            queryRunner.update(connection, "CREATE USER " + jdbcAdminUserName + " PASSWORD " + jdbcAdminPassword + ";");
            queryRunner.update(connection,"GRANT DBA TO " + jdbcAdminUserName +";");
        } catch (SQLException e) {
            log.info("Error creating tables", e);
        }
    }

    protected void createTables(Connection connection) {
        log.info("Creating basic tables.");
        try {
            createObservedMethod(connection);
            createImplementedMethod(connection);
            createObservedKeys(connection);
        } catch (SQLException e) {
            throw new ValuereporterException("Error creating tables. Intiialization fails", e, StatusType.RETRY_NOT_POSSIBLE);
        }
    }

    private void createObservedKeys(Connection connection) throws SQLException {
        String tableSql = "CREATE TABLE ObservedKeys(\n" +
                "      id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,\n" +
                "      prefix varchar(255) NOT NULL,\n" +
                "      methodName varchar(255) NOT NULL,\n" +
                "      CONSTRAINT AK_Prefix_MethodName UNIQUE (prefix, methodName)\n" +
                "    );";
        queryRunner.update(connection,tableSql);

        String observedInterval = " create table ObservedInterval (\n" +
                "      id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,\n" +
                "      observedKeysId bigint NOT NULL ,\n" +
                "      startTime datetime NOT NULL,\n" +
                "      duration bigint not null,\n" +
                "      vrCount bigint NOT NULL,\n" +
                "      vrMax bigint not null,\n" +
                "      vrMin bigint not null,\n" +
                "      vrMean decimal not null,\n" +
                "      vrMedian decimal not null,\n" +
                "      stdDev decimal not null,\n" +
                "      p95 decimal,\n" +
                "      p98 decimal,\n" +
                "      p99 decimal,\n" +
                "      FOREIGN KEY (observedKeysId) REFERENCES ObservedKeys(id)\n" +
                "    );";
        queryRunner.update(connection, observedInterval);
    }

    private void createObservedMethod(Connection connection) throws SQLException {
        String tableSql = ("CREATE TABLE ObservedMethod (" +
                "  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY," +
                "  prefix varchar(255) NOT NULL," +
                "  methodName varchar(255) NOT NULL," +
                "  startTime TIMESTAMP ," +
                "  endTime TIMESTAMP ," +
                "  duration INTEGER" +
                "); ");


        queryRunner.update(connection,tableSql);
    }

    private void createImplementedMethod(Connection connection) throws SQLException {
        String tableSql = ("CREATE TABLE ImplementedMethod (" +
                "  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY," +
                "  prefix varchar(255) NOT NULL," +
                "  methodName varchar(255) NOT NULL," +
                "  CONSTRAINT IMK_PrefixMethodName UNIQUE(prefix, methodName)"+
                "); ");


        queryRunner.update(connection,tableSql);
    }

    private void insertValues(Connection connection)  {
        log.info("Inserting default data.");
        try {
            queryRunner.update(connection, "insert into ObservedMethod (prefix,methodName, startTime, endTime, duration) values ('inital', 'com.valuereporter.test', '2014-05-13 12:02:43.296','2014-05-13 12:02:43.596',300);");
            queryRunner.update(connection, "insert into ImplementedMethod (prefix,methodName) values ('inital', 'com.valuereporter.test');");
            queryRunner.update(connection, "insert into ObservedKeys(prefix, methodName) values ('initial', 'com.valuereporter.test');");
            queryRunner.update(connection, "insert into ObservedInterval (observedKeysId, startTime, duration, vrCount, vrMax, vrMean,vrMin,vrMedian, stdDev)\n" +
                "      select o.id, '2016-03-07 12:02:43.296', 15*60*1000, 4, 50, 5.0, 2,0,0\n" +
                "      from ObservedKeys o\n" +
                "      where prefix='initial' and methodName = 'com.valuereporter.test';");
        } catch (SQLException e) {
            throw new ValuereporterException("Error creating tables. Intiialization fails", e, StatusType.RETRY_NOT_POSSIBLE);
        }

    }



    /*
    private BasicDataSource getDataSource() {
        String jdbcdriver = AppConfig.appConfig.getProperty("roledb.jdbc.driver");
        String jdbcurl = AppConfig.appConfig.getProperty("roledb.jdbc.url");
        String roledbuser = AppConfig.appConfig.getProperty("roledb.jdbc.user");
        String roledbpasswd = AppConfig.appConfig.getProperty("roledb.jdbc.password");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(jdbcdriver);
        dataSource.setUrl(jdbcurl);//"jdbc:hsqldb:file:" + basepath + "hsqldb");
        dataSource.setUsername(roledbuser);
        dataSource.setPassword(roledbpasswd);
        return dataSource;
    }
    */
}
