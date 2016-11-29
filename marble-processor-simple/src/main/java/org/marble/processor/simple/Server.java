package org.marble.processor.simple;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableAutoConfiguration
@EnableDiscoveryClient
public class Server {

    protected Logger logger = Logger.getLogger(Server.class.getName());
    
	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}
}
