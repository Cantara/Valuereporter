package org.valuereporter.httprequest;

import com.github.kevinsawicki.http.HttpRequest;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 06.03.16.
 */
public class HttpRequestIntegrationTest {
    private static final Logger log = getLogger(HttpRequestIntegrationTest.class);
    private static final String APPLICATION_JSON = "application/json";


    public static void main(String[] args) {


        String reporterHost = "localhost";
        String reporterPort = "4901";
        String serviceName = "All";
        String observedMethodsJson = "[]";
        String observationUrl = "http://"+reporterHost + ":" + reporterPort +"/reporter/observe";
        log.info("Connection to ValueReporter on {}" , observationUrl);
        HttpRequest request = HttpRequest.post(observationUrl + "/observedmethods/" + serviceName).acceptJson().contentType(APPLICATION_JSON).send(observedMethodsJson);
//        WebTarget webResource = observationTarget.path("observedmethods").path(serviceName);
//        log.trace("Forwarding observedMethods as Json \n{}", observedMethodsJson);

//        Response response = webResource.request(MediaType.APPLICATION_JSON).post(Entity.entity(observedMethodsJson, MediaType.APPLICATION_JSON));
        int statusCode = request.code();
        log.info("Status {}, body {}", request.code(), request.body());
    }
}
