package com.casemgr.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.casemgr.entity.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    
    /**
     * 根據貨幣代碼查詢貨幣
     * @param currency 貨幣代碼
     * @return 貨幣實體
     */
    Currency findByCurrency(String currency);
    
    /**
     * 根據貨幣代碼查詢貨幣（Optional）
     * @param currency 貨幣代碼
     * @return 貨幣實體（Optional）
     */
    @Query("SELECT c FROM Currency c WHERE c.currency = :currency")
    Optional<Currency> findByCurrencyOptional(@Param("currency") String currency);
    
    /**
     * 檢查貨幣代碼是否存在
     * @param currency 貨幣代碼
     * @return 是否存在
     */
    boolean existsByCurrency(String currency);
}