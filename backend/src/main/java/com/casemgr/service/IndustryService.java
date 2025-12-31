package com.casemgr.service;

import java.util.List;

import com.casemgr.request.IndustryRequest;
import com.casemgr.response.IndustryResponse;
import com.casemgr.response.OrderTemplateResponse;

import jakarta.persistence.EntityNotFoundException;

public interface IndustryService {
    
    /**
     * 創建 Industry
     */
    IndustryResponse create(IndustryRequest industryRequest) throws EntityNotFoundException;
    
    /**
     * 更新 Industry
     */
    IndustryResponse update(Long iId, IndustryRequest industryRequest) throws EntityNotFoundException;
    
    /**
     * 取得 Industry 詳細資訊
     */
    IndustryResponse detail(Long iId) throws EntityNotFoundException;
    
    /**
     * 列出所有 Industry
     */
    List<IndustryResponse> list();
    
    /**
     * 刪除 Industry
     */
    void delete(Long iId) throws EntityNotFoundException;
    
    /**
     * 根據名稱查詢 Industry
     */
    IndustryResponse findByName(String name) throws EntityNotFoundException;
    
//    /**
//     * 根據 URL Path 查詢 Industry
//     */
//    IndustryResponse findByUrlPath(String urlPath) throws EntityNotFoundException;
    
    /**
     * 根據父產業名稱查詢 OrderTemplate
     * @param parentIndustryName 父產業名稱
     * @param region 區域篩選（可為 null）
     * @param page 頁碼（從 0 開始）
     * @param size 每頁大小
     * @return OrderTemplateResponse 列表
     * @throws EntityNotFoundException 當找不到對應的產業時
     */
    List<OrderTemplateResponse> getOrderTemplatesByParentIndustry(
        String parentIndustryName, String region, int page, int size)
        throws EntityNotFoundException;
    
    /**
     * 根據產業階層查詢 OrderTemplate
     * @param parentIndustryName 父產業名稱
     * @param childIndustryName 子產業名稱
     * @param region 區域篩選（可為 null）
     * @param page 頁碼（從 0 開始）
     * @param size 每頁大小
     * @return OrderTemplateResponse 列表
     * @throws EntityNotFoundException 當找不到對應的產業時
     */
    List<OrderTemplateResponse> getOrderTemplatesByIndustryHierarchy(
        String parentIndustryName, String childIndustryName,
        String region, int page, int size)
        throws EntityNotFoundException;
    
    /**
     * 獲取指定產業的所有子產業
     * @param parentName 父產業名稱
     * @return 子產業列表
     * @throws EntityNotFoundException 當找不到對應的產業時
     */
    List<IndustryResponse> getSubIndustries(String parentName)
        throws EntityNotFoundException;
    
    /**
     * 列出所有父產業
     * 父產業定義為 parentIndustry 為 null 的產業，按名稱排序
     * @return 父產業列表，按名稱升序排列
     */
    List<IndustryResponse> listParentIndustries();
}
