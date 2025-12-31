package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.casemgr.entity.Upgrade;

@Repository
public interface UpgradeRepository extends JpaRepository<Upgrade, Long> {
    // You can add custom query methods here if needed, e.g.:
    // List<Upgrade> findByUser(User user);
    // Optional<Upgrade> findByUpgradeIdStr(String upgradeIdStr);
    Page<Upgrade> findByUser_Region(String region, Pageable pageable);
}