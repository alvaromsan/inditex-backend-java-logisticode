package com.hackathon.inditex;

import com.hackathon.inditex.DTO.CenterRequest;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
// Import this bean to allow PATCH method in the restTemplate
@Import(PatchSupportConfig.class)
// Assure DB modifications from one test don't affect the others
@Sql(scripts = "/sql/reset-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class InditexApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	// 1) CENTER MANAGEMENT

	// 1.1) CREATE A CENTER

	// WITH INVALID CAPACITY ("S", "M", "B")
	@Test
	void createCenterInvalidCapacity() {
		String url = "http://localhost:" + port + "/api/centers";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("P"); // Invalid capacity
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

	// WITH INVALID STATUS ("AVAILABLE", "OCCUPIED")
	@Test
	void createCenterInvalidStatus() {
		String url = "http://localhost:" + port + "/api/centers";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("MS");
		centerRequest.setStatus("STALE"); // Invalid status
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> response = restTemplate.postForEntity(url, centerRequest, String.class);

		// Verify the response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid status value");
	}

	// WITH INVALID LOAD (currentLoad > maxCapacity)
	@Test
	void createCenterInvalidLoad() {
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
	void createCenterCreated() {
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

	// 1.2) READ ALL CENTERS

	// LISTING ALL EXPECTED CENTERS
	@Test
	void listCentersOk() {
		String url = "http://localhost:" + port + "/api/centers";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("S");
		centerRequest.setStatus("AVAILABLE");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request to add a center to the list
		ResponseEntity<String> response1 = restTemplate.postForEntity(url, centerRequest, String.class);

		// Verify the center was correctly created
		assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response1.getBody()).contains("Logistics center created successfully.");

		// Send a GET request and receive the list with all the centers
		ResponseEntity<List<Center>> response2 = restTemplate.exchange(url, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Center>>() {
				}
		);

		// Verify the response2
		assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Get the response body
		List<Center> centers = response2.getBody();

		// Verify that the list is not null and has at least one item
		assertThat(centers).isNotNull();

		// Check the first center's name
		assertThat(centers.getFirst().getName()).isEqualTo("Spain Center");
	}

	// LISTING NO CENTERS WHEN EMPTY
	@Test
	void listNoCentersWhenEmpty() {
		String url = "http://localhost:" + port + "/api/centers";;

		// Send a GET request and receive the list with all the centers
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
				null, String.class
		);

		// Verify the response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(response.getBody()).contains("There is no logistics center registered at this time");
	}

	// 1.3) UPDATE A CENTER

	// WITH INVALID CAPACITY ("S", "M", "B")
	@Test
	void updateCenterInvalidCapacity() {
		String urlCreate = "http://localhost:" + port + "/api/centers";
		String urlUpdate = "http://localhost:" + port + "/api/centers/{id}";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("S");
		centerRequest.setStatus("OCCUPIED");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> responseCreate = restTemplate.postForEntity(urlCreate, centerRequest, String.class);

		// Verify the response
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);


		// Send a GET request and receive the list with all the centers
		ResponseEntity<List<Center>> responseList = restTemplate.exchange(urlCreate, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Center>>() {
				}
		);

		// Verify the responseList
		assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Get the response body
		assertThat(responseList.getBody()).isNotNull();
		Center centerCreated = responseList.getBody().getFirst();

		centerCreated.setCapacity("P"); // Invalid capacity
		Long id = centerCreated.getId();

		// Wrap center object and headers into HttpEntity
		HttpEntity<Center> requestEntity = new HttpEntity<>(centerCreated);

		// Send a PATCH request and receive a response
		ResponseEntity<String> responseUpdate = restTemplate.exchange(urlUpdate,HttpMethod.PATCH,
				requestEntity, String.class, id);

		// Verify the response
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseUpdate.getBody()).contains("Invalid capacity value");
	}

	// WITH INVALID STATUS ("AVAILABLE", "OCCUPIED")
	@Test
	void updateCenterInvalidStatus() {
		String urlCreate = "http://localhost:" + port + "/api/centers";
		String urlUpdate = "http://localhost:" + port + "/api/centers/{id}";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("S");
		centerRequest.setStatus("OCCUPIED");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> responseCreate = restTemplate.postForEntity(urlCreate, centerRequest, String.class);

		// Verify the response
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);


		// Send a GET request and receive the list with all the centers
		ResponseEntity<List<Center>> responseList = restTemplate.exchange(urlCreate, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Center>>() {
				}
		);

		// Verify the responseList
		assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Get the response body
		assertThat(responseList.getBody()).isNotNull();
		Center centerCreated = responseList.getBody().getFirst();

		centerCreated.setStatus("STALE"); // Invalid status
		Long id = centerCreated.getId();

		// Wrap center object and headers into HttpEntity
		HttpEntity<Center> requestEntity = new HttpEntity<>(centerCreated);

		// Send a PATCH request and receive a response
		ResponseEntity<String> responseUpdate = restTemplate.exchange(urlUpdate,HttpMethod.PATCH,
				requestEntity, String.class, id);

		// Verify the response
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(responseUpdate.getBody()).contains("Invalid status value");
	}

	// WITH INVALID LOAD (currentLoad > maxCapacity)
	@Test
	void updateCenterInvalidLoad() {
		String urlCreate = "http://localhost:" + port + "/api/centers";
		String urlUpdate = "http://localhost:" + port + "/api/centers/{id}";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("S");
		centerRequest.setStatus("OCCUPIED");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> responseCreate = restTemplate.postForEntity(urlCreate, centerRequest, String.class);

		// Verify the response
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);


		// Send a GET request and receive the list with all the centers
		ResponseEntity<List<Center>> responseList = restTemplate.exchange(urlCreate, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Center>>() {
				}
		);

		// Verify the responseList
		assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Get the response body
		assertThat(responseList.getBody()).isNotNull();
		Center centerCreated = responseList.getBody().getFirst();

		centerCreated.setCurrentLoad(3);
		centerCreated.setMaxCapacity(2); // currentLoad > MaxCapacity
		Long id = centerCreated.getId();

		// Wrap center object and headers into HttpEntity
		HttpEntity<Center> requestEntity = new HttpEntity<>(centerCreated);

		// Send a PATCH request and receive a response
		ResponseEntity<String> responseUpdate = restTemplate.exchange(urlUpdate,HttpMethod.PATCH,
				requestEntity, String.class, id);

		// Verify the response
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(responseUpdate.getBody()).contains("Current load cannot exceed max capacity.");
	}

	// CENTER NOT FOUND
	@Test
	void updateCenterNotFound() {
		String urlCreate = "http://localhost:" + port + "/api/centers";
		String urlUpdate = "http://localhost:" + port + "/api/centers/{id}";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("S");
		centerRequest.setStatus("OCCUPIED");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> responseCreate = restTemplate.postForEntity(urlCreate, centerRequest, String.class);

		// Verify the response
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);


		// Send a GET request and receive the list with all the centers
		ResponseEntity<List<Center>> responseList = restTemplate.exchange(urlCreate, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Center>>() {
				}
		);

		// Verify the responseList
		assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Get the response body
		assertThat(responseList.getBody()).isNotNull();
		Center centerCreated = responseList.getBody().getFirst();

		centerCreated.setMaxCapacity(5); // Change any value
		Long id = centerCreated.getId();

		// Wrap center object and headers into HttpEntity
		HttpEntity<Center> requestEntity = new HttpEntity<>(centerCreated);

		// Send a PATCH request and receive a response
		ResponseEntity<String> responseUpdate = restTemplate.exchange(urlUpdate,HttpMethod.PATCH,
				requestEntity, String.class, id+1); // Modifying a non-existing center

		// Verify the response
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseUpdate.getBody()).contains("Center not found.");
	}

	// CENTER ALREADY REGISTERED IN GIVEN COORDINATES
	@Test
	void updateCenterCoordinatesTaken() {
		String urlCreate = "http://localhost:" + port + "/api/centers";
		String urlUpdate = "http://localhost:" + port + "/api/centers/{id}";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("S");
		centerRequest.setStatus("OCCUPIED");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> responseCreate = restTemplate.postForEntity(urlCreate, centerRequest, String.class);

		// Verify the response
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// Creating a 2nd center in different location
		centerRequest.setCoordinates(new Coordinates(0.2, 0.4));
		ResponseEntity<String> responseCreate2 = restTemplate.postForEntity(urlCreate, centerRequest, String.class);
		assertThat(responseCreate2.getStatusCode()).isEqualTo(HttpStatus.CREATED);


		// Send a GET request and receive the list with all the centers
		ResponseEntity<List<Center>> responseList = restTemplate.exchange(urlCreate, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Center>>() {
				}
		);

		// Verify the responseList
		assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Get the response body
		assertThat(responseList.getBody()).isNotNull();
		Center centerCreated1 = responseList.getBody().get(0);
		Center centerCreated2 = responseList.getBody().get(1);

		// Match the same coordinates on the 2nd center
		centerCreated2.setCoordinates(centerCreated1.getCoordinates());
		Long id = centerCreated2.getId();

		// Wrap center object and headers into HttpEntity
		HttpEntity<Center> requestEntity = new HttpEntity<>(centerCreated2);

		// Send a PATCH request and receive a response
		ResponseEntity<String> responseUpdate = restTemplate.exchange(urlUpdate,HttpMethod.PATCH,
				requestEntity, String.class, id);

		// Verify the response
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		assertThat(responseUpdate.getBody()).contains("There is already a logistics center in that position.");
	}

	// VALID CENTER IS UPDATED
	@Test
	void updateCenterOk() {
		String urlCreate = "http://localhost:" + port + "/api/centers";
		String urlUpdate = "http://localhost:" + port + "/api/centers/{id}";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("S");
		centerRequest.setStatus("OCCUPIED");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> responseCreate = restTemplate.postForEntity(urlCreate, centerRequest, String.class);

		// Verify the response
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);


		// Send a GET request and receive the list with all the centers
		ResponseEntity<List<Center>> responseList = restTemplate.exchange(urlCreate, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Center>>() {
				}
		);

		// Verify the responseList
		assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Get the response body
		assertThat(responseList.getBody()).isNotNull();
		Center centerCreated = responseList.getBody().getFirst();

		centerCreated.setMaxCapacity(5); // Change any value
		Long id = centerCreated.getId();

		// Wrap center object and headers into HttpEntity
		HttpEntity<Center> requestEntity = new HttpEntity<>(centerCreated);

		// Send a PATCH request and receive a response
		ResponseEntity<String> responseUpdate = restTemplate.exchange(urlUpdate,HttpMethod.PATCH,
				requestEntity, String.class, id);

		// Verify the response
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseUpdate.getBody()).contains("Logistics center updated successfully.");
	}

	// 1.4) DELETE A CENTER

	// CENTER DOESN'T EXIST
	@Test
	void deleteCenterNotExist() {
		String urlCreate = "http://localhost:" + port + "/api/centers";
		String urlDelete = "http://localhost:" + port + "/api/centers/{id}";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("S");
		centerRequest.setStatus("OCCUPIED");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> responseCreate = restTemplate.postForEntity(urlCreate, centerRequest, String.class);

		// Verify the response
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);


		// Send a GET request and receive the list with all the centers
		ResponseEntity<List<Center>> responseList = restTemplate.exchange(urlCreate, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Center>>() {
				}
		);

		// Verify the responseList
		assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Get the response body
		assertThat(responseList.getBody()).isNotNull();
		Center centerCreated = responseList.getBody().getFirst();

		Long id = centerCreated.getId();

		// Send a PATCH request and receive a response
		ResponseEntity<String> responseUpdate = restTemplate.exchange(urlDelete,HttpMethod.DELETE,
				null, String.class, id+1); // Deleting a non-existing center


		// Verify the response
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(responseUpdate.getBody()).contains("Center not found.");
	}

	// SUCCESSFUL DELETION
	@Test
	void deleteCenterOk() {
		String urlCreate = "http://localhost:" + port + "/api/centers";
		String urlDelete = "http://localhost:" + port + "/api/centers/{id}";

		CenterRequest centerRequest = new CenterRequest();
		centerRequest.setName("Spain Center");
		centerRequest.setCapacity("S");
		centerRequest.setStatus("OCCUPIED");
		centerRequest.setCurrentLoad(2);
		centerRequest.setMaxCapacity(2);
		centerRequest.setCoordinates(new Coordinates(0.2, 0.3));

		// Send a POST request and receive a response
		ResponseEntity<String> responseCreate = restTemplate.postForEntity(urlCreate, centerRequest, String.class);

		// Verify the response
		assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);


		// Send a GET request and receive the list with all the centers
		ResponseEntity<List<Center>> responseList = restTemplate.exchange(urlCreate, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Center>>() {
				}
		);

		// Verify the responseList
		assertThat(responseList.getStatusCode()).isEqualTo(HttpStatus.OK);

		// Get the response body
		assertThat(responseList.getBody()).isNotNull();
		Center centerCreated = responseList.getBody().getFirst();

		Long id = centerCreated.getId();

		// Send a PATCH request and receive a response
		ResponseEntity<String> responseUpdate = restTemplate.exchange(urlDelete,HttpMethod.DELETE,
				null, String.class, id);


		// Verify the response
		assertThat(responseUpdate.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseUpdate.getBody()).contains("Logistics center deleted successfully.");
	}

}
