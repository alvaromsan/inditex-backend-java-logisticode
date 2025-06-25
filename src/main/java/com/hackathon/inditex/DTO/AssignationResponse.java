package com.hackathon.inditex.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AssignationResponse {

    @JsonProperty("processed-orders")
    private List<OrderAssignation> processedOrders;
}
