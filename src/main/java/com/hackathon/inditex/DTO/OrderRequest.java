package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO representing an Order Request
 * This is the payload for the order creation POST endpoint.
 */
@Schema(description = "Data Transfer Object representing an order for its requests")
@Data
public class OrderRequest {

    /** Customer identification number */
    @Schema(description = "Customer identification number")
    private Long customerId;

    /**
     * Order size.
     * Expected values: "B", "M" or "S".
     */
    @Schema(description = "Order size", allowableValues = {"B", "M", "S"})
    private String size;

    /** Order coordinates location */
    @Schema(description = "Order coordinates location")
    private Coordinates coordinates;

}
