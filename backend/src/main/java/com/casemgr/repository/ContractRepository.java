package com.casemgr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.Industry;
import com.casemgr.entity.Contract;
import com.casemgr.entity.SysListItem;

public interface ContractRepository extends JpaRepository<Contract, Long> {
//	List<Contract> findByTypeAndCategory(String type, Industry category);
}
