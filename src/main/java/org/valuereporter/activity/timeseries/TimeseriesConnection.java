package org.valuereporter.activity.timeseries;

/**
 * Created by baardl on 12.09.17.
 */
public class TimeseriesConnection {
    private final String prefix;
    private final String databaseName;
    private final String username;
    private final String password;

    public TimeseriesConnection(String prefix, String databaseName, String username, String password) {
        this.prefix = prefix;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    public String getPrefix() {
        return prefix;
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
