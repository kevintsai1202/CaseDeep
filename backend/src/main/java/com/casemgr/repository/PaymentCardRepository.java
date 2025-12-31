package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.casemgr.entity.PaymentCard;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {
    // Add custom query methods here if needed later
    // Example: List<PaymentCard> findByOrderId(Long orderId);
}