package org.valuereporter.observation.upload;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.helper.PropertiesHelper;
import org.valuereporter.observation.ObservedMethod;
import org.valuereporter.observation.ThreadSafeObservationsRepositoryVerification;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class PushDataToValueReporter {
    private static final Logger log = LoggerFactory.getLogger(PushDataToValueReporter.class);
    private String reporterHost = "localhost";
    private String reporterPort = "4901";
    private String serviceName = "PushDataToValueReporter";
    private static ObjectMapper mapper = new ObjectMapper();
    private static final int STATUS_BAD_REQUEST = 400; //Response.Status.BAD_REQUEST.getStatusCode();
    private static final int STATUS_OK = 200; //Response.Status.OK.getStatusCode();
    private static final int STATUS_FORBIDDEN = 403;


    public PushDataToValueReporter(String reporterHost, String reporterPort, String serviceName) {
        this.reporterHost = reporterHost;
        this.reporterPort = reporterPort;
        this.serviceName = serviceName;
    }
    public static void main(String[] args) {

        String loadPrefix = "PushDataToValueReporter";
        Properties resources = PropertiesHelper.findProperties();
        String host = resources.getProperty("dataload.host");
        String port = resources.getProperty("dataload.port");
        PushDataToValueReporter main = new PushDataToValueReporter(host, port, loadPrefix);
        List<ObservedMethod> firstMethods = main.getFirstMethods();
        List<ObservedMethod> secondMethods = main.getSecondMethods();
        try {
            main.pushObservedMethods(mapper.writeValueAsString(firstMethods));
            main.pushObservedMethods(mapper.writeValueAsString(secondMethods));
        } catch (JsonProcessingException e) {
            log.warn("Failed to build Json.", e);
        }
    }


    private void pushObservedMethods(String implementedMethodsAsJson) {
        Client client = ClientBuilder.newClient();
        String observationUrl = "http://"+reporterHost + ":" + reporterPort +"/reporter/observe";
        log.info("Connection to ValueReporter on {}" , observationUrl);
        final WebTarget observationTarget = client.target(observationUrl);
        //WebTarget webResource = findWebResourceByPrefix(serviceName);
        WebTarget webResource = observationTarget.path("observedmethods").path(serviceName);
        //String observedMethodsJson = mapper.writeValueAsString(observedMethods);
        log.trace("Forwarding implementedMethods as Json \n{}", implementedMethodsAsJson);


        Response response = webResource.request(MediaType.APPLICATION_JSON).post(Entity.entity(implementedMethodsAsJson, MediaType.APPLICATION_JSON));
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

    List<ObservedMethod> getFirstMethods() {
        List<ObservedMethod> firstMethods = new ArrayList<>();
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 1000;
        long duration = 1000;
        firstMethods.add(new ObservedMethod(serviceName, ThreadSafeObservationsRepositoryVerification.FIRST_METHOD, startTime, endTime, duration));
        firstMethods.add(new ObservedMethod(serviceName, ThreadSafeObservationsRepositoryVerification.FIRST_METHOD, startTime, endTime, duration));
        return firstMethods;
    }

    List<ObservedMethod> getSecondMethods() {
        List<ObservedMethod> secondMethods = new ArrayList<>();
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 200;
        long duration = 200;
        secondMethods.add(new ObservedMethod(serviceName, ThreadSafeObservationsRepositoryVerification.SECOND_METHOD, startTime, endTime, duration));
        return secondMethods;
    }

}
