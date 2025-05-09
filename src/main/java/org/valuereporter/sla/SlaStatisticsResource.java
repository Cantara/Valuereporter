package org.valuereporter.sla;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.valuereporter.ValuereporterInputException;

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
     * http://localhost:4901/reporter/observe/sla/statistics/{prefix}/{methodName}?from=datetimeinmillis&to=datetimeinmillis
     * @param prefix
     * @param methodName
     * @param from
     * @param to
     * @return
     */
    @GET
    @Path("/{prefix}/{methodName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findValuableMethods(@PathParam("prefix") String prefix, @PathParam("methodName") String methodName , @QueryParam("from") Long from, @QueryParam("to") Long to) {
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
            SlaStatisticsRepresentation representation = service.findSlaStatistics(prefix, methodName, fromDate, toDate);

            Writer strWriter = new StringWriter();
            try {
                mapper.writeValue(strWriter, representation);
            } catch (IOException e) {
                log.error("Could not convert {} SlaStatistics to JSON.", representation.toString(), e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error converting to requested format.").build();
            }
            return Response.ok(strWriter.toString()).build();
        } catch (ValuereporterInputException vie) {
            log.info("Failed to find sla statistics due to wrong input data. prefix {}, methodName {}, from {}, to {}. MessageError: {}", prefix, methodName, fromDate, toDate, vie.getUuid() );
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing or illegal input data. Report error " + vie.getUuid()).build();
        }
    }
}
