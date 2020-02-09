package org.valuereporter.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Created by baardl on 18/05/2016.
 */
public class ActivitiesServiceStreamTest {
    private static final Logger log = getLogger(ActivitiesServiceStreamTest.class);
    private ObjectMapper mapper;
    List<UserSessionObservedActivity> observedActivities;
    String byPassword = "userSessionCreatedByPassword";
    String byPin = "userSessionCreatedByPin";
    String appToken1 = "appToken1";
    String appToken2 = "appToken2";

    @BeforeClass
    public void beforeClass() throws Exception{
        mapper = new ObjectMapper();

    }

    @BeforeMethod
    public void setUp() throws Exception {
        observedActivities = new ArrayList<>();

    }

    @Test
    public void testUpdateActivities() throws Exception {

    }

    @Test
    public void testUpdateActivitiesByName() throws Exception {
        List<ObservedActivity> list = new ArrayList<>();

        list.add(buildStubedActivity("logon"));
        list.add(buildStubedActivity("Hello"));
        list.add(buildStubedActivity("World"));
        list.add(buildStubedActivity("logon"));

        Map<String, Long> counted = list.stream().collect(groupingBy(ObservedActivity::getName, counting()));

        assertEquals(4, list.size());
        assertEquals(2, counted.get("logon").longValue());
        assertEquals(3, counted.size());

    }

    @Test
    public void testUserSessionActivities() throws Exception{
        UserSessionObservedActivity user1 = new UserSessionObservedActivity("user1", byPassword, appToken1);
        assertEquals(UserSessionObservedActivity.USER_SESSION_ACTIVITY, user1.getName());
        observedActivities.add(user1);
        observedActivities.add(new UserSessionObservedActivity("user1", byPassword, appToken2));
        observedActivities.add(new UserSessionObservedActivity("user1", byPin, appToken1));
        String json = this.mapper.writeValueAsString(observedActivities);
        //  assertTrue(json.contains("ByPin"));
        log.debug("Activities json {}", json);
        assertEquals(observedActivities.size(), 3);

        Map<String, Long> counted = observedActivities.stream().collect(groupingBy(UserSessionObservedActivity::getUserIdFunction, counting()));
//        assertEquals(counted.get(UserSessionObservedActivity.USER_SESSION_ACTIVITY).longValue(),2);
        assertEquals(counted.size(), 1);

        Map<String, Long> byActivity = observedActivities.stream().collect(groupingBy(UserSessionObservedActivity::getUserSessionFunction, counting()));

        assertEquals(byActivity.get(byPassword).longValue(), 2);
        assertEquals(byActivity.size(), 2);
    }

    @Test
    public void testDatedUserSessionActivities() throws Exception{

        Instant yesterday = Instant.now().minus(1, ChronoUnit.DAYS);
        ZonedDateTime localYesterday = ZonedDateTime.ofInstant(yesterday, ZoneId.systemDefault());
        long yesterdayMillis = yesterday.toEpochMilli();
        long yesterdayAbitEarlierMillis = yesterday.minus(10, ChronoUnit.SECONDS).toEpochMilli();
        observedActivities.add(new UserSessionObservedActivity("user1", byPassword, appToken1, yesterdayMillis));
        observedActivities.add(new UserSessionObservedActivity("user1", byPassword, appToken2));
        observedActivities.add(new UserSessionObservedActivity("user1", byPin, appToken1,yesterdayAbitEarlierMillis));
        String json = mapper.writeValueAsString(observedActivities);
        assertTrue(json.contains(Long.toString(yesterdayMillis)));
        log.debug("Activities json {}", json);

        Map<Integer, Long> byDayOfMonth = observedActivities.stream().collect(groupingBy(UserSessionObservedActivity::getActivityDate, counting()));

        Long countActivitiesYesterday = byDayOfMonth.get(localYesterday.getDayOfMonth());
        assertNotNull(countActivitiesYesterday);
        assertEquals(countActivitiesYesterday.longValue(),2);
        int daysOfActivities = byDayOfMonth.size();
        assertEquals(daysOfActivities,2);
    }

    private ObservedActivity buildStubedActivity(String activity) {
        return new ObservedActivity(activity, System.currentTimeMillis(),null);
    }

    @Test
    public void testFindLogonsByUserid() throws Exception {

    }

    public class UserSessionObservedActivity extends org.valuereporter.activity.ObservedActivity {
        public static final String USER_SESSION_ACTIVITY = "userSession";
        private static final String USER_SESSION_ACTIVITY_DB_KEY = "userid";

        public UserSessionObservedActivity(String name, long time) {
            super(name, time, null);
        }

        public UserSessionObservedActivity(String userid, String userSessionActivity, String applicationtokenid) {
            this(userid, userSessionActivity,applicationtokenid,System.currentTimeMillis());

        }

        public UserSessionObservedActivity(String userid, String userSessionActivity, String applicationtokenid, long timestamp) {
            this(USER_SESSION_ACTIVITY, timestamp);
            String applicationid = "app" + applicationtokenid;
            getData().put("userid", userid);
            put("userid", userid);
            getData().put("usersessionfunction", userSessionActivity);
            put("usersessionfunction", userSessionActivity);
            getData().put("applicationtokenid", applicationtokenid);
            put("applicationtokenid", applicationtokenid);
            getData().put("applicationid", applicationid);
            put("applicationid", applicationid);
        }

        public String getUserSessionFunction() {
            return (String) getData().get("usersessionfunction");
        }

        public String getUserIdFunction() {
            return (String) getData().get("userid");
        }

        public int getActivityDate() {

            Instant startTime = Instant.ofEpochMilli(getStartTime());
            ZonedDateTime localStartTime = ZonedDateTime.ofInstant(startTime, ZoneId.systemDefault());
            return localStartTime.getDayOfMonth();
        }
    }


}