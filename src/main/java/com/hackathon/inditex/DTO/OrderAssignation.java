package com.hackathon.inditex.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing an Order Assignation. Contains the assigned
 * This is the payload for the AssignationResponse DTO.
 */
@Data
@AllArgsConstructor
public class OrderAssignation {
    /**
     * Distance from the order coordinates to the assigned Center.
     * Expected values:
     * - If order assigned -> Distance (in km)
     * - If order not assigned -> null
     */
    private Double distance;

    /** Order identifier */
    private Long orderId;

    /**
     * Name of assigned logistics center.
     * Expected values:
     * - If order assigned -> Name of Center
     * - If order not assigned -> null
     */
    private String assignedLogisticsCenter;

    /**
     * Order status.
     * Expected values:
     * - If order assigned -> "ASSIGNED"
     * - If order not assigned -> "PENDING"
     */
    private String status;

    /**
     * Confirmation/error message for order assignments
     * Expected values:
     * - If assigned -> "Order assigned"
     * - If not assigned -> "All centers are at maximum capacity." or "No available centers support the order type."
     */
    private String message;
}
