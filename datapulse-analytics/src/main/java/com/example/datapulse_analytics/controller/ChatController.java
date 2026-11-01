package com.example.datapulse_analytics.controller;

import com.example.datapulse_analytics.repository.CustomerRepository;
import com.example.datapulse_analytics.repository.OrderPaymentRepository;
import com.example.datapulse_analytics.repository.OrderRepository;
import com.example.datapulse_analytics.repository.OrderReviewRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final OrderReviewRepository orderReviewRepository;

    public ChatController(CustomerRepository customerRepository,
                          OrderRepository orderRepository,
                          OrderPaymentRepository orderPaymentRepository,
                          OrderReviewRepository orderReviewRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderPaymentRepository = orderPaymentRepository;
        this.orderReviewRepository = orderReviewRepository;
    }

    @PostMapping
    public Map<String, Object> chat(@RequestBody ChatRequest request) {
        String prompt = request.message() == null ? "" : request.message().trim();
        String normalizedPrompt = prompt.toLowerCase(Locale.ROOT);

        long customers = customerRepository.count();
        long orders = orderRepository.count();
        BigDecimal revenue = orderPaymentRepository.totalRevenue();
        Double reviewScore = orderReviewRepository.averageReviewScore();
        List<Object[]> stateRows = customerRepository.countCustomersByState();

        String insight;
        if (normalizedPrompt.contains("state") || normalizedPrompt.contains("region")
                || normalizedPrompt.contains("geograph")) {
            insight = "The strongest customer concentration is in " + topStates(stateRows)
                    + ". Consider prioritizing campaigns and delivery capacity in these markets.";
        } else if (normalizedPrompt.contains("revenue") || normalizedPrompt.contains("sales")
                || normalizedPrompt.contains("money")) {
            insight = "The dataset contains " + formatCurrency(revenue)
                    + " in payment value across " + formatNumber(orders)
                    + " orders. Use the monthly trend chart to identify growth and seasonality.";
        } else if (normalizedPrompt.contains("review") || normalizedPrompt.contains("rating")
                || normalizedPrompt.contains("score")) {
            insight = "The average review score is " + String.format(Locale.ROOT, "%.2f", reviewScore)
                    + " out of 5. This is a useful baseline for comparing states and time periods.";
        } else if (normalizedPrompt.contains("customer")) {
            insight = "There are " + formatNumber(customers)
                    + " customers represented in the Olist dataset, associated with "
                    + formatNumber(orders) + " orders.";
        } else {
            insight = "Here is the current snapshot: " + formatNumber(customers) + " customers, "
                    + formatNumber(orders) + " orders, " + formatCurrency(revenue)
                    + " in revenue, and an average review score of "
                    + String.format(Locale.ROOT, "%.2f", reviewScore) + "/5."
                    + " Ask me about revenue, customers, reviews, or states.";
        }

        return Map.of(
                "reply", insight,
                "prompt", prompt,
                "metrics", Map.of(
                        "customers", customers,
                        "orders", orders,
                        "revenue", revenue,
                        "averageReviewScore", reviewScore));
    }

    private String topStates(List<Object[]> rows) {
        return rows.stream()
                .sorted(Comparator.comparingLong(row -> -((Number) row[1]).longValue()))
                .limit(3)
                .map(row -> row[0] + " (" + formatNumber(((Number) row[1]).longValue()) + ")")
                .collect(Collectors.joining(", "));
    }

    private String formatNumber(long value) {
        return NumberFormat.getIntegerInstance(Locale.US).format(value);
    }

    private String formatCurrency(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(value);
    }

    public record ChatRequest(String message) {
    }
}
