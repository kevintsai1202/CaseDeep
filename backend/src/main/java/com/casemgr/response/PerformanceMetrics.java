package com.casemgr.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Provider效能指標
 * 包含各種業務表現相關的指標數據
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Provider效能指標")
public class PerformanceMetrics {
    
    @Schema(description = "即時聊天轉換率 (25% 權重)", example = "0.85")
    private Double livechatConversionRate;
    
    @Schema(description = "平均評分 (20% 權重)", example = "4.5")
    private Double rating;
    
    @Schema(description = "訂單完成率 (15% 權重)", example = "0.92")
    private Double orderCompletionRate;
    
    @Schema(description = "重複訂單率 (10% 權重)", example = "0.35")
    private Double repeatOrderRate;
    
    @Schema(description = "平均回應時間，單位：小時 (10% 權重)", example = "2.5")
    private Double averageResponseTime;
    
    @Schema(description = "進行中訂單數量 (反向權重 0-20%)", example = "3")
    private Integer inProgressOrderCount;
}