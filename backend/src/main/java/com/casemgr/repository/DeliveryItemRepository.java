package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.casemgr.entity.DeliveryItem;

@Repository
public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {
    // Add custom query methods here if needed later
    // Example: List<DeliveryItem> findByOrderId(Long orderId);
}