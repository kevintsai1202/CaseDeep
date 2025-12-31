package com.casemgr.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SendQuoteRequest {

    @NotNull(message = "報價金額不能為空")
    @DecimalMin(value = "0.0", inclusive = false, message = "報價金額必須大於 0")
    private BigDecimal price;

    // 可以考慮加入報價有效期限等欄位
    // private LocalDate validUntil;
}