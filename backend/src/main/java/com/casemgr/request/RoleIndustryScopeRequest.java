package com.casemgr.request;

import lombok.Data;

import java.util.List;

/**
 * 角色產業範圍請求資料傳輸物件
 * 用於傳遞角色類型和相關的產業範圍資訊
 */
@Data
public class RoleIndustryScopeRequest {

    /**
     * 角色名稱
     */
    private String roleName;

    /**
     * 是否管理所有產業
     */
    private Boolean isAllIndustries;

    /**
     * 如果不是管理所有產業，則為特定產業的ID列表
     */
    private List<Long> industryIds;
}