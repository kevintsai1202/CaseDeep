package com.casemgr.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.casemgr.entity.Order;
import com.casemgr.enumtype.RevenueShareStatus;
import com.casemgr.response.RevenueShareResponse;
import com.casemgr.response.RevenueShareStatsResponse;

import jakarta.persistence.EntityNotFoundException;

public interface RevenueShareService {
    
    // 建立Revenue Share記錄（內部使用）
    RevenueShareResponse createRevenueShare(Order order);
    
    // 基本CRUD操作
    RevenueShareResponse getById(Long id) throws EntityNotFoundException;
    
    RevenueShareResponse getByRevenueShareNo(String revenueShareNo) throws EntityNotFoundException;
    
    List<RevenueShareResponse> listByClientId(Long clientId);
    
    List<RevenueShareResponse> listByProviderId(Long providerId);
    
    List<RevenueShareResponse> listByOrderId(Long orderId);
    
    Page<RevenueShareResponse> listAll(Pageable pageable);
    
    Page<RevenueShareResponse> listByClientId(Long clientId, Pageable pageable);
    
    Page<RevenueShareResponse> listByProviderId(Long providerId, Pageable pageable);
    
    // 狀態更新
    RevenueShareResponse updateStatus(Long id, RevenueShareStatus status, LocalDateTime paymentTime) throws EntityNotFoundException;
    
    // 統計功能
    BigDecimal getTotalUnpaidAmount(Long clientId);
    
    BigDecimal getTotalPaidAmount(Long clientId);
    
    BigDecimal getTotalUnpaidAmountByProvider(Long providerId);
    
    BigDecimal getTotalPaidAmountByProvider(Long providerId);
    
    RevenueShareStatsResponse getStatsByClient(Long clientId);
    
    RevenueShareStatsResponse getStatsByProvider(Long providerId);
    
    RevenueShareStatsResponse getOverallStats();
    
    // 內部輔助方法
    Float calculateRevenueShareRate(Order order);
}