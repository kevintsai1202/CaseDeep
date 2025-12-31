package com.casemgr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.casemgr.entity.Commission;
import com.casemgr.entity.Order;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {
    // You can add custom query methods here if needed, e.g.:
    List<Commission> findByOrder(Order order);
    Optional<Commission> findByCommissionIdStr(String commissionIdStr);
    // List<Commission> findByOrder_Provider(User provider); // If needed to find by provider via Order
    // List<Commission> findByOrder_Client(User client); // If needed to find by client via Order
    Page<Commission> findByOrder_Provider_RegionOrOrder_Client_Region(String providerRegion, String clientRegion, Pageable pageable);
}