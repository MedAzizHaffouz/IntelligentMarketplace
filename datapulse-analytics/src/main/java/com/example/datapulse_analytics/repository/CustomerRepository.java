package com.example.datapulse_analytics.repository;

import com.example.datapulse_analytics.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query("SELECT c.customerState, COUNT(c) FROM Customer c GROUP BY c.customerState")
    List<Object[]> countCustomersByState();

    @Query("SELECT c.customerState, COUNT(DISTINCT c.customerId) "
            + "FROM Customer c JOIN Order o ON o.customerId = c.customerId "
            + "WHERE (:startDate IS NULL OR o.orderPurchaseTimestamp >= :startDate) "
            + "AND (:endDate IS NULL OR o.orderPurchaseTimestamp < :endDate) "
            + "AND (:state IS NULL OR c.customerState = :state) "
            + "GROUP BY c.customerState")
    List<Object[]> countCustomersByStateFiltered(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate,
                                                  @Param("state") String state);

    @Query("SELECT COUNT(DISTINCT c.customerId) FROM Customer c "
            + "JOIN Order o ON o.customerId = c.customerId "
            + "WHERE (:startDate IS NULL OR o.orderPurchaseTimestamp >= :startDate) "
            + "AND (:endDate IS NULL OR o.orderPurchaseTimestamp < :endDate) "
            + "AND (:state IS NULL OR c.customerState = :state)")
    long countFiltered(@Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate,
                       @Param("state") String state);
}
