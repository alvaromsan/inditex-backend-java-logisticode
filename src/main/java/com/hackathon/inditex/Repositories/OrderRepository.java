package com.hackathon.inditex.Repositories;

import com.hackathon.inditex.Entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusOrderByIdAsc(String status);
}
