package com.example.datapulse_analytics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@IdClass(OrderItemId.class)
public class OrderItem {

    @Id
    @Column(name = "order_id", nullable = false, length = 32)
    private String orderId;

    @Id
    @Column(name = "order_item_id", nullable = false)
    private Integer orderItemId;

    @Column(name = "product_id", nullable = false, length = 32)
    private String productId;

    @Column(name = "seller_id", nullable = false, length = 32)
    private String sellerId;

    @Column(name = "shipping_limit_date")
    private LocalDateTime shippingLimitDate;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "freight_value", precision = 10, scale = 2)
    private BigDecimal freightValue;

    public OrderItem() {
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Integer getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Integer orderItemId) { this.orderItemId = orderItemId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    public LocalDateTime getShippingLimitDate() { return shippingLimitDate; }
    public void setShippingLimitDate(LocalDateTime shippingLimitDate) { this.shippingLimitDate = shippingLimitDate; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getFreightValue() { return freightValue; }
    public void setFreightValue(BigDecimal freightValue) { this.freightValue = freightValue; }
}
