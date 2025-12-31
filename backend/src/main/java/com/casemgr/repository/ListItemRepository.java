package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.ListItem;

public interface ListItemRepository extends JpaRepository<ListItem, Long> {

}
