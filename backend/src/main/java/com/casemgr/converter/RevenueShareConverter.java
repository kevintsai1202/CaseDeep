package com.casemgr.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.casemgr.entity.RevenueShare;
import com.casemgr.response.RevenueShareResponse;

@Component
public class RevenueShareConverter {
    
    public RevenueShareResponse toResponse(RevenueShare revenueShare) {
        if (revenueShare == null) {
            return null;
        }
        
        RevenueShareResponse response = new RevenueShareResponse();
        response.setId(revenueShare.getId());
        response.setRevenueShareNo(revenueShare.getRevenueShareNo());
        response.setRevenueShareRate(revenueShare.getRevenueShareRate());
        response.setOrderAmount(revenueShare.getOrderAmount());
        response.setRevenueShareAmount(revenueShare.getRevenueShareAmount());
        response.setStatus(revenueShare.getStatus());
        response.setPaymentTime(revenueShare.getPaymentTime());
        response.setCreateTime(revenueShare.getCreateTime());
        response.setUpdateTime(revenueShare.getUpdateTime());
        
        // 設定Order相關資訊
        if (revenueShare.getOrder() != null) {
            response.setOrderId(revenueShare.getOrder().getOId());
            response.setOrderNo(revenueShare.getOrder().getOrderNo());
        }
        
        // 設定Client相關資訊
        if (revenueShare.getClient() != null) {
            response.setClientId(revenueShare.getClient().getUId());
            response.setClientName(revenueShare.getClient().getUsername());
        }
        
        // 設定Provider相關資訊
        if (revenueShare.getProvider() != null) {
            response.setProviderId(revenueShare.getProvider().getUId());
            response.setProviderName(revenueShare.getProvider().getUsername());
        }
        
        // 設定Industry相關資訊
        if (revenueShare.getIndustry() != null) {
            response.setIndustryId(revenueShare.getIndustry().getId());
            response.setIndustryName(revenueShare.getIndustry().getName());
        }
        
        return response;
    }
    
    public List<RevenueShareResponse> toResponseList(List<RevenueShare> revenueShares) {
        if (revenueShares == null) {
            return null;
        }
        
        return revenueShares.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}