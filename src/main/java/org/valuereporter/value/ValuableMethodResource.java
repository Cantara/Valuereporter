package org.valuereporter.value;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    //http://localhost:4901/reporter/observe/valuemethods/{serviceName}
    /**
     *
     * @param serviceName serviceName used to identify running process
     * @return  List of ImplementedMethods
     */
    @GET
    @Path("/{serviceName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findValuableMethods(@PathParam("serviceName") String serviceName) {
        final List<ValuableMethod> implementedMethods;

        //Should also support no queryParams -> findAll
        if (serviceName != null ) {
            log.trace("findValuableMethods serviceName={}", serviceName);
            implementedMethods = queryOperations.findValuableMethods(serviceName);
        } else {
            throw new UnsupportedOperationException("You must supply a serviceName.");
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

    //http://localhost:4901/reporter/observe/valuemethods/{serviceName}/chart
    /**
     *
     * @param serviceName serviceName used to identify running process
     * @return  List of ImplementedMethods
     */
    @GET
    @Path("/{serviceName}/chart")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findValuableDistribution(@PathParam("serviceName") String serviceName) {
        final List<ValuableMethod> valuableMethods;

        if (serviceName != null ) {
            log.trace("findValuableMethods serviceName={}", serviceName);
            valuableMethods = queryOperations.findValuableDistribution(serviceName, null);
        } else {
            throw new UnsupportedOperationException("You must supply a serviceName.");
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
    //http://localhost:4901/reporter/observe/valuemethods/{serviceName}/chart/{filter}
    /**
     *
     * @param serviceName serviceName used to identify running process
     * @return  List of ImplementedMethods
     */
    @GET
    @Path("/{serviceName}/chart/{filter}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findValuableDistribution(@PathParam("serviceName") String serviceName,@PathParam("filter") String filter ) {
        final List<ValuableMethod> valuableMethods;

        if (serviceName != null ) {
            log.trace("findValuableMethods serviceName={}, filter {}", serviceName, filter);
            valuableMethods = queryOperations.findValuableDistribution(serviceName, filter);
        } else {
            throw new UnsupportedOperationException("You must supply a serviceName.");
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
