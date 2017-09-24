package org.valuereporter.activity.timeseries;

/**
 * Created by baardl on 12.09.17.
 */
public class TimeseriesConnection {
    private final String serviceName;
    private final String databaseName;
    private final String username;
    private final String password;

    public TimeseriesConnection(String serviceName, String databaseName, String username, String password) {
        this.serviceName = serviceName;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
