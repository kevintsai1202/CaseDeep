package com.casemgr.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.casemgr.entity.RevenueShare;
import com.casemgr.enumtype.RevenueShareStatus;

@Repository
public interface RevenueShareRepository extends JpaRepository<RevenueShare, Long> {
    
    // 根據客戶查詢
    List<RevenueShare> findByClient_uIdOrderByCreateTimeDesc(Long clientId);
    
    // 根據提供者查詢
    List<RevenueShare> findByProvider_uIdOrderByCreateTimeDesc(Long providerId);
    
    // 根據訂單查詢
    Optional<RevenueShare> findByOrder_oId(Long orderId);
    
    // 根據Revenue Share編號查詢
    Optional<RevenueShare> findByRevenueShareNo(String revenueShareNo);
    
    // 根據狀態查詢
    List<RevenueShare> findByStatus(RevenueShareStatus status);
    
    // 分頁查詢所有記錄
    Page<RevenueShare> findAllByOrderByCreateTimeDesc(Pageable pageable);
    
    // 根據客戶分頁查詢
    Page<RevenueShare> findByClient_uIdOrderByCreateTimeDesc(Long clientId, Pageable pageable);
    
    // 根據提供者分頁查詢
    Page<RevenueShare> findByProvider_uIdOrderByCreateTimeDesc(Long providerId, Pageable pageable);
    
    // 計算客戶在特定行業的歷史訂單數量（不包含當前訂單）
    @Query("SELECT COUNT(rs) FROM RevenueShare rs WHERE rs.client.uId = :clientId AND rs.industry.id = :industryId AND rs.order.id <> :currentOrderId")
    int countByClientIdAndIndustryIdExcludingOrder(@Param("clientId") Long clientId, @Param("industryId") Long industryId, @Param("currentOrderId") Long currentOrderId);
    
    // 計算客戶在特定行業的歷史訂單數量
    @Query("SELECT COUNT(rs) FROM RevenueShare rs WHERE rs.client.uId = :clientId AND rs.industry.id = :industryId")
    int countByClientIdAndIndustryId(@Param("clientId") Long clientId, @Param("industryId") Long industryId);
    
    // 計算未付款總金額
    @Query("SELECT COALESCE(SUM(rs.revenueShareAmount), 0) FROM RevenueShare rs WHERE rs.client.uId = :clientId AND rs.status = 'Unpaid'")
    BigDecimal sumUnpaidAmountByClientId(@Param("clientId") Long clientId);
    
    // 計算已付款總金額
    @Query("SELECT COALESCE(SUM(rs.revenueShareAmount), 0) FROM RevenueShare rs WHERE rs.client.uId = :clientId AND rs.status = 'Paid'")
    BigDecimal sumPaidAmountByClientId(@Param("clientId") Long clientId);
    
    // 計算提供者未收款總金額
    @Query("SELECT COALESCE(SUM(rs.revenueShareAmount), 0) FROM RevenueShare rs WHERE rs.provider.uId = :providerId AND rs.status = 'Unpaid'")
    BigDecimal sumUnpaidAmountByProviderId(@Param("providerId") Long providerId);
    
    // 計算提供者已收款總金額
    @Query("SELECT COALESCE(SUM(rs.revenueShareAmount), 0) FROM RevenueShare rs WHERE rs.provider.uId = :providerId AND rs.status = 'Paid'")
    BigDecimal sumPaidAmountByProviderId(@Param("providerId") Long providerId);
    
    // 統計各狀態的數量
    @Query("SELECT rs.status, COUNT(rs) FROM RevenueShare rs GROUP BY rs.status")
    List<Object[]> countByStatus();
    
    // 根據狀態和客戶統計
    long countByClient_uIdAndStatus(Long clientId, RevenueShareStatus status);
    
    // 根據狀態和提供者統計
    long countByProvider_uIdAndStatus(Long providerId, RevenueShareStatus status);
}