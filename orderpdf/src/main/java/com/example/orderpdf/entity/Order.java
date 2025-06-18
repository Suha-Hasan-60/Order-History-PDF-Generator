package com.example.orderpdf.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "product_name")
    private String productName;

    private Integer quantity;
    private Double price;

    @Column(name = "order_date")
    private LocalDate orderDate;
}
