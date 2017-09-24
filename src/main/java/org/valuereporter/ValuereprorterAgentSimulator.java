package org.valuereporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valuereporter.helper.PropertiesHelper;
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
import java.util.Properties;

/**
 * Thiss simulator will post some usage data into Valuereporter.
 *
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
public class ValuereprorterAgentSimulator {
    private static final Logger log = LoggerFactory.getLogger(ValuereprorterAgentSimulator.class);
    private static final String DEFAULT_REPORTER_HOST = "localhost";
    private final int port;
    private final WebTarget observationTarget;
    ObjectMapper mapper = new ObjectMapper();
    private static final int STATUS_BAD_REQUEST = 400; //Response.Status.BAD_REQUEST.getStatusCode();
    private static final int STATUS_OK = 200; //Response.Status.OK.getStatusCode();
    private static final int STATUS_FORBIDDEN = 403;

    public ValuereprorterAgentSimulator() {
        Properties resources = PropertiesHelper.findProperties();
        port = PropertiesHelper.findHttpPort(resources);
        Client client = ClientBuilder.newClient();
        String reporterHost = DEFAULT_REPORTER_HOST;
        String observationUrl = "http://"+reporterHost + ":" + port +"/reporter/observe";
        log.info("Connection to Statistics Reporter on {}" , observationUrl);
        observationTarget = client.target(observationUrl);
    }

    public static void main(String[] args) {

        ValuereprorterAgentSimulator simulator = new ValuereprorterAgentSimulator();
        String serviceName = "template-SERVICE_NAME";
        simulator.addSomeAction(serviceName);


    }

    private void addSomeAction(String serviceName) {

        try {
            List<ObservedMethod> observedMethods = buildSomeStubMethods(serviceName);
            String observedMethodsJson = mapper.writeValueAsString(observedMethods);
            log.trace("Forwarding observedMethods as Json \n{}", observedMethodsJson);
            WebTarget webTarget = observationTarget.path("observedmethods").path(serviceName);

            Response response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(observedMethodsJson, MediaType.APPLICATION_JSON));
            log.info("Sent this json {} to {}. Response: {}", observedMethodsJson, webTarget.getUri(), response);
        } catch (IOException e) {
            log.error("Error forwarding the observations. The application will not try to fix this.", e);
        }
    }

    private List<ObservedMethod> buildSomeStubMethods(String serviceName) {
        List<ObservedMethod> observedMethods = new ArrayList<>();
        observedMethods.add(new ObservedMethod(serviceName,"org.valuereporter.Welcome.ping", System.currentTimeMillis()-100, System.currentTimeMillis()));
        observedMethods.add(new ObservedMethod(serviceName,"org.valuereporter.Welcome.ping", System.currentTimeMillis()-200, System.currentTimeMillis()-100));
        observedMethods.add(new ObservedMethod(serviceName,"org.valuereporter.Welcome.ping", System.currentTimeMillis()-300, System.currentTimeMillis()-280));
        observedMethods.add(new ObservedMethod(serviceName,"org.valuereporter.Welcome.hello", System.currentTimeMillis()-50, System.currentTimeMillis()));
        observedMethods.add(new ObservedMethod(serviceName,"org.valuereporter.Welcome.hello", System.currentTimeMillis()-50, System.currentTimeMillis()-45));
        observedMethods.add(new ObservedMethod(serviceName,"org.valuereporter.Welcome.hello", System.currentTimeMillis()-300, System.currentTimeMillis()-200));
        return observedMethods;
    }
}
