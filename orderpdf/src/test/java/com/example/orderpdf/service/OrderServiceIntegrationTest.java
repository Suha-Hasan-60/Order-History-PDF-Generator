package com.example.orderpdf.service;

import com.example.orderpdf.dto.OrderRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    private void savePdf(String filename, byte[] content) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(content);
            System.out.println("✅ PDF saved: " + filename);
        }
    }

    @Test
    void testGeneratePdfForValidCustomer() throws Exception {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(1); // Ensure customer 1 has orders in DB
        dto.setFromDate(LocalDate.of(2023, 1, 1));
        dto.setToDate(LocalDate.of(2023, 12, 31));

        var result = orderService.generateReport(dto);
        assertNotNull(result);
        savePdf("real-db-customer1.pdf", result.readAllBytes());
    }

    @Test
    void testNoOrdersFound() throws Exception {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(999); // Use a customer ID that exists but has no orders
        dto.setFromDate(LocalDate.of(2023, 1, 1));
        dto.setToDate(LocalDate.of(2023, 12, 31));

        var result = orderService.generateReport(dto);
        assertNotNull(result);
        savePdf("no-orders-real-db.pdf", result.readAllBytes());
    }

    @Test
    void testInvalidDateRange() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(1);
        dto.setFromDate(LocalDate.of(2023, 12, 31));
        dto.setToDate(LocalDate.of(2023, 1, 1)); // Invalid

        Exception ex = assertThrows(RuntimeException.class, () -> {
            orderService.generateReport(dto);
        });

        System.out.println("✅ Exception caught: " + ex.getMessage());
    }

    @Test
    void testNullCustomerId() {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(null); // Should fail validation
        dto.setFromDate(LocalDate.of(2023, 1, 1));
        dto.setToDate(LocalDate.of(2023, 12, 31));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            orderService.generateReport(dto);
        });

        assertEquals("Customer ID cannot be null", ex.getMessage());
    }

    @Test
    void testFutureDateRange() throws Exception {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setCustomerId(1);
        dto.setFromDate(LocalDate.of(2025, 1, 1));
        dto.setToDate(LocalDate.of(2025, 12, 31));

        var result = orderService.generateReport(dto);
        assertNotNull(result);
        savePdf("future-dates-real-db.pdf", result.readAllBytes());
    }
}
