package com.casemgr.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Provider業務指標
 * 包含訂單、收入、佣金等業務相關數據
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Provider業務指標")
public class BusinessMetrics {
    
    @Schema(description = "訂單數量統計")
    private OrderQuantity orderQuantity;
    
    @Schema(description = "總收入", example = "50000.00")
    private Double totalRevenue;
    
    @Schema(description = "平均訂單價值", example = "1250.00")
    private Double averageOrderValue;
    
    @Schema(description = "佣金指標")
    private CommissionMetrics commission;
}