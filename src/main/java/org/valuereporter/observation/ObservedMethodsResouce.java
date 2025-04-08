package org.valuereporter.observation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.valuereporter.QueryOperations;
import org.valuereporter.WriteOperations;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a>
 */
@Component
@Path("/observedmethods")
public class ObservedMethodsResouce {
    private static final Logger log = LoggerFactory.getLogger(ObservedMethodsResouce.class);

    private final QueryOperations queryOperations;
    private final WriteOperations writeOperations;
    private final ObjectMapper mapper;


    /**
    @Autowired
    public ObservedMethodsResouce(QueryOperations queryOperations, WriteOperations writeOperations, ObjectMapper mapper) {
        this.queryOperations = queryOperations;
        this.writeOperations = writeOperations;
        this.mapper = mapper;
    }
     **/
    @Autowired
    public ObservedMethodsResouce(ObservationsService observationsService, ObjectMapper mapper) {
        this.queryOperations = observationsService;
        this.writeOperations = observationsService;
        this.mapper = mapper;
    }




    //http://localhost:4901/reporter/observe/observedmethods/{prefix}/{name}
    /**
     * A request with no filtering parameters should return a list of all observations.
     *
     * @param prefix prefix used to identify running process
     * @param name    package.classname.method
     * @return  List of observations
     */
    @GET
    @Path("/{prefix}/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findObservationsByName(@PathParam("prefix") String prefix,@PathParam("name") String name) {
        final List<ObservedMethod> observedMethods;

        //Should also support no queryParams -> findAll
        if (name != null ) {
            log.trace("findObservationsByName name={}", name);
            observedMethods = queryOperations.findObservationsByName(prefix, name);
        } else {
            throw new UnsupportedOperationException("You must supply a name. <package.classname.method>");
        }

        Writer strWriter = new StringWriter();
        try {
            mapper.writeValue(strWriter, observedMethods);
        } catch (IOException e) {
            log.error("Could not convert {} ObservedMethod to JSON.", observedMethods.size(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error converting to requested format.").build();
        }
        return Response.ok(strWriter.toString()).build();
    }

    //http://localhost:4901/reporter/observe/observedmethods/{prefix}
    @POST
    @Path("/{prefix}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addObservationMethod(@PathParam("prefix") String prefix, String jsonBody){
        log.trace("addObservationMethod prefix {} , jsonBody {}.", prefix, jsonBody);
        ArrayList<ObservedMethodJson> observedMethods = null;
        try {
            observedMethods = mapper.readValue(jsonBody, new TypeReference<ArrayList<ObservedMethodJson>>(){ });
            if (observedMethods != null) {
                for (ObservedMethod observedMethod : observedMethods) {
                    observedMethod.setPrefix(prefix);
                }
            }
        } catch (IOException e) {
            log.warn("Unexpected error trying to produce list of ObservedMethod from \n prefix {} \n json {}, \n Reason {}",prefix, jsonBody, e.getMessage());
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Error converting to requested format.").build();
        }

        long updatedCount = writeOperations.addObservations(prefix,observedMethods);
        String message =  "added " + updatedCount + " observedMethods.";
        Writer strWriter = new StringWriter();
        try {
            mapper.writeValue(strWriter, message);
        } catch (IOException e) {
            log.error("Could not convert {} to JSON.", updatedCount, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error converting to requested format.").build();
        }
        return Response.ok(strWriter.toString()).build();
    }
}
