package com.example.datapulse_analytics.model;

import java.io.Serializable;
import java.util.Objects;

public class OrderReviewId implements Serializable {

    private String reviewId;
    private String orderId;

    public OrderReviewId() {
    }

    public OrderReviewId(String reviewId, String orderId) {
        this.reviewId = reviewId;
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderReviewId that)) return false;
        return Objects.equals(reviewId, that.reviewId)
                && Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, orderId);
    }
}
