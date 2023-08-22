package org.valuereporter.observation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.valuereporter.activity.ObservedActivityJson;

import java.util.ArrayList;

import static org.testng.Assert.assertTrue;

public class ObservedActivityJsonMapperTest {


    ObjectMapper mapper = new ObjectMapper();

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @Test
    public void testjsonFromTheWild() throws Exception {
        ArrayList<ObservedActivityJson> activities = mapper.readValue(jsonFromTheWild, new TypeReference<ArrayList<ObservedActivityJson>>() {
        });
        assertTrue(activities.size() > 0);

    }

    String jsonFromTheWild = "[\n" +
            "  {\n" +
            "    \"serviceName\": \"\",\n" +
            "    \"activityName\": \"userSession\",\n" +
            "    \"startTime\": 1692704044798,\n" +
            "    \"contextInfo\": {\n" +
            "      \"usersessionfunction\": \"userSessionAccess\",\n" +
            "      \"applicationid\": \"2311\",\n" +
            "      \"userid\": \"b465a1cb-1245-4b00-9b40-7340fcd0679a\",\n" +
            "      \"applicationtokenid\": \"4d0801090ed5fd1ea345525f7f627bc3\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"serviceName\": \"\",\n" +
            "    \"activityName\": \"userSession\",\n" +
            "    \"startTime\": 1692704047013,\n" +
            "    \"contextInfo\": {\n" +
            "      \"usersessionfunction\": \"userSessionVerification\",\n" +
            "      \"applicationid\": \"2210\",\n" +
            "      \"userid\": \"oauth2admin\",\n" +
            "      \"applicationtokenid\": \"03b50f19d28faa204b04c877a69fbff3\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"serviceName\": \"\",\n" +
            "    \"activityName\": \"userSession\",\n" +
            "    \"startTime\": 1692704047018,\n" +
            "    \"contextInfo\": {\n" +
            "      \"usersessionfunction\": \"userSessionAccess\",\n" +
            "      \"applicationid\": \"2210\",\n" +
            "      \"userid\": \"oauth2admin\",\n" +
            "      \"applicationtokenid\": \"03b50f19d28faa204b04c877a69fbff3\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"serviceName\": \"\",\n" +
            "    \"activityName\": \"userSession\",\n" +
            "    \"startTime\": 1692704047270,\n" +
            "    \"contextInfo\": {\n" +
            "      \"usersessionfunction\": \"userSessionVerification\",\n" +
            "      \"applicationid\": \"2212\",\n" +
            "      \"userid\": \"b465a1cb-1245-4b00-9b40-7340fcd0679a\",\n" +
            "      \"applicationtokenid\": \"d9ddadfd3526fc7d1ca6df04c04d69b3\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"serviceName\": \"\",\n" +
            "    \"activityName\": \"userSession\",\n" +
            "    \"startTime\": 1692704047277,\n" +
            "    \"contextInfo\": {\n" +
            "      \"usersessionfunction\": \"userSessionAccess\",\n" +
            "      \"applicationid\": \"2212\",\n" +
            "      \"userid\": \"b465a1cb-1245-4b00-9b40-7340fcd0679a\",\n" +
            "      \"applicationtokenid\": \"d9ddadfd3526fc7d1ca6df04c04d69b3\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"serviceName\": \"\",\n" +
            "    \"activityName\": \"userSession\",\n" +
            "    \"startTime\": 1692704047804,\n" +
            "    \"contextInfo\": {\n" +
            "      \"usersessionfunction\": \"userSessionVerification\",\n" +
            "      \"applicationid\": \"2211\",\n" +
            "      \"userid\": \"ssolwaadmin\",\n" +
            "      \"applicationtokenid\": \"8b9522d1edcbc023a98e5ce2239d8ae0\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"serviceName\": \"\",\n" +
            "    \"activityName\": \"userSession\",\n" +
            "    \"startTime\": 1692704047809,\n" +
            "    \"contextInfo\": {\n" +
            "      \"usersessionfunction\": \"userSessionAccess\",\n" +
            "      \"applicationid\": \"2211\",\n" +
            "      \"userid\": \"ssolwaadmin\",\n" +
            "      \"applicationtokenid\": \"8b9522d1edcbc023a98e5ce2239d8ae0\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"serviceName\": \"\",\n" +
            "    \"activityName\": \"userSession\",\n" +
            "    \"startTime\": 1692704047826,\n" +
            "    \"contextInfo\": {\n" +
            "      \"usersessionfunction\": \"userSessionCreatedByPassword\",\n" +
            "      \"applicationid\": \"2211\",\n" +
            "      \"userid\": \"ssolwaadmin\",\n" +
            "      \"applicationtokenid\": \"8b9522d1edcbc023a98e5ce2239d8ae0\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"serviceName\": \"\",\n" +
            "    \"activityName\": \"userSession\",\n" +
            "    \"startTime\": 1692704047836,\n" +
            "    \"contextInfo\": {\n" +
            "      \"usersessionfunction\": \"userSessionVerification\",\n" +
            "      \"applicationid\": \"2211\",\n" +
            "      \"userid\": \"b465a1cb-1245-4b00-9b40-7340fcd0679a\",\n" +
            "      \"applicationtokenid\": \"8b9522d1edcbc023a98e5ce2239d8ae0\"\n" +
            "    }\n" +
            "  },\n" +
            "  {\n" +
            "    \"serviceName\": \"\",\n" +
            "    \"activityName\": \"userSession\",\n" +
            "    \"startTime\": 1692704047844,\n" +
            "    \"contextInfo\": {\n" +
            "      \"usersessionfunction\": \"userSessionAccess\",\n" +
            "      \"applicationid\": \"2211\",\n" +
            "      \"userid\": \"b465a1cb-1245-4b00-9b40-7340fcd0679a\",\n" +
            "      \"applicationtokenid\": \"8b9522d1edcbc023a98e5ce2239d8ae0\"\n" +
            "    }\n" +
            "  }\n" +
            "]";
}
