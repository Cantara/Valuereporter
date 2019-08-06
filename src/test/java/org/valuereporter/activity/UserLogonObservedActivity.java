package org.valuereporter.activity;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;

/**
 * Created by baardl on 04.03.16.
 */
public class UserLogonObservedActivity extends org.valuereporter.activity.ObservedActivity {
    public static final String USER_LOGON_ACTIVITY = "userLogon";
    private static final String USER_LOGON_USERID_DB_KEY = "userid";

    public UserLogonObservedActivity(String userid) {
        super(USER_LOGON_ACTIVITY, System.currentTimeMillis(), null);
        put("userid", userid);
    }
}
