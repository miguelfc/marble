package org.marble.plotter.simple.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.marble.model.model.PlotterInput;
import org.marble.model.model.PlotterOutput;
import org.marble.plotter.simple.service.PlotterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Component
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "Processing Resource", produces = "application/json")
public class PlotterResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlotterResource.class);

    @Autowired
    PlotterService plotterService;

    @POST
    @Path("plot")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Plots a topic and returns its chart definition.", response = PlotterOutput.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Message processed.")
    })
    public Response plot(PlotterInput input, @Context UriInfo uriInfo) {
        LOGGER.info("Received <plot> request with input <" + input + ">");
        PlotterOutput output = new PlotterOutput();
        output.setCharts(plotterService.plot(input));
        output.setNotes("Processed charts for topic <" + input.getTopicName() + "> using simple plotter.");
        LOGGER.info("Sent <plot> response with output <" + output + ">");
        return Response.status(Status.OK).entity(output).build();
    }
}