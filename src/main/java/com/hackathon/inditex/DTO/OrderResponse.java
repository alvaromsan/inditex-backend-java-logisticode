package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private String size;
    private String assignedLogisticsCenter;
    private Coordinates coordinates;
    private String status;
    private String message;
}
