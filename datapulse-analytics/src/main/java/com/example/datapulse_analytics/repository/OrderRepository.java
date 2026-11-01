package com.example.datapulse_analytics.repository;

import com.example.datapulse_analytics.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT COUNT(o) FROM Order o JOIN Customer c ON c.customerId = o.customerId "
            + "WHERE (:startDate IS NULL OR o.orderPurchaseTimestamp >= :startDate) "
            + "AND (:endDate IS NULL OR o.orderPurchaseTimestamp < :endDate) "
            + "AND (:state IS NULL OR c.customerState = :state)")
    long countFiltered(@Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate,
                       @Param("state") String state);

    /**
     * Calculates all dashboard KPIs in one database round trip. The database
     * performs the counts and aggregates, so no entity rows are materialized
     * in the application heap.
     */
    @Query(value = "SELECT "
            + "(SELECT COUNT(DISTINCT o.customer_id) FROM orders o "
            + "JOIN customers c ON c.customer_id = o.customer_id "
            + "WHERE (:startDate IS NULL OR o.order_purchase_timestamp >= :startDate) "
            + "AND (:endDate IS NULL OR o.order_purchase_timestamp < :endDate) "
            + "AND (:state IS NULL OR c.customer_state = :state)) AS total_customers, "
            + "(SELECT COUNT(*) FROM orders o "
            + "JOIN customers c ON c.customer_id = o.customer_id "
            + "WHERE (:startDate IS NULL OR o.order_purchase_timestamp >= :startDate) "
            + "AND (:endDate IS NULL OR o.order_purchase_timestamp < :endDate) "
            + "AND (:state IS NULL OR c.customer_state = :state)) AS total_orders, "
            + "(SELECT COALESCE(SUM(p.payment_value), 0) FROM order_payments p "
            + "JOIN orders o ON o.order_id = p.order_id "
            + "JOIN customers c ON c.customer_id = o.customer_id "
            + "WHERE (:startDate IS NULL OR o.order_purchase_timestamp >= :startDate) "
            + "AND (:endDate IS NULL OR o.order_purchase_timestamp < :endDate) "
            + "AND (:state IS NULL OR c.customer_state = :state)) AS total_revenue, "
            + "(SELECT COALESCE(AVG(r.review_score), 0) FROM order_reviews r "
            + "JOIN orders o ON o.order_id = r.order_id "
            + "JOIN customers c ON c.customer_id = o.customer_id "
            + "WHERE (:startDate IS NULL OR o.order_purchase_timestamp >= :startDate) "
            + "AND (:endDate IS NULL OR o.order_purchase_timestamp < :endDate) "
            + "AND (:state IS NULL OR c.customer_state = :state)) AS average_review_score",
            nativeQuery = true)
    Object[] dashboardSummary(@Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate,
                              @Param("state") String state);
}
