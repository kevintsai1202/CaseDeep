package com.casemgr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.casemgr.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    
    /**
     * 根據位置名稱查詢位置
     * @param location 位置名稱
     * @return 位置實體
     */
    Location findByLocation(String location);
    
    /**
     * 根據位置名稱查詢位置（Optional）
     * @param location 位置名稱
     * @return 位置實體（Optional）
     */
    @Query("SELECT l FROM Location l WHERE l.location = :location")
    Optional<Location> findByLocationOptional(@Param("location") String location);
    
    /**
     * 檢查位置名稱是否存在
     * @param location 位置名稱
     * @return 是否存在
     */
    boolean existsByLocation(String location);
}