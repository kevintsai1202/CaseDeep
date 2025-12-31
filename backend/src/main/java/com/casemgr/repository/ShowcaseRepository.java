package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.Showcase;

public interface ShowcaseRepository extends JpaRepository<Showcase, Long> {

}
