package com.casemgr.request;

import com.casemgr.enumtype.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull(message = "新狀態不能為空")
    private OrderStatus newStatus;

    private String reason; // 可選，用於記錄狀態變更原因，例如取消原因
}