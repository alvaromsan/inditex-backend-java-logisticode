package com.hackathon.inditex.Services;

import com.hackathon.inditex.DTO.OrderRequest;
import com.hackathon.inditex.DTO.OrderResponse;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class OrderManagementService {

    @Autowired
    private OrderRepository orderRepository;

    private static final Set<String> VALID_SIZES = Set.of(
            "B", "M", "S"
    );

    private static final String INITIAL_ORDER_STATUS = "PENDING";

    public OrderResponse createNewOrder(OrderRequest orderRequest) {
        final String SUCCESS_MESSAGE = "Order created successfully in PENDING status.";

        validateOrderRequest(orderRequest);

        Order newOrder = new Order();
        newOrder.setCustomerId(orderRequest.getCustomerId());
        newOrder.setSize(orderRequest.getSize());
        newOrder.setStatus(INITIAL_ORDER_STATUS);
        newOrder.setCoordinates(orderRequest.getCoordinates());

        // Save the new Order in the database
        orderRepository.save(newOrder);

        return new OrderResponse(
                newOrder.getId(),
                newOrder.getCustomerId(),
                newOrder.getSize(),
                null,
                newOrder.getCoordinates(),
                newOrder.getStatus(),
                SUCCESS_MESSAGE
        );
    }

    public List<Order> readAllOrders(){
        List<Order> orderList= orderRepository.findAll();

        // Verify the list is not empty
        if (orderList.isEmpty()) {
            throw new RuntimeException("There is no orders registered at this time");
        }

        // Return the list of centers
        return orderList;
    }


    // Checks the given Center capacity is valid
    public boolean isValidSize(String size) {
        return size != null && VALID_SIZES.contains(size);
    }

    // Function that verifies 3 checks:
    // 1) customerId field is not empty
    // 2) size field is valid ("B", "M", "S")
    // 3) coordinates field is not empty
    public void validateOrderRequest(OrderRequest orderRequest) {
        // Verify customerId is not empty
        if (orderRequest.getCustomerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Empty customerId value");
        }

        // Verify the size value is valid
        if (!isValidSize(orderRequest.getSize())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid size value");
        }

        // Verify coordinates is not empty
        boolean isCoordinatesNull = false;
        if (orderRequest.getCoordinates() != null) {
            Coordinates coordinates = orderRequest.getCoordinates();
            if (coordinates.getLatitude() == null) {
                isCoordinatesNull = true;
            }
            if (coordinates.getLongitude() == null ) {
                isCoordinatesNull = true;
            }
        }
        else {
            isCoordinatesNull = true;
        }
        if (isCoordinatesNull) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Empty coordinates values");
        }
    }
}
