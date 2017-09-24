package org.valuereporter.sla;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.valuereporter.ValuereporterInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Component
@Path("/sla/statistics")
public class SlaStatisticsResource {
    private static final Logger log = LoggerFactory.getLogger(SlaStatisticsResource.class);

    SlaQueryOperations service;
    ObjectMapper mapper;

    @Autowired
    public SlaStatisticsResource(SlaQueryOperations queryOperations, ObjectMapper objectMapper) {
        this.service = queryOperations;
        this.mapper = objectMapper;
    }

    /**
     * http://localhost:4901/reporter/observe/sla/statistics/{serviceName}/{methodName}?from=datetimeinmillis&to=datetimeinmillis
     * @param serviceName
     * @param methodName
     * @param from
     * @param to
     * @return
     */
    @GET
    @Path("/{serviceName}/{methodName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findValuableMethods(@PathParam("serviceName") String serviceName, @PathParam("methodName") String methodName , @QueryParam("from") Long from, @QueryParam("to") Long to) {
        DateTime toDate = null;
        DateTime fromDate = null;
        if (to == null) {
            to = System.currentTimeMillis();
        }
        toDate = new DateTime(to);
        if (from != null) {
            fromDate = new DateTime(from);
        }else {
            fromDate = toDate.minusDays(7);
        }
        try {
            SlaStatisticsRepresentation representation = service.findSlaStatistics(serviceName, methodName, fromDate, toDate);

            Writer strWriter = new StringWriter();
            try {
                mapper.writeValue(strWriter, representation);
            } catch (IOException e) {
                log.error("Could not convert {} SlaStatistics to JSON.", representation.toString(), e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error converting to requested format.").build();
            }
            return Response.ok(strWriter.toString()).build();
        } catch (ValuereporterInputException vie) {
            log.info("Failed to find sla statistics due to wrong input data. serviceName {}, methodName {}, from {}, to {}. MessageError: {}", serviceName, methodName, fromDate, toDate, vie.getMessageId() );
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing or illegal input data. Report error " + vie.getMessageId()).build();
        }
    }
}
