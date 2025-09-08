package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO representing a Center.
 * This is the payload for the center creation POST endpoint
 * and center update PATCH endpoint.
 */
@Schema(description = "Data Transfer Object representing a Center for its requests")
@Data // generates getters, setters, toString, equals, hashCode, etc.
public class CenterRequest {
    /** Name of the center */
    @Schema(description = "Name of the center")
    private String name;

    /**
     * Center capacity - Indicates what type of order size the center accepts.
     * Expected values: "B", "M", "S" or combinations of two or all.
     */
    @Schema(description = "Center capacity", example = "B,M,S", allowableValues = {"B", "M", "S", "BM", "BS", "MS", "BMS"})
    private String capacity;

    /**
     * Center status - Indicates whether the center is available to accept orders.
     * Expected values: "AVAILABLE" or "OCCUPIED"
     */
    @Schema(description = "Center status", allowableValues = "AVAILABLE, OCCUPIED")
    private String status;


    /** Center maximum order capacity - Indicates how many orders the center can handle at a time. */
    @Schema(description = "Center maximum order capacity")
    private Integer maxCapacity;

    /**
     * Center current load of orders - Indicates how many orders the center is currently handling
     * Expected value: currentLoad <= maxCapacity
     */
    @Schema(description = "Center current load of orders")
    private Integer currentLoad;

    /** Center coordinates location */
    @Schema(description = "Center coordinates location")
    private Coordinates coordinates;


}