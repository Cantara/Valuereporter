package org.valuereporter.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Created by baardl on 21.09.17.
 */
public class ActivitiesResourceTest {
    private ActivitiesResource activitiesResource;
    private ObjectMapper objectMapper = new ObjectMapper();
    private ActivitiesService activitiesService;
    @BeforeMethod
    public void setUp() throws Exception {
        activitiesService = mock(ActivitiesService.class);
        activitiesResource = new ActivitiesResource(objectMapper, activitiesService);
    }

    @Test
    public void testBuildObservedActivitiesFromJson() throws Exception {
        String jsonBody = "[{\"serviceName\": \"test\",\"activityName\": \"userSession\",\"startTime\": 1506003702276,\"contextInfo\": {\"usersessionfunction\": \"someActivity\",\"started-at\": \"1506003704598\",\"applicationid\": \"SomeArbitraryId\",\"userid\": \"testuser11506003702220\",\"applicationtokenid\": \"application1\"}}]";
        List<ObservedActivity> observedActivities = activitiesResource.buildObservedActivitiesFromJson(jsonBody);
        assertEquals(observedActivities.size(), 1);
        ObservedActivity observedActivity = observedActivities.get(0);
        assertEquals(observedActivity.getServiceName(), "test");
        assertEquals(observedActivity.getActivityName(), "userSession");
        assertEquals(observedActivity.getStartTime(), 1506003702276L);
        assertNotNull(observedActivity.getContextInfo());
    }

    @Test
    public void testBuildObservedActivitiesFromDeprecatedJson() throws Exception {
        String jsonBody = "[{\"serviceName\": \"test\",\"activityName\": \"userSession\",\"startTime\": 1506003702276,\"data\": {\"usersessionfunction\": \"someActivity\",\"started-at\": \"1506003704598\",\"applicationid\": \"SomeArbitraryId\",\"userid\": \"testuser11506003702220\",\"applicationtokenid\": \"application1\"}}]";
        List<ObservedActivity> observedActivities = activitiesResource.buildObservedActivitiesFromJson(jsonBody);
        assertEquals(observedActivities.size(), 1);
        ObservedActivity observedActivity = observedActivities.get(0);
        assertEquals(observedActivity.getServiceName(), "test");
        assertEquals(observedActivity.getActivityName(), "userSession");
        assertEquals(observedActivity.getStartTime(), 1506003702276L);
        assertNotNull(observedActivity.getContextInfo());
    }

}