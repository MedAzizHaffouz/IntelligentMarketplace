package com.example.datapulse_analytics.controller;

import com.example.datapulse_analytics.repository.CustomerRepository;
import com.example.datapulse_analytics.repository.OrderPaymentRepository;
import com.example.datapulse_analytics.repository.OrderRepository;
import com.example.datapulse_analytics.repository.OrderReviewRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final OrderReviewRepository orderReviewRepository;

    public DashboardController(CustomerRepository customerRepository,
                               OrderRepository orderRepository,
                               OrderPaymentRepository orderPaymentRepository,
                               OrderReviewRepository orderReviewRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderPaymentRepository = orderPaymentRepository;
        this.orderReviewRepository = orderReviewRepository;
    }

    @GetMapping("/summary")
    public Map<String, Object> summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String state) {
        LocalDateTime start = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime end = endDate == null ? null : endDate.plusDays(1).atStartOfDay();

        long totalCustomers = customerRepository.countFiltered(start, end, state);
        long totalOrders = orderRepository.countFiltered(start, end, state);
        Object totalRevenue = orderPaymentRepository.totalRevenueFiltered(start, end, state);
        Double averageReviewScore = orderReviewRepository.averageReviewScoreFiltered(start, end, state);
        return Map.of(
                "totalCustomers", totalCustomers,
                "totalOrders", totalOrders,
                "totalRevenue", totalRevenue,
                "averageReviewScore", averageReviewScore);
    }

    @GetMapping("/revenue/monthly")
    public List<Map<String, Object>> monthlyRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String state) {
        return orderPaymentRepository.monthlyRevenue(toStart(startDate), toEnd(endDate), state).stream()
                .map(row -> Map.of(
                        "month", String.format("%04d-%02d", ((Number) row[0]).intValue(), ((Number) row[1]).intValue()),
                        "revenue", row[2]))
                .collect(Collectors.toList());
    }

    @GetMapping("/payments/by-method")
    public List<Map<String, Object>> paymentMethodDistribution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String state) {
        return orderPaymentRepository.paymentMethodDistribution(toStart(startDate), toEnd(endDate), state).stream()
                .map(row -> Map.of("paymentType", row[0], "total", row[1]))
                .collect(Collectors.toList());
    }

    private LocalDateTime toStart(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private LocalDateTime toEnd(LocalDate date) {
        return date == null ? null : date.plusDays(1).atStartOfDay();
    }
}
