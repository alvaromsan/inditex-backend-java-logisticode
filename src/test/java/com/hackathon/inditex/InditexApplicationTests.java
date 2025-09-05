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


/**
 * Integration tests for the Inditex application.
 * <p>
 * This class uses Spring Boot's testing support to start a real web environment
 * on a random port. It also uses a test-specific profile ("test") and resets
 * the database before each test method to ensure isolation.
 * <p>
 * The {@link PatchSupportConfig} is imported to provide a {@link org.springframework.web.client.RestTemplate}
 * capable of sending PATCH requests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
// Import this bean to allow PATCH method in the restTemplate
@Import(PatchSupportConfig.class)
// Assure DB modifications from one test don't affect the others
@Sql(scripts = "/sql/reset-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class InditexApplicationTests {

	/** Injects the random port number the server is running on during tests. */
	@LocalServerPort
	private int port;

	/** RestTemplate for sending HTTP requests to the test server. */
	@Autowired
	private TestRestTemplate restTemplate;

	// ========================================================
	// 1) CENTER MANAGEMENT TESTS
	// ========================================================

	// 1.1) CREATE A CENTER

	/**
	 * Test creating a Center with an invalid capacity.
	 * <p>
	 * This test verifies that sending a POST request with an invalid capacity value
	 * (e.g., "P" instead of allowed values "S", "M", "B") results in a 400 Bad Request
	 * response. It also checks that the response body contains an appropriate error message.
	 */
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

	/**
	 * Test creating a Center with an invalid status value.
	 * <p>
	 * Allowed status values are "AVAILABLE" and "OCCUPIED".
	 * This test sends a POST request with an invalid status ("STALE") and verifies
	 * that the server responds with 400 Bad Request and an appropriate error message.
	 */
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

	/**
	 * Test creating a Center with a current load greater than max capacity.
	 * <p>
	 * Sending a POST request where currentLoad > maxCapacity should result in
	 * an INTERNAL_SERVER_ERROR response and a descriptive error message.
	 */
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

	/**
	 * Test creating a Center that already exists at the same coordinates.
	 * <p>
	 * The first POST request should succeed and create the center.
	 * A second identical POST request should fail with INTERNAL_SERVER_ERROR,
	 * because a center already exists at those coordinates.
	 */
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

	/**
	 * Test creating a valid Center.
	 * <p>
	 * Sends a POST request with all valid fields. Expects the server to
	 * respond with CREATED status and a success message.
	 */
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

	/**
	 * Test listing all centers when at least one center exists.
	 * <p>
	 * This test first creates a new center using a POST request. Then, it sends
	 * a GET request to retrieve all centers. It verifies that:
	 * <ul>
	 *     <li>The POST request succeeds with status CREATED.</li>
	 *     <li>The GET request succeeds with status OK.</li>
	 *     <li>The returned list of centers is not null and contains at least one element.</li>
	 *     <li>The first center's name matches the one that was created.</li>
	 * </ul>
	 */
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

	/**
	 * Test listing centers when no centers exist.
	 * <p>
	 * This test sends a GET request to retrieve centers from an empty database.
	 * It verifies that the server responds with INTERNAL_SERVER_ERROR and a
	 * descriptive message indicating that no centers are registered.
	 */
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

	/**
	 * Test updating a center with an invalid capacity.
	 * <p>
	 * Attempts to set a capacity value that is not allowed ("P"). Expects
	 * a BAD_REQUEST status with an appropriate error message.
	 */
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

	/**
	 * Test updating a center with an invalid status.
	 * <p>
	 * Attempts to set a status value that is not allowed ("STALE"). Expects
	 * a BAD_REQUEST status with an appropriate error message.
	 */
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

	/**
	 * Test updating a center where current load exceeds max capacity.
	 * <p>
	 * Expects an INTERNAL_SERVER_ERROR with an appropriate error message.
	 */
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

	/**
	 * Test updating a center that does not exist.
	 * <p>
	 * Expects a NOT_FOUND status with a "Center not found" message.
	 */
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

	/**
	 * Test updating a center to coordinates that are already taken by another center.
	 * <p>
	 * Expects an INTERNAL_SERVER_ERROR with an appropriate message.
	 */
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

	/**
	 * Test successfully updating a valid center.
	 * <p>
	 * Expects an OK status and confirmation message.
	 */
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

	/**
	 * Test case for attempting to delete a logistics center that does not exist.
	 * <p>
	 * Steps:
	 * <ol>
	 *     <li>Create a new center via POST request.</li>
	 *     <li>Retrieve the list of centers and get the ID of the created center.</li>
	 *     <li>Attempt to delete a center using a non-existing ID (ID + 1).</li>
	 *     <li>Verify that the response returns HTTP 404 NOT FOUND with the appropriate error message.</li>
	 * </ol>
	 */
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

	/**
	 * Test case for successfully deleting an existing logistics center.
	 * <p>
	 * Steps:
	 * <ol>
	 *     <li>Create a new center via POST request.</li>
	 *     <li>Retrieve the list of centers and get the ID of the created center.</li>
	 *     <li>Send a DELETE request using the correct center ID.</li>
	 *     <li>Verify that the response returns HTTP 200 OK with a success message.</li>
	 * </ol>
	 */
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
