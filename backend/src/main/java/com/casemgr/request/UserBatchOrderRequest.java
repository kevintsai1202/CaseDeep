package com.casemgr.request;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量用戶排序請求物件
 * 用於一次更新多個用戶的排序值
 */
@Data
public class UserBatchOrderRequest {
    
    @NotEmpty(message = "Order list cannot be empty")
    @Valid
    private List<UserOrderRequest> orders;
}