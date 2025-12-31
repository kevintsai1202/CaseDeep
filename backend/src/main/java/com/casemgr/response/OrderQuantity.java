package com.casemgr.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 訂單數量分類
 * 區分推廣訂單和直接訂單的數量統計
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "訂單數量分類")
public class OrderQuantity {
    
    @Schema(description = "推廣訂單數量", example = "15")
    private Integer promotedOrders;
    
    @Schema(description = "直接訂單數量", example = "25")
    private Integer directOrders;
    
    @Schema(description = "總訂單數量", example = "40")
    private Integer totalOrders;
    
    /**
     * 計算總訂單數量
     */
    public void calculateTotal() {
        this.totalOrders = (promotedOrders != null ? promotedOrders : 0) + 
                          (directOrders != null ? directOrders : 0);
    }
}