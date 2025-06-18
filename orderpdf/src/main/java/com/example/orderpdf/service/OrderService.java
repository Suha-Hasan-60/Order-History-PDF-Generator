package com.example.orderpdf.service;

import com.example.orderpdf.dto.OrderRequestDto;
import com.example.orderpdf.entity.Order;
import com.example.orderpdf.exception.InvalidDateRangeException;
import com.example.orderpdf.repository.OrderRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public ByteArrayInputStream generateReport(OrderRequestDto dto) {
        if (dto.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        log.info("Received request to generate PDF for customer {}", dto.getCustomerId());
        validateDateRange(dto.getFromDate(), dto.getToDate());

        List<Order> orders = orderRepository.findByCustomerIdAndOrderDateBetween(
                dto.getCustomerId(), dto.getFromDate(), dto.getToDate());

        log.info("Fetched {} orders from DB", orders.size());
        log.info("Starting PDF generation...");
        ByteArrayInputStream report = createPdf(orders);
        log.info("PDF generation completed.");

        return report;
    }


    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new InvalidDateRangeException("FromDate must not be after ToDate.");
        }
    }
    private Paragraph formatOrderLine(Order o) {
        String text = String.format("Product: %s | Qty: %d | Price: %.2f | Date: %s",
                o.getProductName(), o.getQuantity(), o.getPrice(), o.getOrderDate());
        return new Paragraph(text);
    }

    private ByteArrayInputStream createPdf(List<Order> orders) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Order History Report"));
            document.add(new Paragraph(" "));

            orders.stream()
                    .map(this::formatOrderLine)
                    .forEach(p -> {
                        try {
                            document.add(p);
                        } catch (DocumentException e) {
                            log.error("Failed to add paragraph", e);
                        }
                    });


            document.close();
        } catch (DocumentException e) {
            log.error("PDF generation failed", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
