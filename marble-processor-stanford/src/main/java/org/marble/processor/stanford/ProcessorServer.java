package org.marble.processor.stanford;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableAutoConfiguration
@EnableDiscoveryClient
public class ProcessorServer {

    protected Logger logger = Logger.getLogger(ProcessorServer.class.getName());
    
	public static void main(String[] args) {
		// Tell server to look for registration.properties or registration.yml
		System.setProperty("spring.config.name", "processor-stanford");

		SpringApplication.run(ProcessorServer.class, args);
	}
}
