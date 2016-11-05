package org.marble.commons.config;

import org.marble.commons.service.processor.ProcessorService;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RemoteConfig {

    public static final String PROCESSOR_SIMPLE_SERVICE = "http://PROCESSOR-SIMPLE";
    public static final String PROCESSOR_STANFORD_SERVICE = "http://PROCESSOR-STANFORD";
    public static final String PREPROCESSOR_SIMPLE_SERVICE = "http://PREPROCESSOR-SIMPLE";

    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean(name="preprocessorSimpleService")
    public ProcessorService preprocessorSimpleService() {
        return new ProcessorService(PREPROCESSOR_SIMPLE_SERVICE);
    }

    @Bean(name="processorSimpleService")
    public ProcessorService processorSimpleService() {
        return new ProcessorService(PROCESSOR_SIMPLE_SERVICE);
    }
    
    
    @Bean(name="processorStanfordService")
    public ProcessorService processorStanfordService() {
        return new ProcessorService(PROCESSOR_STANFORD_SERVICE);
    }
    
}