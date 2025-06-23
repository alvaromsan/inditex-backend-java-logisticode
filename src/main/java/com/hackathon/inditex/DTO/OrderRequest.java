package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;
import lombok.Data;

@Data
public class OrderRequest {
    private Long customerId;
    private String size;
    private Coordinates coordinates;

}
