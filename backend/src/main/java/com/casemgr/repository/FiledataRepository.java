package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.casemgr.entity.Filedata;

public interface FiledataRepository extends JpaRepository<Filedata, Long> {
	Filedata findByStorageUuid(String uuid);
	
	@Modifying
	void deleteByStorageUuid(String uuid);
}
