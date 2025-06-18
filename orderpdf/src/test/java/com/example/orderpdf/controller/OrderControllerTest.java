package com.example.orderpdf.controller;

import com.example.orderpdf.dto.OrderRequestDto;
import com.example.orderpdf.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void testGeneratePdf_Success() throws Exception {
        Mockito.when(orderService.generateReport(any(OrderRequestDto.class)))
                .thenReturn(new ByteArrayInputStream("Mock PDF".getBytes()));

        mockMvc.perform(post("/api/orders/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "customerId": "1",
                          "fromDate": "2023-01-01",
                          "toDate": "2023-12-31"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"));
    }

    @Test
    void testGeneratePdf_BadRequest() throws Exception {
        mockMvc.perform(post("/api/orders/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "fromDate": "2023-01-01",
                          "toDate": "2023-12-31"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }
}
