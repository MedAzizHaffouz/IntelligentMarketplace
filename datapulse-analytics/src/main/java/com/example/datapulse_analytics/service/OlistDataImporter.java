package com.example.datapulse_analytics.service;

import com.example.datapulse_analytics.model.Customer;
import com.example.datapulse_analytics.model.Order;
import com.example.datapulse_analytics.model.OrderItem;
import com.example.datapulse_analytics.model.OrderPayment;
import com.example.datapulse_analytics.model.OrderReview;
import com.example.datapulse_analytics.repository.CustomerRepository;
import com.example.datapulse_analytics.repository.OrderRepository;
import com.example.datapulse_analytics.repository.OrderItemRepository;
import com.example.datapulse_analytics.repository.OrderPaymentRepository;
import com.example.datapulse_analytics.repository.OrderReviewRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OlistDataImporter implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(OlistDataImporter.class);
    private static final Path DATA_DIRECTORY = Path.of("C:/Users/azizh/Downloads/archive");
    private static final DateTimeFormatter CSV_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final OrderReviewRepository orderReviewRepository;

    public OlistDataImporter(CustomerRepository customerRepository,
                             OrderRepository orderRepository,
                             OrderItemRepository orderItemRepository,
                             OrderPaymentRepository orderPaymentRepository,
                             OrderReviewRepository orderReviewRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderPaymentRepository = orderPaymentRepository;
        this.orderReviewRepository = orderReviewRepository;
    }

    @Override
    public void run(String... args) throws IOException {
        importCustomersIfNeeded();
        importOrdersIfNeeded();
        importOrderItemsIfNeeded();
        importOrderPaymentsIfNeeded();
        importOrderReviewsIfNeeded();
    }

    private void importCustomersIfNeeded() throws IOException {
        if (customerRepository.count() > 0) {
            log.info("Customer data already exists; skipping customer CSV import.");
            return;
        }

        Path csvPath = DATA_DIRECTORY.resolve("olist_customers_dataset.csv");
        List<Customer> customers;
        try (Reader reader = openCsv(csvPath);
             CSVParser parser = csvParser(reader)) {
            customers = parser.stream()
                    .map(this::toCustomer)
                    .toList();
        }

        customerRepository.saveAll(customers);
        log.info("Imported {} customers from {}.", customers.size(), csvPath);
    }

    private void importOrdersIfNeeded() throws IOException {
        if (orderRepository.count() > 0) {
            log.info("Order data already exists; skipping order CSV import.");
            return;
        }

        Path csvPath = DATA_DIRECTORY.resolve("olist_orders_dataset.csv");
        List<Order> orders;
        try (Reader reader = openCsv(csvPath);
             CSVParser parser = csvParser(reader)) {
            orders = parser.stream()
                    .map(this::toOrder)
                    .toList();
        }

        orderRepository.saveAll(orders);
        log.info("Imported {} orders from {}.", orders.size(), csvPath);
    }

    private void importOrderItemsIfNeeded() throws IOException {
        if (orderItemRepository.count() > 0) {
            log.info("Order-item data already exists; skipping order-item CSV import.");
            return;
        }

        Path csvPath = DATA_DIRECTORY.resolve("olist_order_items_dataset.csv");
        List<OrderItem> orderItems;
        try (Reader reader = openCsv(csvPath);
             CSVParser parser = csvParser(reader)) {
            orderItems = parser.stream()
                    .map(this::toOrderItem)
                    .toList();
        }

        orderItemRepository.saveAll(orderItems);
        log.info("Imported {} order items from {}.", orderItems.size(), csvPath);
    }

    private void importOrderPaymentsIfNeeded() throws IOException {
        if (orderPaymentRepository.count() > 0) {
            log.info("Order-payment data already exists; skipping payment CSV import.");
            return;
        }

        Path csvPath = DATA_DIRECTORY.resolve("olist_order_payments_dataset.csv");
        List<OrderPayment> payments;
        try (Reader reader = openCsv(csvPath);
             CSVParser parser = csvParser(reader)) {
            payments = parser.stream()
                    .map(this::toOrderPayment)
                    .toList();
        }

        orderPaymentRepository.saveAll(payments);
        log.info("Imported {} order payments from {}.", payments.size(), csvPath);
    }

    private void importOrderReviewsIfNeeded() throws IOException {
        if (orderReviewRepository.count() > 0) {
            log.info("Order-review data already exists; skipping review CSV import.");
            return;
        }

        Path csvPath = DATA_DIRECTORY.resolve("olist_order_reviews_dataset.csv");
        List<OrderReview> reviews;
        try (Reader reader = openCsv(csvPath);
             CSVParser parser = csvParser(reader)) {
            reviews = parser.stream()
                    .map(this::toOrderReview)
                    .toList();
        }

        orderReviewRepository.saveAll(reviews);
        log.info("Imported {} order reviews from {}.", reviews.size(), csvPath);
    }

    private Reader openCsv(Path csvPath) throws IOException {
        if (!Files.exists(csvPath)) {
            throw new IOException("CSV file not found: " + csvPath.toAbsolutePath());
        }
        return Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
    }

    private CSVParser csvParser(Reader reader) throws IOException {
        return CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .build()
                .parse(reader);
    }

    private Customer toCustomer(CSVRecord record) {
        Customer customer = new Customer();
        customer.setCustomerId(record.get("customer_id"));
        customer.setCustomerUniqueId(record.get("customer_unique_id"));
        customer.setCustomerZipCodePrefix(Integer.valueOf(record.get("customer_zip_code_prefix")));
        customer.setCustomerCity(record.get("customer_city"));
        customer.setCustomerState(record.get("customer_state"));
        return customer;
    }

    private Order toOrder(CSVRecord record) {
        Order order = new Order();
        order.setOrderId(record.get("order_id"));
        order.setCustomerId(record.get("customer_id"));
        order.setOrderStatus(record.get("order_status"));
        order.setOrderPurchaseTimestamp(parseDateTime(record.get("order_purchase_timestamp")));
        order.setOrderApprovedAt(parseDateTime(record.get("order_approved_at")));
        order.setOrderDeliveredCarrierDate(parseDateTime(record.get("order_delivered_carrier_date")));
        order.setOrderDeliveredCustomerDate(parseDateTime(record.get("order_delivered_customer_date")));
        order.setOrderEstimatedDeliveryDate(parseDateTime(record.get("order_estimated_delivery_date")));
        return order;
    }

    private OrderItem toOrderItem(CSVRecord record) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(record.get("order_id"));
        orderItem.setOrderItemId(Integer.valueOf(record.get("order_item_id")));
        orderItem.setProductId(record.get("product_id"));
        orderItem.setSellerId(record.get("seller_id"));
        orderItem.setShippingLimitDate(parseDateTime(record.get("shipping_limit_date")));
        orderItem.setPrice(new BigDecimal(record.get("price")));
        orderItem.setFreightValue(new BigDecimal(record.get("freight_value")));
        return orderItem;
    }

    private OrderPayment toOrderPayment(CSVRecord record) {
        OrderPayment payment = new OrderPayment();
        payment.setOrderId(record.get("order_id"));
        payment.setPaymentSequential(Integer.valueOf(record.get("payment_sequential")));
        payment.setPaymentType(record.get("payment_type"));
        payment.setPaymentInstallments(Integer.valueOf(record.get("payment_installments")));
        payment.setPaymentValue(new BigDecimal(record.get("payment_value")));
        return payment;
    }

    private OrderReview toOrderReview(CSVRecord record) {
        OrderReview review = new OrderReview();
        review.setReviewId(record.get("review_id"));
        review.setOrderId(record.get("order_id"));
        review.setReviewScore(Integer.valueOf(record.get("review_score")));
        review.setReviewCommentTitle(blankToNull(record.get("review_comment_title")));
        review.setReviewCommentMessage(blankToNull(record.get("review_comment_message")));
        review.setReviewCreationDate(parseDateTime(record.get("review_creation_date")));
        review.setReviewAnswerTimestamp(parseDateTime(record.get("review_answer_timestamp")));
        return review;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private LocalDateTime parseDateTime(String value) {
        return value == null || value.isBlank() ? null : LocalDateTime.parse(value, CSV_DATE_TIME);
    }
}
