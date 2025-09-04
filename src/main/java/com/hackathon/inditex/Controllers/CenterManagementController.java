package com.hackathon.inditex.Controllers;

import com.hackathon.inditex.DTO.CenterRequest;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Services.CenterManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing logistics centers.
 * Defines endpoints for CRUD operations on the Center entity.
 */
@RestController
@RequestMapping("/api/centers")
public class CenterManagementController {

    // Autowiring the CenterManagementService bean from the ApplicationContext
    @Autowired
    private CenterManagementService centerManagementService;

    /**
     * Endpoint to register a new logistics Center.
     *
     *  * Validations:
     *  * - If center status is invalid → 400 Bad Request
     *  * - If center capacity is invalid → 400 Bad Request
     *  * - If coordinates already exist → 500 Internal Server Error
     *  * - If currentLoad exceeds maxCapacity → 500 Internal Server Error
     *
     * @param centerRequest request payload with center details
     * @return 201 Created with confirmation message if successful
     */
    @PostMapping
    public ResponseEntity<?> createNewCenter(@RequestBody CenterRequest centerRequest) {
        String responseMessage = centerManagementService.createNewCenter(centerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    /**
     * Endpoint to read all registered logistics Centers.
     *
     *  * Validations:
     *  * - If no logistics center registered → 500 Internal Server Error
     *
     * @return 200 Ok with all the currently registered Centers
     */
    @GetMapping
    public ResponseEntity<?> readAllCenters() {
        List<Center> centerList = centerManagementService.readAllCenters();
        return ResponseEntity.ok(centerList);
    }

    /**
     * Endpoint to update a specific logistics Center.
     *
     *  * Validations:
     *  * - If The given ID center exists → 404 Not Found
     *
     * @param id logistics center identification number
     * @param centerRequest request payload with center details
     * @return 200 Ok with confirmation message if successful
     */
    @PatchMapping("{id}")
    public ResponseEntity<?> updateCenter(@PathVariable Long id, @RequestBody CenterRequest centerRequest) {
        String responseMessage = centerManagementService.updateCenter(id, centerRequest);
        return ResponseEntity.ok(responseMessage);
    }

    /**
     * Endpoint to update a specific logistics Center.
     *
     *  * Validations:
     *  * - If The given ID center exists → 404 Not Found
     *
     * @param id logistics center identification number
     * @return 200 Ok with confirmation message if successful
     */
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCenter(@PathVariable Long id) {
        String responseMessage = centerManagementService.deleteCenter(id);
        return ResponseEntity.ok(responseMessage);
    }
}
