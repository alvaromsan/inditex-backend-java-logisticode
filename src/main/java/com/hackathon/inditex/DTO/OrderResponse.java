package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing an Order Response.
 * This is the response for the order creation POST endpoint.
 */
@Data
@AllArgsConstructor
public class OrderResponse {

    /** Order identification number */
    private Long orderId;

    /** Customer identification number */
    private Long customerId;

    /**
     * Order size.
     * Expected values: "B", "M" or "S".
     */
    private String size;

    /**
     * Name of assigned logistics center. This value will be modified once order is assigned
     * Expected initial value: null.
     */
    private String assignedLogisticsCenter;

    /** Order coordinates location */
    private Coordinates coordinates;

    /**
     * Order status. This value will be modified once order is assigned.
     * Expected initial value: "PENDING".
     */
    private String status;

    /**
     * Confirmation message once order is created
     * Expected value: "Order created successfully in PENDING status."
     */
    private String message;

}
