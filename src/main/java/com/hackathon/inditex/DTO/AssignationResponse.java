package com.hackathon.inditex.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * DTO representing a list of the processed Orders with their corresponding assigned logistics Center.
 * This is the payload for the order assignment POST endpoint.
 */
@Data
@AllArgsConstructor
public class AssignationResponse {

    /** List of all processed Orders */
    @JsonProperty("processed-orders")
    private List<OrderAssignation> processedOrders;
}
