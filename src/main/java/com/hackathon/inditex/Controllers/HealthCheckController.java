package com.hackathon.inditex.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for checking if the container is working properly.
 * Defines a health-check endpoint.
 */

@Tag(name = "Health Check Actuator", description = "Endpoint for checking application status")
@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Operation(
            summary = "Health check actuator",
            description = "Returns an application status message"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Health check actuator",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
    })
    @GetMapping
    public String healthCheck() {
        return "API is working";
    }
}