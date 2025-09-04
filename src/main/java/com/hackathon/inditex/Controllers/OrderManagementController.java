package com.hackathon.inditex.Controllers;

import com.hackathon.inditex.DTO.AssignationResponse;
import com.hackathon.inditex.DTO.OrderAssignation;
import com.hackathon.inditex.DTO.OrderRequest;
import com.hackathon.inditex.DTO.OrderResponse;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Services.OrderManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Orders.
 * Defines endpoints for Create and Read operations on the Order entity
 * and Order-Assignment to an existing Center.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderManagementController {

    // Autowiring the OrderManagementService bean from the ApplicationContext
    @Autowired
    private OrderManagementService orderManagementService;

    /**
     * Endpoint to create a new Order.
     *
     * @param orderRequest request payload with order details
     * @return 201 Created with the created Order details + confirmation message
     */
    @PostMapping
    public ResponseEntity<?> createNewOrder(@RequestBody OrderRequest orderRequest){
        OrderResponse orderResponse = orderManagementService.createNewOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    /**
     * Endpoint to read/list all the registered Orders.
     *
     * * Validations:
     * * - If there are no registered orders → 500 Internal Server Error
     *
     * @return 200 Ok with the list of currently registered orders
     */
    @GetMapping
    public ResponseEntity<?> listAllOrders() {
        List<Order> orderList = orderManagementService.readAllOrders();
        return ResponseEntity.ok(orderList);
    }

    /**
     * Endpoint to assign all the Orders with "PENDING" status to an
     * available logistics Center.
     *
     * * Validations:
     * * - If there are no orders with "PENDING" status → 500 Internal Server Error
     * * - If there are no available logistics center → 500 Internal Server Error
     *
     * @return 200 Ok with the list of the assigned orders with their corresponding
     * assigned logistics center
     */
    @PostMapping("order-assignations")
    public ResponseEntity<?> centerAssignment() {
        AssignationResponse assignationResponse = orderManagementService.orderAssignation();
        return ResponseEntity.ok(assignationResponse);
    }

}
