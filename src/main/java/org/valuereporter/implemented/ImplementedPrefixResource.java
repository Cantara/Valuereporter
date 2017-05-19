package org.valuereporter.implemented;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 29.07.15.
 */
@Component
@Path("/implementedprefix")
public class ImplementedPrefixResource {
    private static final Logger log = getLogger(ImplementedPrefixResource.class);

    private final QueryOperations queryOperations;
    private final WriteOperations writeOperations;
    private final ObjectMapper mapper;

    @Autowired
    public ImplementedPrefixResource(ObjectMapper mapper, QueryOperations queryOperations, WriteOperations writeOperations) {
        this.mapper = mapper;
        this.queryOperations = queryOperations;
        this.writeOperations = writeOperations;
    }

    //http://localhost:4901/reporter/observe/implementedprefix
    /**
     * List prefixes registered in the database
     *
     * @return  List of ImplementedPrefix'es
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllPrefixes() {
        final List<String> implementedPrefixes;

        implementedPrefixes = queryOperations.findImplementedPrefixes();

        Writer strWriter = new StringWriter();
        try {
            mapper.writeValue(strWriter, implementedPrefixes);
        } catch (IOException e) {
            log.error("Could not convert {} ImplementedPrefixes to JSON.", implementedPrefixes.size(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error converting to requested format.").build();
        }
        return Response.ok(strWriter.toString()).build();
    }
}
