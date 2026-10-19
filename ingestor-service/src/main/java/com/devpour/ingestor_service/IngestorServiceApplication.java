package com.devpour.ingestor_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class IngestorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngestorServiceApplication.class, args);
	}

}
