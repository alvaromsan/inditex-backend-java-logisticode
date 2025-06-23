package com.hackathon.inditex.Controllers;

import com.hackathon.inditex.DTO.OrderRequest;
import com.hackathon.inditex.DTO.OrderResponse;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Services.OrderManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderManagementController {

    @Autowired
    private OrderManagementService orderManagementService;

    @PostMapping
    public ResponseEntity<?> createNewOrder(@RequestBody OrderRequest orderRequest){
        OrderResponse orderResponse = orderManagementService.createNewOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @GetMapping
    public  ResponseEntity<?> listAllOrders() {
        List<Order> orderList = orderManagementService.readAllOrders();
        return ResponseEntity.ok(orderList);
    }

}
