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
import java.util.ArrayList;
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

    //http://localhost:4901/reporter/observe/implementedmethod/{serviceName}/{name}
    /**
     * A request with no filtering parameters should return a list of all ImplementedMethods.
     *
     * @param serviceName serviceName used to identify running process
     * @param name    package.classname.method
     * @return  List of ImplementedMethods
     */
    @GET
    @Path("/{serviceName}/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findImplementedMethodsByName(@PathParam("serviceName") String serviceName,@PathParam("name") String name) {
        final List<ImplementedMethod> implementedMethods;

        //Should also support no queryParams -> findAll
        if (name != null ) {
            log.trace("findImplementedMethodsByName name={}", name);
            implementedMethods = queryOperations.findImplementedMethods(serviceName, name);
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

    //http://localhost:4901/reporter/observe/implementedmethods/{serviceName}
    /**
     * A request with no filtering parameters should return a list of all ImplementedMethods.
     *
     * @param serviceName serviceName used to identify running process
     * @return  List of ImplementedMethods
     */
    @GET
    @Path("/{serviceName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findImplementedMethodsPrefix(@PathParam("serviceName") String serviceName) {
        final List<ImplementedMethod> implementedMethods;

        //Should also support no queryParams -> findAll
        if (serviceName != null ) {
            log.trace("findImplementedMethodsByPrefix serviceName={}", serviceName);
            implementedMethods = queryOperations.findImplementedMethods(serviceName, null);
        } else {
            throw new UnsupportedOperationException("You must supply a serviceName.");
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

    //http://localhost:4901/reporter/observe/implementedmethod/{serviceName}
    @POST
    @Path("/{serviceName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addObservationMethod(@PathParam("serviceName") String serviceName, String jsonBody){
        log.trace("addObservationMethod serviceName {} , jsonBody {}.", serviceName, jsonBody);
        List<ImplementedMethod> implementedMethods = null;
        try {
            implementedMethods = mapper.readValue(jsonBody, new TypeReference<ArrayList<ImplementedMethodJson>>(){ });
            if (implementedMethods != null) {
                for (ImplementedMethod implementedMethod : implementedMethods) {
                    implementedMethod.setPrefix(serviceName);
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
