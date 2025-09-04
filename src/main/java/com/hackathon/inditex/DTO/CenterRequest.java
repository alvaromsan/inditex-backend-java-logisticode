package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;
import lombok.Data;

/**
 * DTO representing a Center.
 * This is the payload for the center creation POST endpoint
 * and center update PATCH endpoint.
 */
@Data // generates getters, setters, toString, equals, hashCode, etc.
public class CenterRequest {
    /** Name of the center */
    private String name;

    /**
     * Center capacity - Indicates what type of order size the center accepts.
     * Expected values: "B", "M", "S" or combinations of two or all.
     */
    private String capacity;

    /**
     * Center status - Indicates whether the center is available to accept orders.
     * Expected values: "AVAILABLE" or "OCCUPIED"
     */
    private String status;

    /** Center maximum order capacity - Indicates how many orders the center can handle at a time. */
    private Integer maxCapacity;

    /**
     * Center current load of orders - Indicates how many orders the center is currently handling
     * Expected value: currentLoad <= maxCapacity
     */
    private Integer currentLoad;

    /** Center coordinates location */
    private Coordinates coordinates;


}