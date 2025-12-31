package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.Settlement;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

}
