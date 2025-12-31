package com.casemgr.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 統計項目
 * 包含總數和週新增數量的統計資訊
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "統計項目")
public class StatisticItem {
    
    @Schema(description = "總數", example = "150")
    private long total;
    
    @Schema(description = "最近一周新增數量", example = "5")
    private long weeklyNew;
    
    /**
     * 獲取顯示文字格式
     * @return 格式化的顯示文字，例如："150 (+5)"
     */
    @Schema(description = "格式化顯示文字", example = "150 (+5)")
    public String getDisplayText() {
        return String.format("%d (+%d)", total, weeklyNew);
    }
}