package com.casemgr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.PersonProfile;
import com.casemgr.entity.PricePackage;
import com.casemgr.entity.User;

public interface PricePackageRepository extends JpaRepository<PricePackage, Long> {
	List<PricePackage> findByProvider(User user);
}
