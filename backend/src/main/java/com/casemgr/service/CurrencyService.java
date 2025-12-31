package com.casemgr.service;

import java.util.List;

import com.casemgr.request.CurrencyRequest;
import com.casemgr.response.CurrencyResponse;

import jakarta.persistence.EntityNotFoundException;

public interface CurrencyService {
    
    /**
     * 創建 Currency
     */
    CurrencyResponse create(CurrencyRequest currencyRequest) throws EntityNotFoundException;
    
    /**
     * 更新 Currency
     */
    CurrencyResponse update(Long id, CurrencyRequest currencyRequest) throws EntityNotFoundException;
    
    /**
     * 取得 Currency 詳細資訊
     */
    CurrencyResponse detail(Long id) throws EntityNotFoundException;
    
    /**
     * 列出所有 Currency
     */
    List<CurrencyResponse> list();
    
    /**
     * 刪除 Currency
     */
    void delete(Long id) throws EntityNotFoundException;
    
    /**
     * 根據貨幣代碼查詢 Currency
     */
    CurrencyResponse findByCurrency(String currency) throws EntityNotFoundException;
}