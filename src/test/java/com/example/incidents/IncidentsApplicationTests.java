package com.example.incidents;

import com.example.incidents.api.IncidentsApi;
import com.example.incidents.service.IncidentsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class IncidentsApplicationTests {

	@Autowired
	IncidentsApi incidentsApi;

	@Autowired
	IncidentsService incidentsService;

	/**
	 * Make sure, that the application container creates the API-class (i.e. controller).
	 */
	@Test
	void contextLoads() {
		assertNotNull(incidentsApi);
		assertNotNull(incidentsService);
	}

}
