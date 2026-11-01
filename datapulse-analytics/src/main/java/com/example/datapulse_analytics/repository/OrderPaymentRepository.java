package com.example.datapulse_analytics.repository;

import com.example.datapulse_analytics.model.OrderPayment;
import com.example.datapulse_analytics.model.OrderPaymentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment, OrderPaymentId> {

    @Query("SELECT COALESCE(SUM(p.paymentValue), 0) FROM OrderPayment p")
    BigDecimal totalRevenue();

    @Query("SELECT COALESCE(SUM(p.paymentValue), 0) FROM OrderPayment p "
            + "JOIN Order o ON o.orderId = p.orderId "
            + "JOIN Customer c ON c.customerId = o.customerId "
            + "WHERE (:startDate IS NULL OR o.orderPurchaseTimestamp >= :startDate) "
            + "AND (:endDate IS NULL OR o.orderPurchaseTimestamp < :endDate) "
            + "AND (:state IS NULL OR c.customerState = :state)")
    BigDecimal totalRevenueFiltered(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    @Param("state") String state);

    @Query(value = "SELECT YEAR(o.order_purchase_timestamp) AS year_value, "
            + "MONTH(o.order_purchase_timestamp) AS month_value, "
            + "COALESCE(SUM(p.payment_value), 0) AS revenue "
            + "FROM order_payments p "
            + "JOIN orders o ON o.order_id = p.order_id "
            + "JOIN customers c ON c.customer_id = o.customer_id "
            + "WHERE (:startDate IS NULL OR o.order_purchase_timestamp >= :startDate) "
            + "AND (:endDate IS NULL OR o.order_purchase_timestamp < :endDate) "
            + "AND (:state IS NULL OR c.customer_state = :state) "
            + "GROUP BY YEAR(o.order_purchase_timestamp), MONTH(o.order_purchase_timestamp) "
            + "ORDER BY year_value, month_value", nativeQuery = true)
    List<Object[]> monthlyRevenue(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate,
                                  @Param("state") String state);

    @Query(value = "SELECT p.payment_type AS payment_type, COALESCE(SUM(p.payment_value), 0) AS total "
            + "FROM order_payments p "
            + "JOIN orders o ON o.order_id = p.order_id "
            + "JOIN customers c ON c.customer_id = o.customer_id "
            + "WHERE (:startDate IS NULL OR o.order_purchase_timestamp >= :startDate) "
            + "AND (:endDate IS NULL OR o.order_purchase_timestamp < :endDate) "
            + "AND (:state IS NULL OR c.customer_state = :state) "
            + "GROUP BY p.payment_type ORDER BY total DESC", nativeQuery = true)
    List<Object[]> paymentMethodDistribution(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             @Param("state") String state);
}
