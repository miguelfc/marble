package org.marble.preprocessor.simple;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableAutoConfiguration
@EnableDiscoveryClient
public class PreprocessorServer {

    protected Logger logger = Logger.getLogger(PreprocessorServer.class.getName());
    
	public static void main(String[] args) {
		// Tell server to look for registration.properties or registration.yml
		System.setProperty("spring.config.name", "preprocessor-simple");

		SpringApplication.run(PreprocessorServer.class, args);
	}
}
