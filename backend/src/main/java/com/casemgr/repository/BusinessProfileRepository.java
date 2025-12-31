package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.BusinessProfile;

public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, Long> {
}
