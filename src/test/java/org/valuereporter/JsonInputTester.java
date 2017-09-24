package org.valuereporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.observation.ObservedMethod;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class JsonInputTester {
    private static final Logger log = LoggerFactory.getLogger(JsonInputTester.class);

    private ObjectMapper mapper = new ObjectMapper();
    private static final int STATUS_BAD_REQUEST = 400; //Response.Status.BAD_REQUEST.getStatusCode();
    private static final int STATUS_OK = 200; //Response.Status.OK.getStatusCode();
    private static final int STATUS_FORBIDDEN = 403;

    private static String reporterHost;
    private static String reporterPort;
    private static String serviceName;

    public JsonInputTester(String reporterHost, String reporterPort, String serviceName) {
        this.reporterHost = reporterHost;
        this.reporterPort = reporterPort;
        this.serviceName = serviceName;
    }

    public static void main(String[] args) {
        JsonInputTester tester = new JsonInputTester("localhost", "4901", "testserviceName");
        List<ObservedMethod> observedMethods = observedMethodsStub();
        tester.pushSomeData(observedMethods);

    }

    private static List<ObservedMethod> observedMethodsStub() {
            List<ObservedMethod> observedMethods = new ArrayList<>();
            long end = System.currentTimeMillis();
            long start = new DateTime(end).minusMillis(50).getMillis();
            observedMethods.add(new ObservedMethod(serviceName,"firstMethod",start, end));
            observedMethods.add(new ObservedMethod(serviceName,"firstMethod",start +2, end +3));
            observedMethods.add(new ObservedMethod(serviceName,"secondMethod",start +10, end +12));
            return observedMethods;
    }

    private void pushSomeData(List<ObservedMethod> observedMethods) {
        Client client = ClientBuilder.newClient();
        String observationUrl = "http://"+reporterHost + ":" + reporterPort +"/reporter/observe/";
        log.info("Connection to ValueReporter on {}" , observationUrl);
        final WebTarget observationTarget = client.target(observationUrl);
        //WebTarget webResource = findWebResourceByPrefix(serviceName);
        WebTarget webResource = observationTarget.path("observedmethods").path(serviceName);
        String observedMethodsJson = buildJson(observedMethods);
        log.trace("Forwarding implementedMethods as Json \n{}", observedMethodsJson);


        Response response = webResource.request(MediaType.APPLICATION_JSON).post(Entity.entity(observedMethodsJson, MediaType.APPLICATION_JSON));
        int statusCode = response.getStatus();
        switch (statusCode) {
            case STATUS_OK:
                log.trace("Updated via http ok. Response is {}", response.readEntity(String.class));
                break;
            case STATUS_FORBIDDEN:
                log.warn("Can not access ValueReporter. The application will function as normally, though Observation statistics will not be stored. URL {}, HttpStatus {}, Response {}, ", observationUrl,response.getStatus(), response.readEntity(String.class));
                break;
            default:
                log.error("Error while accessing ValueReporter. The application will function as normally, though Observation statistics will not be stored. URL {}, HttpStatus {},Response from ValueReporter {}",observationUrl, response.getStatus(), response.readEntity(String.class));
        }
    }

    private String buildJson(List<ObservedMethod> observedMethods)  {
        String json = null;
        try {
            json = mapper.writeValueAsString(observedMethods);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }



}
