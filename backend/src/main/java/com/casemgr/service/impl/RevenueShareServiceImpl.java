package com.casemgr.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casemgr.converter.RevenueShareConverter;
import com.casemgr.entity.Industry;
import com.casemgr.entity.Order;
import com.casemgr.entity.RevenueShare;
import com.casemgr.entity.User;
import com.casemgr.enumtype.RevenueShareStatus;
import com.casemgr.exception.BusinessException;
import com.casemgr.repository.RevenueShareRepository;
import com.casemgr.response.RevenueShareResponse;
import com.casemgr.response.RevenueShareStatsResponse;
import com.casemgr.service.RevenueShareService;
import com.casemgr.utils.NumberUtils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevenueShareServiceImpl implements RevenueShareService {
    
    private final RevenueShareRepository revenueShareRepository;
    private final RevenueShareConverter revenueShareConverter;
    
    @Override
    @Transactional
    public RevenueShareResponse createRevenueShare(Order order) {
        try {
            // 驗證訂單狀態
            if (order == null) {
                throw new RuntimeException("Order cannot be null");
            }
            
            if (order.getIndustry() == null || order.getIndustry().getRevenueShareRate() == null) {
                throw new RuntimeException("Industry revenue share rate not configured");
            }
            
            if (order.getClient() == null) {
                throw new RuntimeException("Order client cannot be null");
            }
            
            if (order.getProvider() == null) {
                throw new RuntimeException("Order provider cannot be null");
            }
            
            if (order.getTotalPrice() == null) {
                throw new RuntimeException("Order total price cannot be null");
            }
            
            // 檢查是否已存在Revenue Share記錄
            if (revenueShareRepository.findByOrder_oId(order.getOId()).isPresent()) {
                throw new RuntimeException("Revenue Share record already exists for this order");
            }
            
            // 計算實際的Revenue Share Rate
            Float actualRate = calculateRevenueShareRate(order);
            
            // 計算Revenue Share Amount
            BigDecimal revenueShareAmount = order.getTotalPrice().multiply(BigDecimal.valueOf(actualRate));
            
            // 建立Revenue Share記錄
            RevenueShare revenueShare = new RevenueShare();
            revenueShare.setRevenueShareNo(NumberUtils.generateFormNumber("CO"));
            revenueShare.setOrder(order);
            revenueShare.setClient(order.getClient());
            revenueShare.setProvider(order.getProvider());
            revenueShare.setIndustry(order.getIndustry());
            revenueShare.setRevenueShareRate(actualRate);
            revenueShare.setOrderAmount(order.getTotalPrice());
            revenueShare.setRevenueShareAmount(revenueShareAmount);
            revenueShare.setStatus(RevenueShareStatus.Unpaid);
            
            RevenueShare savedRevenueShare = revenueShareRepository.save(revenueShare);
            
            log.info("Revenue Share record created successfully. ID: {}, Order ID: {}, Amount: {}", 
                savedRevenueShare.getId(), order.getOId(), revenueShareAmount);
            
            return revenueShareConverter.toResponse(savedRevenueShare);
            
        } catch (Exception e) {
            log.error("Failed to create Revenue Share for Order ID: {}", order.getOId(), e);
            throw new RuntimeException("Failed to create Revenue Share record: " + e.getMessage());
        }
    }
    
    @Override
    public Float calculateRevenueShareRate(Order order) {
        Long clientId = order.getClient().getUId();
        Long industryId = order.getIndustry().getId();
        Long currentOrderId = order.getOId();

        // 計算該Client在該Industry的歷史訂單數量（不包含當前訂單）
        int historicalOrderCount = revenueShareRepository.countByClientIdAndIndustryIdExcludingOrder(
            clientId, industryId, currentOrderId);
        Float baseRate = order.getIndustry().getRevenueShareRate();
        // 從Industry獲取基礎費率
        // 注意：這裡需要注入IndustryRepository或通過Order獲取Industry
        // 為了簡化，我們從Order中獲取Industry的revenueShareRate
        // 在實際使用中，這個方法會通過Order參數傳入Industry信息
        
        log.debug("Client ID: {}, Industry ID: {}, Historical order count: {}", 
            clientId, industryId, historicalOrderCount);
        
        // 這個方法需要基礎費率，我們需要重構一下
        return calculateRevenueShareRateWithBaseRate(historicalOrderCount, baseRate);
    }
    
    private Float calculateRevenueShareRateWithBaseRate(int historicalOrderCount, Float baseRate) {
        if (baseRate == null) {
            throw new RuntimeException("Base revenue share rate cannot be null");
        }
        
        if (historicalOrderCount == 0) {
            return baseRate; // 第一次使用原費率
        }
        
        // 第N次訂單: 原費率 - (N-1) * 2.5%
        Float reduction = historicalOrderCount * 0.025f;
        Float finalRate = baseRate - reduction;
        
        // 確保費率不會變成負數，最低為0
        Float result = Math.max(finalRate, 0.065f);
        
        log.debug("Base rate: {}, Historical count: {}, Reduction: {}, Final rate: {}", 
            baseRate, historicalOrderCount, reduction, result);
        
        return result;
    }
    
    // 重載方法，直接使用Industry對象
    // private Float calculateRevenueShareRate(User client, Industry industry, Long currentOrderId) {
    //     int historicalOrderCount = revenueShareRepository.countByClientIdAndIndustryIdExcludingOrder(
    //         client.getUId(), industry.getId(), currentOrderId);
        
    //     return calculateRevenueShareRateWithBaseRate(historicalOrderCount, industry.getRevenueShareRate());
    // }
    
    private String generateRevenueShareNo() {
        return NumberUtils.generateFormNumber("CO");
    }
    
    @Override
    @Transactional(readOnly = true)
    public RevenueShareResponse getById(Long id) throws EntityNotFoundException {
        RevenueShare revenueShare = revenueShareRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Revenue Share not found with id: " + id));
        
        return revenueShareConverter.toResponse(revenueShare);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RevenueShareResponse getByRevenueShareNo(String revenueShareNo) throws EntityNotFoundException {
        RevenueShare revenueShare = revenueShareRepository.findByRevenueShareNo(revenueShareNo)
            .orElseThrow(() -> new EntityNotFoundException("Revenue Share not found with number: " + revenueShareNo));
        
        return revenueShareConverter.toResponse(revenueShare);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RevenueShareResponse> listByClientId(Long clientId) {
        List<RevenueShare> revenueShares = revenueShareRepository.findByClient_uIdOrderByCreateTimeDesc(clientId);
        return revenueShareConverter.toResponseList(revenueShares);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RevenueShareResponse> listByProviderId(Long providerId) {
        List<RevenueShare> revenueShares = revenueShareRepository.findByProvider_uIdOrderByCreateTimeDesc(providerId);
        return revenueShareConverter.toResponseList(revenueShares);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RevenueShareResponse> listByOrderId(Long orderId) {
        return revenueShareRepository.findByOrder_oId(orderId)
            .map(revenueShare -> {
                RevenueShareResponse response = revenueShareConverter.toResponse(revenueShare);
                return java.util.Arrays.asList(response);
            })
            .orElse(new java.util.ArrayList<>());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RevenueShareResponse> listAll(Pageable pageable) {
        Page<RevenueShare> revenueShares = revenueShareRepository.findAllByOrderByCreateTimeDesc(pageable);
        return revenueShares.map(revenueShareConverter::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RevenueShareResponse> listByClientId(Long clientId, Pageable pageable) {
        Page<RevenueShare> revenueShares = revenueShareRepository.findByClient_uIdOrderByCreateTimeDesc(clientId, pageable);
        return revenueShares.map(revenueShareConverter::toResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<RevenueShareResponse> listByProviderId(Long providerId, Pageable pageable) {
        Page<RevenueShare> revenueShares = revenueShareRepository.findByProvider_uIdOrderByCreateTimeDesc(providerId, pageable);
        return revenueShares.map(revenueShareConverter::toResponse);
    }
    
    @Override
    @Transactional
    public RevenueShareResponse updateStatus(Long id, RevenueShareStatus status, LocalDateTime paymentTime) throws EntityNotFoundException {
        RevenueShare revenueShare = revenueShareRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Revenue Share not found with id: " + id));
        
        revenueShare.setStatus(status);
        if (status == RevenueShareStatus.Paid && paymentTime != null) {
            revenueShare.setPaymentTime(paymentTime);
        } else if (status == RevenueShareStatus.Paid) {
            revenueShare.setPaymentTime(LocalDateTime.now());
        }
        
        RevenueShare savedRevenueShare = revenueShareRepository.save(revenueShare);
        
        log.info("Revenue Share status updated. ID: {}, New status: {}, Payment time: {}", 
            id, status, revenueShare.getPaymentTime());
        
        return revenueShareConverter.toResponse(savedRevenueShare);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalUnpaidAmount(Long clientId) {
        return revenueShareRepository.sumUnpaidAmountByClientId(clientId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidAmount(Long clientId) {
        return revenueShareRepository.sumPaidAmountByClientId(clientId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalUnpaidAmountByProvider(Long providerId) {
        return revenueShareRepository.sumUnpaidAmountByProviderId(providerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidAmountByProvider(Long providerId) {
        return revenueShareRepository.sumPaidAmountByProviderId(providerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RevenueShareStatsResponse getStatsByClient(Long clientId) {
        RevenueShareStatsResponse stats = new RevenueShareStatsResponse();
        
        stats.setTotalUnpaidAmount(getTotalUnpaidAmount(clientId));
        stats.setTotalPaidAmount(getTotalPaidAmount(clientId));
        stats.setUnpaidCount(revenueShareRepository.countByClient_uIdAndStatus(clientId, RevenueShareStatus.Unpaid));
        stats.setPaidCount(revenueShareRepository.countByClient_uIdAndStatus(clientId, RevenueShareStatus.Paid));
        
        stats.setTotalRevenueShareAmount(stats.getTotalUnpaidAmount().add(stats.getTotalPaidAmount()));
        stats.setTotalCount(stats.getUnpaidCount() + stats.getPaidCount());
        
        return stats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public RevenueShareStatsResponse getStatsByProvider(Long providerId) {
        RevenueShareStatsResponse stats = new RevenueShareStatsResponse();
        
        stats.setTotalUnpaidAmount(getTotalUnpaidAmountByProvider(providerId));
        stats.setTotalPaidAmount(getTotalPaidAmountByProvider(providerId));
        stats.setUnpaidCount(revenueShareRepository.countByProvider_uIdAndStatus(providerId, RevenueShareStatus.Unpaid));
        stats.setPaidCount(revenueShareRepository.countByProvider_uIdAndStatus(providerId, RevenueShareStatus.Paid));
        
        stats.setTotalRevenueShareAmount(stats.getTotalUnpaidAmount().add(stats.getTotalPaidAmount()));
        stats.setTotalCount(stats.getUnpaidCount() + stats.getPaidCount());
        
        return stats;
    }
    
    @Override
    @Transactional(readOnly = true)
    public RevenueShareStatsResponse getOverallStats() {
        RevenueShareStatsResponse stats = new RevenueShareStatsResponse();
        
        List<RevenueShare> allUnpaid = revenueShareRepository.findByStatus(RevenueShareStatus.Unpaid);
        List<RevenueShare> allPaid = revenueShareRepository.findByStatus(RevenueShareStatus.Paid);
        
        BigDecimal totalUnpaid = allUnpaid.stream()
            .map(RevenueShare::getRevenueShareAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPaid = allPaid.stream()
            .map(RevenueShare::getRevenueShareAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.setTotalUnpaidAmount(totalUnpaid);
        stats.setTotalPaidAmount(totalPaid);
        stats.setUnpaidCount((long) allUnpaid.size());
        stats.setPaidCount((long) allPaid.size());
        stats.setTotalRevenueShareAmount(totalUnpaid.add(totalPaid));
        stats.setTotalCount(stats.getUnpaidCount() + stats.getPaidCount());
        
        return stats;
    }
}