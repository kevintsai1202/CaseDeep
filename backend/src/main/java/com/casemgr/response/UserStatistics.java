package com.casemgr.response;

import com.casemgr.enumtype.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 用戶統計資訊
 * 包含總數、週新增數量和各種分類統計
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用戶統計資訊")
public class UserStatistics {
    
    @Schema(description = "總用戶數量", example = "500")
    private long totalCount;
    
    @Schema(description = "最近一周新增用戶數量", example = "12")
    private long weeklyNewCount;
    
    @Schema(description = "按行業分類的統計")
    private Map<String, StatisticItem> industryBreakdown;
    
    @Schema(description = "按用戶類型分類的統計")
    private Map<UserType, StatisticItem> userTypeBreakdown;
    
    /**
     * 獲取總數的顯示文字
     * @return 格式化的總數顯示文字
     */
    @Schema(description = "總數顯示文字", example = "500 (+12)")
    public String getTotalDisplayText() {
        return String.format("%d (+%d)", totalCount, weeklyNewCount);
    }
}