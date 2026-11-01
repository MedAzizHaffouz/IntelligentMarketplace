package com.example.datapulse_analytics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_reviews", indexes = {
        @jakarta.persistence.Index(name = "idx_reviews_order", columnList = "order_id")
})
@IdClass(OrderReviewId.class)
public class OrderReview {

    @Id
    @Column(name = "review_id", nullable = false, length = 32)
    private String reviewId;

    @Id
    @Column(name = "order_id", nullable = false, length = 32)
    private String orderId;

    @Column(name = "review_score", nullable = false)
    private Integer reviewScore;

    @Column(name = "review_comment_title", length = 255)
    private String reviewCommentTitle;

    @Column(name = "review_comment_message", length = 2000)
    private String reviewCommentMessage;

    @Column(name = "review_creation_date")
    private LocalDateTime reviewCreationDate;

    @Column(name = "review_answer_timestamp")
    private LocalDateTime reviewAnswerTimestamp;

    public OrderReview() {
    }

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public Integer getReviewScore() { return reviewScore; }
    public void setReviewScore(Integer reviewScore) { this.reviewScore = reviewScore; }
    public String getReviewCommentTitle() { return reviewCommentTitle; }
    public void setReviewCommentTitle(String reviewCommentTitle) { this.reviewCommentTitle = reviewCommentTitle; }
    public String getReviewCommentMessage() { return reviewCommentMessage; }
    public void setReviewCommentMessage(String reviewCommentMessage) { this.reviewCommentMessage = reviewCommentMessage; }
    public LocalDateTime getReviewCreationDate() { return reviewCreationDate; }
    public void setReviewCreationDate(LocalDateTime reviewCreationDate) { this.reviewCreationDate = reviewCreationDate; }
    public LocalDateTime getReviewAnswerTimestamp() { return reviewAnswerTimestamp; }
    public void setReviewAnswerTimestamp(LocalDateTime reviewAnswerTimestamp) { this.reviewAnswerTimestamp = reviewAnswerTimestamp; }
}
