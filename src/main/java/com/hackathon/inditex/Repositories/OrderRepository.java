package com.hackathon.inditex.Repositories;

import com.hackathon.inditex.Entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on {@link Order} entities.
 *
 * Extends {@link JpaRepository} to provide standard database operations.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Retrieves all orders with the specified status, ordered by their ID in ascending order.
     *
     * @param status the status to filter orders by
     * @return a list of orders matching the given status, sorted by ID ascending;
     *         empty list if none found
     */
    List<Order> findByStatusOrderByIdAsc(String status);
}
