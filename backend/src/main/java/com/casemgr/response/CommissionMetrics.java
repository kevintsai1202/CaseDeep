package com.casemgr.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 佣金指標
 * 包含佣金相關的統計數據
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "佣金指標")
public class CommissionMetrics {
    
    @Schema(description = "總佣金金額", example = "1500.00")
    private Double totalCommissionAmount;
    
    @Schema(description = "平均佣金金額", example = "37.50")
    private Double averageCommissionAmount;
    
    @Schema(description = "佣金率", example = "0.15")
    private Double commissionRate;
}