package org.marble.plotter.simple.config;

import javax.annotation.PostConstruct;
import javax.servlet.annotation.MultipartConfig;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.wadl.internal.WadlResource;
import org.marble.plotter.simple.controller.PlotterResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

@Component
public class JerseyConfig extends ResourceConfig {

    @Value("${spring.jersey.application-path:/}")
    private String apiPath;

    public JerseyConfig() {
        this.registerEndpoints();
    }

    @PostConstruct
    public void init() {
        // Register components where DI is needed
        this.configureSwagger();
    }

    private void registerEndpoints() {
        this.register(PlotterResource.class);
        // Access through /<Jersey's servlet path>/application.wadl
        this.register(WadlResource.class);
    }

    private void configureSwagger() {
        // Available at localhost:port/swagger.json
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig config = new BeanConfig();
        config.setConfigId("marble-preprocessor-simple");
        config.setTitle("Marble Simple Preprocessor");
        config.setVersion("v1");
        config.setContact("Miguel Fernandes");
        config.setSchemes(new String[] { "http", "https" });
        config.setBasePath(this.apiPath);
        config.setResourcePackage("org.marble.preprocessor.simple.controller");
        config.setPrettyPrint(true);
        config.setScan(true);
    }
}