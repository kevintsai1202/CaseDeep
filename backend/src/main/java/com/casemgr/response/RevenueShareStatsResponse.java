package com.casemgr.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RevenueShareStatsResponse {
    
    private BigDecimal totalUnpaidAmount;
    
    private BigDecimal totalPaidAmount;
    
    private Long unpaidCount;
    
    private Long paidCount;
    
    private BigDecimal averageRevenueShareRate;
    
    private BigDecimal totalRevenueShareAmount;
    
    private Long totalCount;
}