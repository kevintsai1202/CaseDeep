package com.casemgr.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Client業務指標
 * 包含客戶的訂單、消費等業務相關數據
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Client業務指標")
public class ClientBusinessMetrics {
    
    @Schema(description = "總訂單數量", example = "25")
    private Integer orderQuantity;
    
    @Schema(description = "總消費金額", example = "15000.00")
    private Double totalSpend;
    
    @Schema(description = "平均訂單價值", example = "600.00")
    private Double averageOrderValue;
    
    @Schema(description = "訂單子行業列表", example = "[\"軟體開發\", \"數據分析\"]")
    private List<String> orderSubIndustries;
    
    @Schema(description = "總佣金金額", example = "750.00")
    private Double totalCommissionAmount;
}