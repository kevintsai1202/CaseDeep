package com.casemgr.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 用戶排序請求物件
 * 用於單一用戶的排序值更新
 */
@Data
public class UserOrderRequest {
    
    @NotNull(message = "User ID cannot be null")
    private Long userId;
    
    @NotNull(message = "Display order cannot be null")
    private Integer displayOrder;
}