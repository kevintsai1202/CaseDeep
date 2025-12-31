package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.Block;
import com.casemgr.entity.FeeCode;

public interface FeeCodeRepository extends JpaRepository<FeeCode, Long> {
	public FeeCode findByFeeCode(String feeCode);
}
