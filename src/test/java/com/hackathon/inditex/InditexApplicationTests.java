package com.hackathon.inditex;

import com.hackathon.inditex.DTO.CenterRequest;
import com.hackathon.inditex.Entities.Coordinates;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
// Assure DB modifications from one test don't affect the others
@Sql(scripts = "/sql/reset-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class InditexApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	//@Test
	void contextLoads() {
	}

	// 1) CENTER MANAGEMENT

	// 1.1) CREATE A CENTER

	// WITH BAD CAPACITY ("S", "M", "B")
	@Test
	void createCenterBadCapacity() {
		String url = "http://localhost:" + port + "/api/centers";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("P"); //bad capacity
		centerRequest.setStatus("OCCUPIED");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> response = restTemplate.postForEntity(url, centerRequest, String.class);

		// Verify the response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid capacity value");
	}

	// WITH BAD STATUS ("AVAILABLE", "OCCUPIED")
	@Test
	void createCenterBadStatus() {
		String url = "http://localhost:" + port + "/api/centers";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("MS");
		centerRequest.setStatus("STALE");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> response = restTemplate.postForEntity(url, centerRequest, String.class);

		// Verify the response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid status value");
	}

	// WITH BAD LOAD (currentLoad > maxCapacity)
	@Test
	void createCenterBadLoad() {
		String url = "http://localhost:" + port + "/api/centers";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("MS");
		centerRequest.setStatus("AVAILABLE");
		centerRequest.setCurrentLoad(3);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> response = restTemplate.postForEntity(url, centerRequest, String.class);

		// Verify the response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).contains("Current load cannot exceed max capacity.");
	}

	// ALREADY EXISTING (same coordinates)
	@Test
	void createCenterExists() {
		String url = "http://localhost:" + port + "/api/centers";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("MS");
		centerRequest.setStatus("AVAILABLE");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> response1 = restTemplate.postForEntity(url, centerRequest, String.class);

		// Verify the OK response
		assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response1.getBody()).contains("Logistics center created successfully.");

		// Send the same POST request and receive a response
		ResponseEntity<String> response2 = restTemplate.postForEntity(url, centerRequest, String.class);

		// Verify the ERROR response
		assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response2.getBody()).contains("There is already a logistics center in that position.");
	}

	// VALID CENTER IS CREATED
	@Test
	void createCenterIsCreated() {
		String url = "http://localhost:" + port + "/api/centers";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("MS");
		centerRequest.setStatus("AVAILABLE");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> response1 = restTemplate.postForEntity(url, centerRequest, String.class);

		// Verify the OK response
		assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response1.getBody()).contains("Logistics center created successfully.");
	}

}
