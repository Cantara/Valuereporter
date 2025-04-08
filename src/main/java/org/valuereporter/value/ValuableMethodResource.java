package org.valuereporter.value;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * @author <a href="bard.lind@gmail.com">Bard Lind</a>
 */
@Component
@Path("/valuemethods")
public class ValuableMethodResource {
    private static final Logger log = LoggerFactory.getLogger(ValuableMethodResource.class);

    private final QueryOperations queryOperations;
    private final WriteOperations writeOperations;
    private final ObjectMapper mapper;

    @Autowired
    public ValuableMethodResource(ValueMethodService valueMethodService, ObjectMapper mapper) {
        this.queryOperations = valueMethodService;
        this.writeOperations = valueMethodService;
        this.mapper = mapper;
    }

    //http://localhost:4901/reporter/observe/valuemethods/{prefix}
    /**
     *
     * @param prefix prefix used to identify running process
     * @return  List of ImplementedMethods
     */
    @GET
    @Path("/{prefix}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findValuableMethods(@PathParam("prefix") String prefix) {
        final List<ValuableMethod> implementedMethods;

        //Should also support no queryParams -> findAll
        if (prefix != null ) {
            log.trace("findValuableMethods prefix={}", prefix);
            implementedMethods = queryOperations.findValuableMethods(prefix);
        } else {
            throw new UnsupportedOperationException("You must supply a prefix.");
        }

        Writer strWriter = new StringWriter();
        try {
            mapper.writeValue(strWriter, implementedMethods);
        } catch (IOException e) {
            log.error("Could not convert {} ObservedMethod to JSON.", implementedMethods.size(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error converting to requested format.").build();
        }
        return Response.ok(strWriter.toString()).build();
    }

    //http://localhost:4901/reporter/observe/valuemethods/{prefix}/chart
    /**
     *
     * @param prefix prefix used to identify running process
     * @return  List of ImplementedMethods
     */
    @GET
    @Path("/{prefix}/chart")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findValuableDistribution(@PathParam("prefix") String prefix) {
        final List<ValuableMethod> valuableMethods;

        if (prefix != null ) {
            log.trace("findValuableMethods prefix={}", prefix);
            valuableMethods = queryOperations.findValuableDistribution(prefix, null);
        } else {
            throw new UnsupportedOperationException("You must supply a prefix.");
        }

        Writer strWriter = new StringWriter();
        try {
            mapper.writeValue(strWriter, valuableMethods);
        } catch (IOException e) {
            log.error("Could not convert {} ObservedMethod to JSON.", valuableMethods.size(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error converting to requested format.").build();
        }
        return Response.ok(strWriter.toString()).build();
    }
    //http://localhost:4901/reporter/observe/valuemethods/{prefix}/chart/{filter}
    /**
     *
     * @param prefix prefix used to identify running process
     * @return  List of ImplementedMethods
     */
    @GET
    @Path("/{prefix}/chart/{filter}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findValuableDistribution(@PathParam("prefix") String prefix,@PathParam("filter") String filter ) {
        final List<ValuableMethod> valuableMethods;

        if (prefix != null ) {
            log.trace("findValuableMethods prefix={}, filter {}", prefix, filter);
            valuableMethods = queryOperations.findValuableDistribution(prefix, filter);
        } else {
            throw new UnsupportedOperationException("You must supply a prefix.");
        }

        Writer strWriter = new StringWriter();
        try {
            mapper.writeValue(strWriter, valuableMethods);
        } catch (IOException e) {
            log.error("Could not convert {} ObservedMethod to JSON.", valuableMethods.size(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error converting to requested format.").build();
        }
        return Response.ok(strWriter.toString()).build();
    }

}
