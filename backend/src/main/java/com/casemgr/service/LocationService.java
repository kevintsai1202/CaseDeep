package com.casemgr.service;

import java.util.List;

import com.casemgr.request.LocationRequest;
import com.casemgr.response.LocationResponse;

import jakarta.persistence.EntityNotFoundException;

public interface LocationService {
    
    /**
     * 創建 Location
     */
    LocationResponse create(LocationRequest locationRequest) throws EntityNotFoundException;
    
    /**
     * 更新 Location
     */
    LocationResponse update(Long id, LocationRequest locationRequest) throws EntityNotFoundException;
    
    /**
     * 取得 Location 詳細資訊
     */
    LocationResponse detail(Long id) throws EntityNotFoundException;
    
    /**
     * 列出所有 Location
     */
    List<LocationResponse> list();
    
    /**
     * 刪除 Location
     */
    void delete(Long id) throws EntityNotFoundException;
    
    /**
     * 根據位置名稱查詢 Location
     */
    LocationResponse findByLocation(String location) throws EntityNotFoundException;
}