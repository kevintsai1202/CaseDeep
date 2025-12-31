package com.casemgr.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePriceRequest {

    @NotNull(message = "新價格不能為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "價格必須大於 0")
    private BigDecimal newPrice;

    // 可以考慮加入原因欄位
    // private String reason;
}