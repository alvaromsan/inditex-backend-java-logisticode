package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing an Order Response.
 * This is the response for the order creation POST endpoint.
 */

@Schema(description = "Data Transfer Object representing a Center for its responses")
@Data
@AllArgsConstructor
public class OrderResponse {

    /** Order identification number */
    @Schema(description = "Order identification number")
    private Long orderId;

    /** Customer identification number */
    @Schema(description = "Customer identification number")
    private Long customerId;

    /**
     * Order size.
     * Expected values: "B", "M" or "S".
     */
    @Schema(description = "Order size", allowableValues = {"B", "M", "S"})
    private String size;

    /**
     * Name of assigned logistics center. This value will be modified once order is assigned
     * Expected initial value: null.
     */
    @Schema(description = "Name of the assigned logistics center")
    private String assignedLogisticsCenter;

    /** Order coordinates location */
    @Schema(description = "Order coordinates location")
    private Coordinates coordinates;

    /**
     * Order status. This value will be modified once order is assigned.
     * Expected initial value: "PENDING".
     */
    @Schema(description = "Order status", defaultValue = "PENDING", allowableValues = {"PENDING", "ASSIGNED"})
    private String status;

    /**
     * Confirmation message once order is created
     * Expected value: "Order created successfully in PENDING status."
     */
    @Schema(description = "Confirmation message once order is created")
    private String message;

}
