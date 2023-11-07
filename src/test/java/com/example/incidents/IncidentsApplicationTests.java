package com.example.incidents;

import com.example.incidents.api.IncidentsApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class IncidentsApplicationTests {

	@Autowired
	IncidentsApi incidentsApi;

	/**
	 * Make sure, that the application container creates the API-class (i.e. controller).
	 */
	@Test
	void contextLoads() {
		assertNotNull(incidentsApi);
	}

}
