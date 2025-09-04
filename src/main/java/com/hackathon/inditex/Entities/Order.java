package com.hackathon.inditex.Entities;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents an order in the system.
 * Contains basic information from the order: id, customerId, size,
 * status, assignedCenter and coordinates.
 */
@Data
@Entity
@Table(name = "orders")
public class Order {
    /** Unique order identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Customer identification number. */
    private Long customerId;

    /** Order size. Expected values: "S", "M" or "B" */
    private String size;

    /** Order status. Expected values: "PENDING" or "ASSIGNED" */
    private String status;

    /** Name of assigned logistics center. */
    private String assignedCenter;

    /** Order coordinates location */
    @Embedded
    private Coordinates coordinates;
}

