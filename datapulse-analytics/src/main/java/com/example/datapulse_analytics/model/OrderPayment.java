package com.example.datapulse_analytics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "order_payments", indexes = {
        @jakarta.persistence.Index(name = "idx_payments_order", columnList = "order_id"),
        @jakarta.persistence.Index(name = "idx_payments_type", columnList = "payment_type")
})
@IdClass(OrderPaymentId.class)
public class OrderPayment {

    @Id
    @Column(name = "order_id", nullable = false, length = 32)
    private String orderId;

    @Id
    @Column(name = "payment_sequential", nullable = false)
    private Integer paymentSequential;

    @Column(name = "payment_type", nullable = false, length = 30)
    private String paymentType;

    @Column(name = "payment_installments")
    private Integer paymentInstallments;

    @Column(name = "payment_value", precision = 12, scale = 2)
    private BigDecimal paymentValue;

    public OrderPayment() {
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Integer getPaymentSequential() { return paymentSequential; }
    public void setPaymentSequential(Integer paymentSequential) { this.paymentSequential = paymentSequential; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public Integer getPaymentInstallments() { return paymentInstallments; }
    public void setPaymentInstallments(Integer paymentInstallments) { this.paymentInstallments = paymentInstallments; }
    public BigDecimal getPaymentValue() { return paymentValue; }
    public void setPaymentValue(BigDecimal paymentValue) { this.paymentValue = paymentValue; }
}
