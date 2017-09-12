package org.valuereporter.activity;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.valuereporter.activity.timeseries.TimeseriesConnection;
import org.valuereporter.whydah.LogonDao;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by baardl on 12.09.17.
 */
public class ActivitiesServiceTest {

    private ActivitiesService activitiesService = null;
    private ActivitiesDao activitiesDao;
    private LogonDao logonDao;
    private String timeseriesUrl = "http://localhost/timeseries";
    private String timeseriesDbName = "default";
    private String timeseriesUsername = "defUser";
    private String timeseriesPassword = "devPW";

    @BeforeMethod
    public void setUp() throws Exception {
        activitiesDao = mock(ActivitiesDao.class);
        logonDao = mock(LogonDao.class);
        activitiesService = new ActivitiesService(activitiesDao, logonDao, timeseriesUrl, timeseriesDbName, timeseriesUsername, timeseriesPassword);
        System.setProperty("CONSTRETTO_TAGS", "junit");
    }

    @Test
    public void testFindConnection() throws Exception {
        TimeseriesConnection defaultConnection = activitiesService.findConnection(null);
        assertNotNull(defaultConnection);
        assertEquals(defaultConnection.getPrefix(), ActivitiesService.DEFAULT_DATABASE);
        assertEquals(defaultConnection.getDatabaseName(),timeseriesDbName);

    }

    @Test
    public void testFindConnectionInProperties() throws Exception {
        TimeseriesConnection connection = activitiesService.findConnectionInProperties("test");
        assertNotNull(connection);
        assertEquals(connection.getPrefix(),"test");
    }

}