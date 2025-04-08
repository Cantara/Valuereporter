package org.valuereporter.activity;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Created by t-blind5-01 on 02.03.2016.
 */
public class ActivitiesDaoTest {

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @Test
    public void testBuildSql() throws Exception {
        ActivitiesDao activitiesDao = new ActivitiesDao(null,null);
        List<String> activities = new ArrayList<>();
        activities.add("userId");
        activities.add("applicationId");

        String stattime = ActivitiesDao.START_TIME_COLUMN;
        String expectedSql = "INSERT INTO logonByApplication(" + stattime+", userId, applicationId) VALUES (?,?,?)";
        String sql = activitiesDao.buildSql("logonByApplication", activities);
        assertEquals(sql.toLowerCase(), expectedSql.toLowerCase());

    }
}