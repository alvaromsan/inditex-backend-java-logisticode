package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;
import lombok.Data;

@Data // generates getters, setters, toString, equals, hashCode, etc.
public class CenterRequest {
    private String name;
    private String capacity; // Accepts strings like "B", "MS", "BMS"
    private String status;   // Accepts "AVAILABLE" or "ASSIGNED"
    private Integer maxCapacity;
    private Integer currentLoad;
    private Coordinates coordinates;
}