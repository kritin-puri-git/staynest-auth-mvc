package com.project.staynest.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class StaynestBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StaynestBackendApplication.class, args);
	}

}
