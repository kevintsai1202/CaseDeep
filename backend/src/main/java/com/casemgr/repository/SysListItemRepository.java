package com.casemgr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.SysListItem;

public interface SysListItemRepository extends JpaRepository<SysListItem, Long> {
	List<SysListItem> findByType(String type);	
}
