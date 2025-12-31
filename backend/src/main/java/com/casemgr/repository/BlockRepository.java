package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.Block;

public interface BlockRepository extends JpaRepository<Block, Long> {

}
