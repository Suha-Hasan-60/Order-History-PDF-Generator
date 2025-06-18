package com.example.orderpdf.repository;

import com.example.orderpdf.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdAndOrderDateBetween(Integer customerId, LocalDate from, LocalDate to);
}
