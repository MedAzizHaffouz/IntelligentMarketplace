package com.example.datapulse_analytics.repository;

import com.example.datapulse_analytics.model.OrderReview;
import com.example.datapulse_analytics.model.OrderReviewId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderReviewRepository extends JpaRepository<OrderReview, OrderReviewId> {

    @Query("SELECT COALESCE(AVG(r.reviewScore), 0) FROM OrderReview r")
    Double averageReviewScore();

    @Query("SELECT COALESCE(AVG(r.reviewScore), 0) FROM OrderReview r "
            + "JOIN Order o ON o.orderId = r.orderId "
            + "JOIN Customer c ON c.customerId = o.customerId "
            + "WHERE (:startDate IS NULL OR o.orderPurchaseTimestamp >= :startDate) "
            + "AND (:endDate IS NULL OR o.orderPurchaseTimestamp < :endDate) "
            + "AND (:state IS NULL OR c.customerState = :state)")
    Double averageReviewScoreFiltered(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      @Param("state") String state);
}
