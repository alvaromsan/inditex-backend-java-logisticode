package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;
import lombok.Data;

import java.util.List;

@Data // generates getters, setters, toString, equals, hashCode, etc.
public class CenterRequest {
    private String name;
    private String capacity; // Accepts strings like "B", "MS", "BMS"
    private String status;   // Accepts "AVAILABLE" or "ASSIGNED"
    private int maxCapacity;
    private int currentLoad;
    private Coordinates coordinates;
}