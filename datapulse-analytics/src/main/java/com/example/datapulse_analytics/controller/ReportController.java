package com.example.datapulse_analytics.controller;

import com.example.datapulse_analytics.repository.CustomerRepository;
import com.example.datapulse_analytics.repository.OrderPaymentRepository;
import com.example.datapulse_analytics.repository.OrderRepository;
import com.example.datapulse_analytics.repository.OrderReviewRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final OrderReviewRepository orderReviewRepository;

    public ReportController(CustomerRepository customerRepository,
                            OrderRepository orderRepository,
                            OrderPaymentRepository orderPaymentRepository,
                            OrderReviewRepository orderReviewRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderPaymentRepository = orderPaymentRepository;
        this.orderReviewRepository = orderReviewRepository;
    }

    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String state) {
        LocalDateTime start = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime end = endDate == null ? null : endDate.plusDays(1).atStartOfDay();
        long customers = customerRepository.countFiltered(start, end, state);
        long orders = orderRepository.countFiltered(start, end, state);
        BigDecimal revenue = orderPaymentRepository.totalRevenueFiltered(start, end, state);
        Double reviewScore = orderReviewRepository.averageReviewScoreFiltered(start, end, state);
        List<Object[]> states = customerRepository.countCustomersByStateFiltered(start, end, state);

        StringBuilder csv = new StringBuilder("DataPulse Analytics Report\nFilter,Value\n");
        csv.append(csvRow("Start date", startDate == null ? "All" : startDate));
        csv.append(csvRow("End date", endDate == null ? "All" : endDate));
        csv.append(csvRow("State", state == null || state.isBlank() ? "All" : state));
        csv.append("\nMetric,Value\n");
        csv.append(csvRow("Total customers", customers));
        csv.append(csvRow("Total orders", orders));
        csv.append(csvRow("Total revenue", revenue));
        csv.append(csvRow("Average review score", reviewScore));
        csv.append("\nState,Customers\n");
        for (Object[] row : states) csv.append(csvRow(row[0], row[1]));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=datapulse-dashboard-report.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String csvRow(Object first, Object second) {
        return csvValue(first) + "," + csvValue(second) + "\n";
    }

    private String csvValue(Object value) {
        String text = value == null ? "" : value.toString();
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
