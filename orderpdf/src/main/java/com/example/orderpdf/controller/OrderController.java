package com.example.orderpdf.controller;

import com.example.orderpdf.dto.OrderRequestDto;
import com.example.orderpdf.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/report")
    public ResponseEntity<InputStreamResource> generateReport(@Valid @RequestBody OrderRequestDto dto) {
        log.info("Request received for PDF generation");

        try {
            ByteArrayInputStream stream = orderService.generateReport(dto);
            InputStreamResource pdf = new InputStreamResource(stream);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            log.error("Failed to generate PDF: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InputStreamResource(new ByteArrayInputStream("Internal server error occurred.".getBytes())));
        }
    }

}
