package com.hackathon.inditex.Controllers;

import com.hackathon.inditex.DTO.*;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Services.OrderManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name="Order Endpoints", description = "Endpoints for managing orders")
@RestController
@RequestMapping("/api/orders")
public class OrderManagementController {

    // Autowiring the OrderManagementService bean from the ApplicationContext
    @Autowired
    private OrderManagementService orderManagementService;

    @Operation(
            summary = "Register a new order",
            description = "Creates a order with the given payload data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Missing id or coordinates or invalid size", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> createNewOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    implementation = OrderRequest.class,
                                    description = "Payload containing order details"
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody OrderRequest orderRequest){
        OrderResponse orderResponse = orderManagementService.createNewOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }


    @Operation(
            summary = "Read all the registered orders",
            description = "Returns all registered orders at the time of the request"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Existing orders successfully",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(responseCode = "500", description = "Server error: No existing orders to be read", content = @Content)
    })
    @GetMapping
    public ResponseEntity<?> listAllOrders() {
        List<Order> orderList = orderManagementService.readAllOrders();
        return ResponseEntity.ok(orderList);
    }

    @Operation(
            summary = "Assign the 'PENDING' orders",
            description = "Assign all the orders with 'PENDING' status to an available logistics Center"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders assigned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AssignationResponse.class))),
            @ApiResponse(responseCode = "500", description = "No 'PENDING' orders or available logistics centers", content = @Content)
    })
    @PostMapping("order-assignations")
    public ResponseEntity<?> centerAssignment() {
        AssignationResponse assignationResponse = orderManagementService.orderAssignation();
        return ResponseEntity.ok(assignationResponse);
    }

}
