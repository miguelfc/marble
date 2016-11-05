package org.marble.commons.service.processor;

import org.marble.model.domain.ProcessorInput;
import org.marble.model.domain.ProcessorOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//@Service
public class ProcessorService {

    @Autowired // NO LONGER auto-created by Spring Cloud
    @LoadBalanced
    protected RestTemplate restTemplate;

    protected String serviceUrl;

    private static final Logger log = LoggerFactory.getLogger(ProcessorService.class);

    public ProcessorService(String serviceUrl) {
        this.serviceUrl = serviceUrl.startsWith("http") ? serviceUrl : "http://" + serviceUrl;
    }

    public ProcessorOutput processMessage(ProcessorInput input) {
        ProcessorOutput output = restTemplate.postForObject(serviceUrl
                + "/api/process", input, ProcessorOutput.class);

        if (output == null) {
            log.error("Processor <" + serviceUrl + "> operation failed.");
        }
        return output;
    }

}
