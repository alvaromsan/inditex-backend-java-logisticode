package com.hackathon.inditex.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing an Order Assignation. Contains the assigned
 * This is the payload for the AssignationResponse DTO.
 */

@Schema(description = "Data Transfer Object representing an Order Assignation")
@Data
@AllArgsConstructor
public class OrderAssignation {
    /**
     * Distance from the order coordinates to the assigned Center.
     * Expected values:
     * - If order assigned -> Distance (in km)
     * - If order not assigned -> null
     */
    @Schema(description = "Distance from the order coordinates to the assigned Center (in km)")
    private Double distance;

    /** Order identifier */
    @Schema(description = "Order identifier")
    private Long orderId;

    /**
     * Name of assigned logistics center.
     * Expected values:
     * - If order assigned -> Name of Center
     * - If order not assigned -> null
     */
    @Schema(description = "Name of the assigned logistics center")
    private String assignedLogisticsCenter;

    /**
     * Order status.
     * Expected values:
     * - If order assigned -> "ASSIGNED"
     * - If order not assigned -> "PENDING"
     */
    @Schema(description = "Order status", allowableValues = {"ASSIGNED", "PENDING"})
    private String status;

    /**
     * Confirmation/error message for order assignments
     * Expected values:
     * - If assigned -> "Order assigned"
     * - If not assigned -> "All centers are at maximum capacity." or "No available centers support the order type."
     */
    @Schema(description = "Confirmation/error message for order assignments", example = "Order assigned")
    private String message;
}
