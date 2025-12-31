package com.casemgr.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyResponse implements Serializable {
    private Long id;
    private String currency;
    private String currencySymbol;
}