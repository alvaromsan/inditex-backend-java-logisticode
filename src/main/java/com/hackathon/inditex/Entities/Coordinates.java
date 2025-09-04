package com.hackathon.inditex.Entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the coordinates of an order or logistics center.
 * Contains basic coordinates information : latitude and longitude.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Coordinates {
    /** Latitude of the coordinates */
    private Double latitude;
    /** Longitude of the coordinates */
    private Double longitude;
}
