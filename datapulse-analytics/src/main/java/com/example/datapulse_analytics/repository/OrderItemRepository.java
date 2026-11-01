package com.example.datapulse_analytics.repository;

import com.example.datapulse_analytics.model.OrderItem;
import com.example.datapulse_analytics.model.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
}
