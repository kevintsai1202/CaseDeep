package com.casemgr.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casemgr.converter.CurrencyConverter;
import com.casemgr.entity.Currency;
import com.casemgr.repository.CurrencyRepository;
import com.casemgr.request.CurrencyRequest;
import com.casemgr.response.CurrencyResponse;
import com.casemgr.service.CurrencyService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("currencyService")
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyConverter currencyConverter;

    /**
     * 創建新的 Currency 實體。
     *
     * @param currencyRequest 包含 Currency 資訊的請求物件。
     * @return 創建後的 CurrencyResponse 物件。
     */
    @Transactional
    @Override
    public CurrencyResponse create(CurrencyRequest currencyRequest) {
        // 檢查貨幣代碼是否已存在
        if (currencyRepository.existsByCurrency(currencyRequest.getCurrency())) {
            throw new IllegalArgumentException("Currency already exists: " + currencyRequest.getCurrency());
        }

        Currency currency = currencyConverter.toEntity(currencyRequest);
        Currency savedCurrency = currencyRepository.save(currency);
        return currencyConverter.toResponse(savedCurrency);
    }

    /**
     * 更新現有的 Currency 實體。
     *
     * @param id 要更新的 Currency 的 ID。
     * @param currencyRequest 包含更新的 Currency 資訊的請求物件。
     * @return 更新後的 CurrencyResponse 物件。
     * @throws EntityNotFoundException 如果要更新的 Currency 不存在。
     */
    @Transactional
    @Override
    public CurrencyResponse update(Long id, CurrencyRequest currencyRequest) throws EntityNotFoundException {
        Currency existingCurrency = currencyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Currency not found with id: " + id));

        // 檢查貨幣代碼是否已被其他記錄使用
        Currency existingByCurrency = currencyRepository.findByCurrency(currencyRequest.getCurrency());
        if (existingByCurrency != null && !existingByCurrency.getId().equals(id)) {
            throw new IllegalArgumentException("Currency already exists: " + currencyRequest.getCurrency());
        }

        existingCurrency.setCurrency(currencyRequest.getCurrency());
        existingCurrency.setCurrencySymbol(currencyRequest.getCurrencySymbol());
        Currency updatedCurrency = currencyRepository.save(existingCurrency);
        return currencyConverter.toResponse(updatedCurrency);
    }

    /**
     * 獲取指定 ID 的 Currency 詳細資訊。
     *
     * @param id Currency 的 ID。
     * @return CurrencyResponse 物件。
     * @throws EntityNotFoundException 如果找不到指定 ID 的 Currency。
     */
    @Override
    @Transactional(readOnly = true)
    public CurrencyResponse detail(Long id) throws EntityNotFoundException {
        Currency currency = currencyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Currency not found with id: " + id));
        return currencyConverter.toResponse(currency);
    }

    /**
     * 列出所有 Currency。
     *
     * @return CurrencyResponse 物件列表。
     */
    @Override
    @Transactional(readOnly = true)
    public List<CurrencyResponse> list() {
        List<Currency> currencies = currencyRepository.findAll();
        return currencies.stream()
            .map(currencyConverter::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * 根據 ID 刪除 Currency。
     *
     * @param id 要刪除的 Currency 的 ID。
     * @throws EntityNotFoundException 如果找不到指定 ID 的 Currency。
     */
    @Transactional
    @Override
    public void delete(Long id) throws EntityNotFoundException {
        Currency currency = currencyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Currency not found with id: " + id));
        currencyRepository.delete(currency);
    }

    /**
     * 根據貨幣代碼查找 Currency。
     *
     * @param currency Currency 的貨幣代碼。
     * @return CurrencyResponse 物件。
     * @throws EntityNotFoundException 如果找不到具有該貨幣代碼的 Currency。
     */
    @Override
    @Transactional(readOnly = true)
    public CurrencyResponse findByCurrency(String currency) throws EntityNotFoundException {
        Currency currencyEntity = currencyRepository.findByCurrency(currency);
        if (currencyEntity == null) {
            throw new EntityNotFoundException("Currency not found with currency: " + currency);
        }
        return currencyConverter.toResponse(currencyEntity);
    }
}