package com.casemgr.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.casemgr.enumtype.RevenueShareStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RevenueShareResponse {
    
    private Long id;
    
    private String revenueShareNo;
    
    private Long orderId;
    
    private String orderNo;              // 來自Order
    
    private Long clientId;
    
    private String clientName;           // 來自User
    
    private Long providerId;
    
    private String providerName;         // 來自User
    
    private Long industryId;
    
    private String industryName;         // 來自Industry
    
    private Float revenueShareRate;
    
    private BigDecimal orderAmount;
    
    private BigDecimal revenueShareAmount;
    
    private RevenueShareStatus status;
    
    private LocalDateTime paymentTime;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}