package org.valuereporter.activity.timeseries;

import com.github.kevinsawicki.http.HttpRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.slf4j.Logger;
import org.valuereporter.activity.CountedActivity;
import org.valuereporter.activity.ObservedActivity;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import static org.slf4j.LoggerFactory.getLogger;

//import org.codehaus.jackson.map.ObjectMapper;

public class CommandSendActivities extends HystrixCommand<String>  {

    private static final Logger log = getLogger(CommandSendActivities.class);


    private final String prefix;
    private final List<ObservedActivity> observedActivities;
    private final String reporterHost;
    private final String reporterPort;
//    private ObjectMapper mapper = new ObjectMapper();
    private static final int STATUS_OK = 200;
    private static final int STATUS_NO_CONTENT = 204;
    private static final int STATUS_FORBIDDEN = 403;
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_BINARY = "application/octet-stream";
    private final String observedActivitiesJson;
    private final int no_of_activities;
    private final URI influxDbUri;
    private final String databaseName;

    /*
    public CommandSendActivities(final String reporterHost, final String reporterPort, final String prefix, final List<ObservedActivity> observedActivities) {
        super(HystrixCommandGroupKey.Factory.asKey("ValueReporter-Activities-group"));
        observedActivitiesJson = buildBody(observedActivities);
        no_of_activities = observedActivities.size();
        this.reporterHost = reporterHost;
        this.reporterPort = reporterPort;
        this.prefix = prefix;
        this.observedActivities = observedActivities;
    }
    */

    public CommandSendActivities(URI influxDbUri, String databaseName, List<ObservedActivity> observedActivities) {
        super(HystrixCommandGroupKey.Factory.asKey("ValueReporter-Activities-group"));
        this.influxDbUri = influxDbUri;
        this.databaseName = databaseName;
        observedActivitiesJson = buildBody(observedActivities);
        no_of_activities = observedActivities.size();
        this.reporterHost = null;
        this.reporterPort = null;
        this.prefix = null;
        this.observedActivities = observedActivities;
    }

    protected String buildBody(List<ObservedActivity> observedActivities)  {
        String json = "client-access,host=dev.shareproc.com,service=api,function=login,ip=127.0.0.1 count=1\n";
        String body = "";
        String line = "";
        for (ObservedActivity activity : observedActivities) {
            if (activity instanceof CountedActivity) {
                line = activity.getName() + "," + buildTags(activity) + " " + buildMeasurement((CountedActivity)activity) + "\n";
            } else {
                line = activity.getName() + "," + buildTags(activity) + " count=1\n";

            }
            log.trace("Line: [{}]", line);
            body += line;
        }
        return body;
    }

    protected String buildMeasurement(CountedActivity activity) {
        String measurement = "";
        if (activity != null) {
            measurement = "count=" + activity.getCount();
        }
        return measurement;
    }

    String buildTags(ObservedActivity activity) {
        StringJoiner tags = new StringJoiner(",");
        if (activity != null) {
            Map tagMap = activity.getData();
            Set<Map.Entry> entries = tagMap.entrySet();
            for (Map.Entry entry : entries) {
                tags.add(entry.getKey() + "=" +entry.getValue());
            }
        }
        return tags.toString();
    }

    @Override
    protected String run() {
        String observationUrl = influxDbUri + "/write?db=" + databaseName;
//        http://influxdb-component-ox6b3xp9td0-772793266.eu-west-1.elb.amazonaws.com:8086/write?db=shareproc";
        log.info("Connection to InfluxDb on {} num of activities: {}" , observationUrl,no_of_activities);
        HttpRequest request = HttpRequest.post(observationUrl ).acceptJson().contentType(APPLICATION_BINARY).send(observedActivitiesJson);
        int statusCode = request.code();
        String responseBody = request.body();
        switch (statusCode) {
            case STATUS_OK:
                log.trace("Updated via http ok. Response is {}", responseBody);
                break;
            case STATUS_NO_CONTENT:
                log.trace("Updated via http ok. No content in response, as expected.");
                break;
            case STATUS_FORBIDDEN:
                log.warn("Can not access InfluxDb. The application will function as normally, though Observation statistics will not be stored. URL {}, HttpStatus {}, Response {}, ", observationUrl,statusCode, responseBody);
                break;
            default:
                log.trace("Retrying access to InfluxDb");
                request = HttpRequest.post(observationUrl ).acceptJson().contentType(APPLICATION_BINARY).send(observedActivitiesJson);
                statusCode = request.code();
                responseBody = request.body();
                if (statusCode == STATUS_OK) {
                    log.trace("Retry via http ok. Response is {}", responseBody);
                } else {
                    log.error("Error while accessing InfluxDb. The application will function as normally, though Observation statistics will not be stored. URL {}, HttpStatus {},Response from InfluxDb {}", observationUrl, statusCode, responseBody);
                }
        }
        return "OK";


    }

    @Override
    protected String getFallback() {
        return "FALLBACK";
    }

    public String getObservedActivitiesJson() {
        return observedActivitiesJson;
    }
}