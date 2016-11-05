package org.marble.processor.stanford.config;

import javax.annotation.PostConstruct;
import javax.servlet.annotation.MultipartConfig;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.wadl.internal.WadlResource;
import org.marble.processor.stanford.controller.ProcessingResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

@Component
//@MultipartConfig(fileSizeThreshold = 20971520)
public class JerseyConfig extends ResourceConfig {

    @Value("${spring.jersey.application-path:/}")
    private String apiPath;

    public JerseyConfig() {
        // Register endpoints, providers, ...
        this.registerEndpoints();
        this.register(MultiPartFeature.class);
    }

    @PostConstruct
    public void init() {
        // Register components where DI is needed
        this.configureSwagger();
    }

    private void registerEndpoints() {
        this.register(ProcessingResource.class);
        // Access through /<Jersey's servlet path>/application.wadl
        this.register(WadlResource.class);
    }

    private void configureSwagger() {
        // Available at localhost:port/swagger.json
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig config = new BeanConfig();
        config.setConfigId("marble-processor-stanford");
        config.setTitle("Marble Stanford Processor");
        config.setVersion("v1");
        config.setContact("Miguel Fernandes");
        config.setSchemes(new String[] { "http", "https" });
        config.setBasePath(this.apiPath);
        config.setResourcePackage("org.marble.processor.stanford.controller");
        config.setPrettyPrint(true);
        config.setScan(true);
    }
}