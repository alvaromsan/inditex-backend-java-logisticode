package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;
import lombok.Data;

/**
 * DTO representing an Order Request
 * This is the payload for the order creation POST endpoint.
 */
@Data
public class OrderRequest {

    /** Customer identification number */
    private Long customerId;

    /**
     * Order size.
     * Expected values: "B", "M" or "S".
     */
    private String size;

    /** Order coordinates location */
    private Coordinates coordinates;

}
