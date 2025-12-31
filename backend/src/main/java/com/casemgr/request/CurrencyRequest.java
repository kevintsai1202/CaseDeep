package com.casemgr.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Currency 創建/更新請求 DTO
 */
@Getter
@Setter
public class CurrencyRequest {
    
    @NotBlank(message = "Currency code cannot be blank")
    @Size(max = 10, message = "Currency code must be less than 10 characters")
    private String currency;
    
    @NotBlank(message = "Currency symbol cannot be blank")
    @Size(max = 10, message = "Currency symbol must be less than 10 characters")
    private String currencySymbol;
}