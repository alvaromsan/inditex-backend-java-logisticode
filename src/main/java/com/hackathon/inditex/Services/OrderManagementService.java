package com.hackathon.inditex.Services;

import com.hackathon.inditex.DTO.AssignationResponse;
import com.hackathon.inditex.DTO.OrderAssignation;
import com.hackathon.inditex.DTO.OrderRequest;
import com.hackathon.inditex.DTO.OrderResponse;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Repositories.CenterRepository;
import com.hackathon.inditex.Repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class OrderManagementService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CenterRepository centerRepository;

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

    public AssignationResponse orderAssignation() {
        // List of order assignments
        List<OrderAssignation> orderAssignations = new ArrayList<>();

        List<Order> pendingOrderList = orderRepository.findByStatusOrderByIdAsc("PENDING");
        List<Center> centerList = centerRepository.findByStatus("AVAILABLE");

        if(pendingOrderList.isEmpty()) {
            // There is no pending orders
            throw new RuntimeException("There is no pending orders at this time");
        }

        if (centerList.isEmpty()) {
            // There are no available centers
            throw new RuntimeException("There are no available centers at this time");
        }

        for (Order order: pendingOrderList) {
            processPendingOrder(order,centerList,orderAssignations);
        }
        return new AssignationResponse(orderAssignations);
    }


    // Check:
    // 1) All centers that "allow" the order size
    // 2) All centers whose currentLoad < maxCapacity
    // 3) Closer center from the given order
    // Once a center is found:
    // 1) Update center load (+1)
    // 2) Update order status ("ASSIGNED") and assignedCenter
    public void processPendingOrder(Order order, List<Center> centerList, List<OrderAssignation> orderAssignations) {
        // Map that will store all the centers which support the capacity + its distance to current order
        Map<Center, Double> centerDistances = new HashMap<>();

        // 1) Narrow down the centerList to support the order size
        List<Center> supportingCenters = centerList.stream()
                .filter(c -> c.getCapacity() != null && c.getCapacity().contains(order.getSize()))
                .toList();

        if(supportingCenters.isEmpty()) {
            // No available centers support the order type.
            // Fill the order in the assignations list
            OrderAssignation orderAssignation = new OrderAssignation(
                    null,
                    order.getId(),
                    null,
                    order.getStatus(),
                    "No available centers support the order type."
            );
            orderAssignations.add(orderAssignation);
            return;
        }

        // 2) Narrow down the centerList to admit an order (currentLoad < MaxCapacity)
        List<Center> availableCenterList = supportingCenters.stream()
                .filter(c -> c.getCurrentLoad() < c.getMaxCapacity())
                .toList();
        if(availableCenterList.isEmpty()) {
            // All centers are at maximum capacity.
            // Fill the order in the assignations list
            OrderAssignation orderAssignation = new OrderAssignation(
                    null,
                    order.getId(),
                    null,
                    order.getStatus(),
                    "All centers are at maximum capacity."
            );
            orderAssignations.add(orderAssignation);
            return;
        }

        // 3) Calculate distance for each suitable center
        for (Center center: availableCenterList) {
            String capacity = center.getCapacity();

            // Check this capacity contains order's size
            if(capacity.contains(order.getSize())) {
                // This center supports the order's size
                // Calculate distance
                double distance = calculateDistance(
                        order.getCoordinates().getLatitude(),
                        order.getCoordinates().getLatitude(),
                        center.getCoordinates().getLatitude(),
                        center.getCoordinates().getLongitude()
                );

                // Store this center and its distance in the Map
                centerDistances.put(center,distance);
            }
        }

        // Obtain the center with the shortest distance to the order
        Optional<Map.Entry<Center, Double>> closestEntry = centerDistances.entrySet()
                .stream()
                .min(Map.Entry.comparingByValue());

        if (closestEntry.isPresent()) {
            Center closestCenter = closestEntry.get().getKey();
            double minDistance = closestEntry.get().getValue();

            // Update center load & order status and assignedCenter
            closestCenter.setCurrentLoad(closestCenter.getCurrentLoad()+1);
            centerRepository.save(closestCenter);

            order.setStatus("ASSIGNED");
            order.setAssignedCenter(closestCenter.getName());
            orderRepository.save(order);

            // Update the currentLoad also in the original centerList for future order processing
            centerList.stream()
                    .filter(c -> c.getId().equals(closestCenter.getId()))
                    .findFirst()
                    .ifPresent(c -> c.setCurrentLoad(closestCenter.getCurrentLoad()));

            // Assign order to this center
            OrderAssignation orderAssignation = new OrderAssignation(
                    minDistance,
                    order.getId(),
                    order.getAssignedCenter(),
                    order.getStatus(),
                    "Order assigned"
            );
            orderAssignations.add(orderAssignation);
        }
    }

    public double calculateDistance(double startLat, double startLong, double endLat, double endLong) {
        final int EARTH_RADIUS = 6371; // Radius of the Earth in kilometers

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
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
