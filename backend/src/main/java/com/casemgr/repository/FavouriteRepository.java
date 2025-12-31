package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.Favourite;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

}
