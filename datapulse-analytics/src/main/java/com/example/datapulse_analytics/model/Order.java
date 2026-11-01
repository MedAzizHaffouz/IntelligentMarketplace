package com.example.datapulse_analytics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders", indexes = {
        @jakarta.persistence.Index(name = "idx_orders_customer_purchase", columnList = "customer_id, order_purchase_timestamp"),
        @jakarta.persistence.Index(name = "idx_orders_purchase", columnList = "order_purchase_timestamp")
})
public class Order {

    @Id
    @Column(name = "order_id", nullable = false, length = 32)
    private String orderId;

    @Column(name = "customer_id", nullable = false, length = 32)
    private String customerId;

    @Column(name = "order_status", nullable = false, length = 20)
    private String orderStatus;

    @Column(name = "order_purchase_timestamp")
    private LocalDateTime orderPurchaseTimestamp;

    @Column(name = "order_approved_at")
    private LocalDateTime orderApprovedAt;

    @Column(name = "order_delivered_carrier_date")
    private LocalDateTime orderDeliveredCarrierDate;

    @Column(name = "order_delivered_customer_date")
    private LocalDateTime orderDeliveredCustomerDate;

    @Column(name = "order_estimated_delivery_date")
    private LocalDateTime orderEstimatedDeliveryDate;

    public Order() {
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public LocalDateTime getOrderPurchaseTimestamp() { return orderPurchaseTimestamp; }
    public void setOrderPurchaseTimestamp(LocalDateTime value) { this.orderPurchaseTimestamp = value; }
    public LocalDateTime getOrderApprovedAt() { return orderApprovedAt; }
    public void setOrderApprovedAt(LocalDateTime value) { this.orderApprovedAt = value; }
    public LocalDateTime getOrderDeliveredCarrierDate() { return orderDeliveredCarrierDate; }
    public void setOrderDeliveredCarrierDate(LocalDateTime value) { this.orderDeliveredCarrierDate = value; }
    public LocalDateTime getOrderDeliveredCustomerDate() { return orderDeliveredCustomerDate; }
    public void setOrderDeliveredCustomerDate(LocalDateTime value) { this.orderDeliveredCustomerDate = value; }
    public LocalDateTime getOrderEstimatedDeliveryDate() { return orderEstimatedDeliveryDate; }
    public void setOrderEstimatedDeliveryDate(LocalDateTime value) { this.orderEstimatedDeliveryDate = value; }
}
