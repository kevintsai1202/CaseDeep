package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.PersonProfile;

public interface PersonProfileRepository extends JpaRepository<PersonProfile, Long> {
}
