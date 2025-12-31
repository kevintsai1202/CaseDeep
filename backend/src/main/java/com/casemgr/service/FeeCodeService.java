package com.casemgr.service;

import java.util.List;

import com.casemgr.entity.User;
import com.casemgr.response.FeeCodeResponse;

/**
 * 費用代碼服務介面
 * 提供建立與查詢費用代碼之服務
 */
public interface FeeCodeService {

    /**
     * 建立費用代碼
     * @param code 代碼字串（唯一）
     * @param creator 建立者（目前登入管理者）
     * @return 建立後之回應物件
     */
    FeeCodeResponse create(String code, User creator);

    /**
     * 取得所有費用代碼清單
     * @return 代碼回應清單
     */
    List<FeeCodeResponse> listAll();
}

