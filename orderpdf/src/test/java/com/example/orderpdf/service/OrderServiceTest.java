package com.example.orderpdf.service;

import com.example.orderpdf.dto.OrderRequestDto;
import com.example.orderpdf.entity.Order;
import com.example.orderpdf.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void runTestAndSavePdf(OrderRequestDto dto, List<Order> mockOrders, String filename) throws IOException {
        when(orderRepository.findByCustomerIdAndOrderDateBetween(any(), any(), any()))
                .thenReturn(mockOrders);

        var result = orderService.generateReport(dto);
        assertNotNull(result);

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(result.readAllBytes());
            System.out.println("✅ PDF saved: " + filename);
        }
    }

    private Order createOrder(String productName, int qty, double price, String date) {
        Order order = new Order();
        order.setProductName(productName);
        order.setQuantity(qty);
        order.setPrice(price);
        order.setOrderDate(LocalDate.parse(date));
        return order;
    }

    @Test
    void testCustomer1Orders() throws IOException {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(1);
        dto.setFromDate(LocalDate.of(2023, 1, 1));
        dto.setToDate(LocalDate.of(2023, 12, 31));

        List<Order> orders = List.of(
                createOrder("Bluetooth Headphones", 1, 2999.99, "2023-01-15"),
                createOrder("USB-C Charger", 2, 1499.5, "2023-03-22"),
                createOrder("Laptop Stand", 1, 1899.0, "2023-07-05")
        );

        runTestAndSavePdf(dto, orders, "customer1-orders.pdf");
    }

    @Test
    void testCustomer2Orders() throws IOException {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(2);
        dto.setFromDate(LocalDate.of(2023, 1, 1));
        dto.setToDate(LocalDate.of(2023, 12, 31));

        List<Order> orders = List.of(
                createOrder("Wireless Mouse", 1, 799.0, "2023-02-17"),
                createOrder("Mechanical Keyboard", 1, 4599.99, "2023-09-10")
        );

        runTestAndSavePdf(dto, orders, "customer2-orders.pdf");
    }

    @Test
    void testCustomer3Orders() throws IOException {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(3);
        dto.setFromDate(LocalDate.of(2023, 1, 1));
        dto.setToDate(LocalDate.of(2023, 12, 31));

        List<Order> orders = List.of(
                createOrder("Monitor 24 inch", 1, 8499.99, "2023-11-23")
        );

        runTestAndSavePdf(dto, orders, "customer3-orders.pdf");
    }

    @Test
    void testInvalidDateRangeThrowsException() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(1);
        dto.setFromDate(LocalDate.of(2023, 12, 31));
        dto.setToDate(LocalDate.of(2023, 1, 1));

        assertThrows(RuntimeException.class, () -> orderService.generateReport(dto));
    }

    @Test
    void testNullCustomerIdValidation() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(null);
        dto.setFromDate(LocalDate.of(2023, 1, 1));
        dto.setToDate(LocalDate.of(2023, 12, 31));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.generateReport(dto);
        });

        assertEquals("Customer ID cannot be null", exception.getMessage());
    }


    @Test
    void testValidInputButNoOrdersFound() throws IOException {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(99);
        dto.setFromDate(LocalDate.of(2023, 1, 1));
        dto.setToDate(LocalDate.of(2023, 12, 31));

        when(orderRepository.findByCustomerIdAndOrderDateBetween(any(), any(), any()))
                .thenReturn(List.of());

        var result = orderService.generateReport(dto);
        assertNotNull(result);

        try (FileOutputStream fos = new FileOutputStream("no-orders-found.pdf")) {
            fos.write(result.readAllBytes());
            System.out.println("✅ Edge case PDF saved: no-orders-found.pdf");
        }
    }

    @Test
    void testDatabaseThrowsException() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(1);
        dto.setFromDate(LocalDate.of(2023, 1, 1));
        dto.setToDate(LocalDate.of(2023, 12, 31));

        when(orderRepository.findByCustomerIdAndOrderDateBetween(any(), any(), any()))
                .thenThrow(new RuntimeException("Database connection error"));

        assertThrows(RuntimeException.class, () -> orderService.generateReport(dto));
    }
}