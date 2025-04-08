package org.valuereporter.rest;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a>
 */
@ApplicationPath("reporter")
public class JerseyApplication extends ResourceConfig {
    private static final Logger log = LoggerFactory.getLogger(JerseyApplication.class);

    /**
     * Base Url to http://host:port/reporer/observe/
     */
    public JerseyApplication() {
        //https://java.net/jira/browse/JERSEY-2175
        packages("org.valuereporter");

        //resourceConfig.register(MultiPartFeature.class);
        log.debug(this.getClass().getSimpleName() + " started!");
    }
}
