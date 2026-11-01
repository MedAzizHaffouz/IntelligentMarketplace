package com.example.datapulse_analytics.model;

import java.io.Serializable;
import java.util.Objects;

public class OrderPaymentId implements Serializable {

    private String orderId;
    private Integer paymentSequential;

    public OrderPaymentId() {
    }

    public OrderPaymentId(String orderId, Integer paymentSequential) {
        this.orderId = orderId;
        this.paymentSequential = paymentSequential;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderPaymentId that)) return false;
        return Objects.equals(orderId, that.orderId)
                && Objects.equals(paymentSequential, that.paymentSequential);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, paymentSequential);
    }
}
