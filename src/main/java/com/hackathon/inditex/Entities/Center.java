package com.hackathon.inditex.Entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a logistics center in the system.
 * Contains basic information from the center: id, name, capacity,
 * status, currentLoad, maxCapacity and coordinates.
 */
@Data
@Entity
@Table(name = "centers", uniqueConstraints = @UniqueConstraint(columnNames = {"latitude", "longitude"}))
public class Center {

    /** Unique logistics center identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Name of the logistics center. */
    private String name;

    /**
     * Order size acceptance of the logistics center. Expected values:
     * "S", "M", "B" or a combination of two or all
     */
    private String capacity;

    /** Center status. Expected values: "AVAILABLE" or "OCCUPIED" */
    private String status;

    /** Center current load of orders. Cannot exceed maxCapacity number */
    private Integer currentLoad;

    /** Center maximum orders capacity */
    private Integer maxCapacity;

    /** Center coordinates location */
    @Embedded
    private Coordinates coordinates;
}
