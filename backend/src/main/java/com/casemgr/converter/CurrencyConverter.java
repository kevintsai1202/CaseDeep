package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.Currency;
import com.casemgr.request.CurrencyRequest;
import com.casemgr.response.CurrencyResponse;

@Mapper(componentModel = "spring")
public interface CurrencyConverter {
    
    CurrencyConverter INSTANCE = Mappers.getMapper(CurrencyConverter.class);

    Currency toEntity(CurrencyRequest currencyRequest);

    CurrencyResponse toResponse(Currency currency);

    List<CurrencyResponse> entityToResponse(List<Currency> currencies);
}