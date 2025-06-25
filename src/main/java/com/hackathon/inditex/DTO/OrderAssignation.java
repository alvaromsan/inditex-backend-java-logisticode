package com.hackathon.inditex.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderAssignation {
    private Double distance;
    private Long orderId;
    private String assignedLogisticsCenter;
    private String status;
    private String message;
}
