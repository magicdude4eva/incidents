package com.example.incidents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;

/**
 * Disable automatic configuration of the ES-client as this will prevent the application from start.
 */
@SpringBootApplication(exclude = ElasticsearchClientAutoConfiguration.class)
public class IncidentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(IncidentsApplication.class, args);
	}

}
