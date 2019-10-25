package org.valuereporter.implemented;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.valuereporter.ValuereporterInputException;
import org.valuereporter.helper.StatusType;

import javax.ws.rs.*;
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
@Path("/implementedmethods")
public class ImplementedMethodResource {
    private static final Logger log = LoggerFactory.getLogger(ImplementedMethodResource.class);

    private final QueryOperations queryOperations;
    private final WriteOperations writeOperations;
    private final ObjectMapper mapper;

    @Autowired
    public ImplementedMethodResource(ImplementedMethodService implementedMethodService, ObjectMapper mapper) {
        this.queryOperations = implementedMethodService;
        this.writeOperations = implementedMethodService;
        this.mapper = mapper;
    }

    //http://localhost:4901/reporter/observe/implementedmethod/{prefix}/{name}
    /**
     * A request with no filtering parameters should return a list of all ImplementedMethods.
     *
     * @param prefix prefix used to identify running process
     * @param name    package.classname.method
     * @return  List of ImplementedMethods
     */
    @GET
    @Path("/{prefix}/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findImplementedMethodsByName(@PathParam("prefix") String prefix,@PathParam("name") String name) {
        final List<ImplementedMethod> implementedMethods;

        //Should also support no queryParams -> findAll
        if (name != null ) {
            log.trace("findImplementedMethodsByName name={}", name);
            implementedMethods = queryOperations.findImplementedMethods(prefix, name);
        } else {
            throw new UnsupportedOperationException("You must supply a name. <package.classname.method>");
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

    //http://localhost:4901/reporter/observe/implementedmethods/{prefix}
    /**
     * A request with no filtering parameters should return a list of all ImplementedMethods.
     *
     * @param prefix prefix used to identify running process
     * @return  List of ImplementedMethods
     */
    @GET
    @Path("/{prefix}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findImplementedMethodsPrefix(@PathParam("prefix") String prefix) {
        final List<ImplementedMethod> implementedMethods;

        //Should also support no queryParams -> findAll
        if (prefix != null ) {
            log.trace("findImplementedMethodsByPrefix prefix={}", prefix);
            implementedMethods = queryOperations.findImplementedMethods(prefix, null);
        } else {
            throw new UnsupportedOperationException("You must supply a prefix.");
        }

        Writer strWriter = new StringWriter();
        try {
            mapper.writeValue(strWriter, implementedMethods);
        } catch (IOException e) {
            log.error("Could not convert {} ImplementedMethod to JSON.", implementedMethods.size(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error converting to requested format.").build();
        }
        return Response.ok(strWriter.toString()).build();
    }

    //http://localhost:4901/reporter/observe/implementedmethod/{prefix}
    @POST
    @Path("/{prefix}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addObservationMethod(@PathParam("prefix") String prefix, String jsonBody){
        log.trace("addObservationMethod prefix {} , jsonBody {}.", prefix, jsonBody);
        List<ImplementedMethodJson> implementedMethods = null;
        try {
            implementedMethods = mapper.readValue(jsonBody, new TypeReference<List<ImplementedMethodJson>>() {
            });
            if (implementedMethods != null) {
                for (ImplementedMethod implementedMethod : implementedMethods) {
                    implementedMethod.setPrefix(prefix);
                }
            }
        } catch (IOException e) {
           log.warn("TODO: Handle messages in non-expected format.", e);
            throw new ValuereporterInputException("Wrong data format on input.", StatusType.data_error);
        }

        long updatedCount = writeOperations.addImplementedMethods(implementedMethods);
        String message =  "added " + updatedCount + " implementedMethods.";
        log.trace("addImplementedMethod msg: {}",message);
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
