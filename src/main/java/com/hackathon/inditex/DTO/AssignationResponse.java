package com.hackathon.inditex.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * DTO representing a list of the processed Orders with their corresponding assigned logistics Center.
 * This is the payload for the order assignment POST endpoint.
 */
@Schema(description = "Data Transfer Object representing a list of orders with assigned centers")
@Data
@AllArgsConstructor
public class AssignationResponse {

    /** List of all processed Orders */
    @Schema(description = "List of all processed Orders")
    @JsonProperty("processed-orders")
    private List<OrderAssignation> processedOrders;
}
