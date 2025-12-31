package com.casemgr.service;

import com.casemgr.response.UserRankingResponse;
import com.casemgr.response.PaginatedUserRankingResponse;

public interface RankingService {
    /**
     * 計算指定用戶的排名分數(RR)
     * @param userId 用戶ID
     * @return 排名分數結果
     */
    UserRankingResponse calculateRankingScore(Long userId);

    /**
     * 根據條件計算所有用戶的排名分數，並支援分頁
     * @param industryId 行業ID
     * @param subIndustryId 子行業ID，可為null
     * @param location 位置篩選，可為null
     * @param page 頁碼
     * @param size 每頁數量
     * @return 分頁的用戶排名列表
     */
    PaginatedUserRankingResponse calculateRankingList(Long industryId, Long subIndustryId, String location, int page, int size);
    
    /**
     * 管理員手動更新用戶的排名分數(RR)
     * @param userId 用戶ID
     * @param newScore 新的排名分數
     * @return 更新後的排名分數結果
     */
    UserRankingResponse updateUserRankingScore(Long userId, Double newScore);
}