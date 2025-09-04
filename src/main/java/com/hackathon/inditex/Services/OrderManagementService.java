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

/**
 * Service class responsible for managing orders.
 *
 * Contains business logic for operations such as creating, reading
 * and assigning orders.
 */
@Service
public class OrderManagementService {

    // Autowiring the OrderRepository bean from the ApplicationContext
    @Autowired
    private OrderRepository orderRepository;

    // Autowiring the CenterRepository bean from the ApplicationContext
    @Autowired
    private CenterRepository centerRepository;

    // Valid values for the Order#size attribute
    private static final Set<String> VALID_SIZES = Set.of(
            "B", "M", "S"
    );

    // Initial creation value for the Order#status attribute
    private static final String INITIAL_ORDER_STATUS = "PENDING";

    /**
     * Creates a new order based on the provided orderRequest.
     *
     * @param orderRequest the payload containing order details
     * @return {@link OrderResponse} containing the created order’s details along with a success message
     * @throws ResponseStatusException if the request is invalid: missing customerId, invalid size, or missing coordinates
     */
    public OrderResponse createNewOrder(OrderRequest orderRequest) {
        final String SUCCESS_MESSAGE = "Order created successfully in PENDING status.";

        // validates if request is invalid: missing customerId, invalid size, or missing coordinates
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

    /**
     * Retrieves all registered orders.
     *
     * @return a list of all currently registered orders
     * @throws RuntimeException if no orders are registered in the system
     */
    public List<Order> readAllOrders(){
        List<Order> orderList= orderRepository.findAll();

        // Verify the list is not empty
        if (orderList.isEmpty()) {
            throw new RuntimeException("There is no orders registered at this time");
        }

        // Return the list of centers
        return orderList;
    }

    /**
     * Assigns all orders with "PENDING" status to available logistics centers.
     *
     * @return an {@link AssignationResponse} containing the list of order assignments
     * @throws RuntimeException if there are no pending orders or no available logistics centers
     */
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


    /**
     * Processes a single pending order by finding a suitable logistics center.
     *
     * The method performs the following steps:
     * 1. Filters all centers that support the order's size.
     * 2. Filters centers whose current load is below their maximum capacity.
     * 3. Finds the closest center to the order's coordinates.
     * 4. (if a center is found)Updates the center's current load and the order's status and assigned center.
     * 5. Records the result in the provided orderAssignations list.
     *
     * If no suitable center is found, an OrderAssignation is still created with a message
     * explaining why the order could not be assigned.
     *
     * @param order the pending order to be assigned
     * @param centerList the list of centers to consider for assignment; center load will be updated here
     * @param orderAssignations the list where the assignment result will be added
     */
    private void processPendingOrder(Order order, List<Center> centerList, List<OrderAssignation> orderAssignations) {
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

    /**
     * Calculates the great-circle distance between two geographic points using the Haversine formula.
     * Used to calculate the distance between order-center
     *
     * @param startLat the latitude of the starting point in decimal degrees
     * @param startLong the longitude of the starting point in decimal degrees
     * @param endLat the latitude of the ending point in decimal degrees
     * @param endLong the longitude of the ending point in decimal degrees
     * @return the distance between the two points in kilometers
     */
    private double calculateDistance(double startLat, double startLong, double endLat, double endLong) {
        final int EARTH_RADIUS = 6371; // Radius of the Earth in kilometers

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * Calculates the haversine of an angle.
     *
     * This is a helper method used in the Haversine formula for computing distances between two points on a sphere.
     *
     * @param val the angle in radians
     * @return the haversine of the given angle
     */
    private double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }


    /**
     * Checks if the given order size is valid.
     *
     * @param size the size to check
     * @return {@code true} if the size is non-null and contained in {@link #VALID_SIZES}, {@code false} otherwise
     */
    private boolean isValidSize(String size) {
        return size != null && VALID_SIZES.contains(size);
    }

    /**
     * Validates the given OrderRequest.
     *
     * Validates:
     * - customerId is not null
     * - size is valid ("B", "M", "S")
     * - coordinates (latitude and longitude) are not null
     *
     * Throws a 400 Bad Request ResponseStatusException if any check fails.
     * @param orderRequest the order request to validate
     */
    private void validateOrderRequest(OrderRequest orderRequest) {
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
