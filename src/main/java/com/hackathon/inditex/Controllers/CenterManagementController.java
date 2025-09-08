package com.hackathon.inditex.Controllers;

import com.hackathon.inditex.DTO.CenterRequest;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Services.CenterManagementService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing logistics centers.
 * Defines endpoints for CRUD operations on the Center entity.
 * Check /docs/api endpoint for more information
 */
@Tag(name = "Logistics Center Endpoints", description = "Endpoints for managing logistics centers")
@RestController
@RequestMapping("/api/centers")
public class CenterManagementController {

    // Autowiring the CenterManagementService bean from the ApplicationContext
    @Autowired
    private CenterManagementService centerManagementService;


    @Operation(
            summary = "Register a new logistics center",
            description = "Creates a new logistics center with the given payload data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Center created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid status or capacity", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error: Existing center in given coordinates or load exceeds capacity",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> createNewCenter(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    implementation = CenterRequest.class,
                                    description = "Payload containing center details"
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody CenterRequest centerRequest
    ) {
        String responseMessage = centerManagementService.createNewCenter(centerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }


    @Operation(
            summary = "Read all registered logistics centers",
            description = "Returns all registered logistics centers at the time of the request"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Existing centers read successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Center.class)))),
            @ApiResponse(responseCode = "500", description = "Server error: No existing centers to be read",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<?> readAllCenters() {
        List<Center> centerList = centerManagementService.readAllCenters();
        return ResponseEntity.ok(centerList);
    }


    @Operation(
            summary = "Update a registered logistics center",
            description = "Updates an already registered logistics center with the given payload data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Center updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid status or capacity", content = @Content),
            @ApiResponse(responseCode = "404", description = "Provided center {id} doesn't exist", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error: Existing center in given coordinates or load exceeds capacity",
                    content = @Content)
    })
    @PatchMapping("{id}")
    public ResponseEntity<?> updateCenter(
            @Parameter(description = "ID of the center to retrieve", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(
                            implementation = CenterRequest.class,
                            description = "Payload containing center details"
                    )
            )
    )
    @org.springframework.web.bind.annotation.RequestBody CenterRequest centerRequest) {
        String responseMessage = centerManagementService.updateCenter(id, centerRequest);
        return ResponseEntity.ok(responseMessage);
    }


    @Operation(
            summary = "Delete a logistics center",
            description = "Delete an existing logistics center"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Center deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Provided center {id} doesn't exist", content = @Content)
    })
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCenter(
            @Parameter(description = "ID of the center to retrieve", required = true)
            @PathVariable Long id) {
        String responseMessage = centerManagementService.deleteCenter(id);
        return ResponseEntity.ok(responseMessage);
    }
}
