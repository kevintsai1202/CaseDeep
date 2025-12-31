package com.casemgr.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casemgr.converter.FeeCodeConverter;
import com.casemgr.entity.FeeCode;
import com.casemgr.entity.User;
import com.casemgr.repository.FeeCodeRepository;
import com.casemgr.response.FeeCodeResponse;
import com.casemgr.service.FeeCodeService;

import lombok.RequiredArgsConstructor;

/**
 * 費用代碼服務實作
 */
@Service
@RequiredArgsConstructor
public class FeeCodeServiceImpl implements FeeCodeService {

    private final FeeCodeRepository feeCodeRepository;

    /**
     * 建立費用代碼
     * - 驗證唯一性，已存在則拋出 IllegalStateException 以供 Controller 轉為 409
     */
    @Override
    @Transactional
    public FeeCodeResponse create(String code, User creator) {
        FeeCode existing = feeCodeRepository.findByFeeCode(code);
        if (existing != null) {
            throw new IllegalStateException("FeeCode already exists: " + code);
        }
        FeeCode entity = new FeeCode();
        entity.setFeeCode(code);
        entity.setCreator(creator);
        FeeCode saved = feeCodeRepository.save(entity);
        return FeeCodeConverter.INSTANCT.entityToResponse(saved);
    }

    /**
     * 取得所有費用代碼清單
     */
    @Override
    @Transactional(readOnly = true)
    public List<FeeCodeResponse> listAll() {
        return feeCodeRepository.findAll()
                .stream()
                .map(FeeCodeConverter.INSTANCT::entityToResponse)
                .collect(Collectors.toList());
    }
}

