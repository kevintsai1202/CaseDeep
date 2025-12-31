package com.casemgr.response;

import lombok.Data;

/**
 * 管理員角色產業範圍回應物件
 * 用於返回用戶的角色產業範圍資訊
 */
@Data
public class AdminRoleIndustryScopeResponse {
    
    private Long id;
    
    private String roleName;
    
    private Boolean isAllIndustries;
    
    private Long industryId;
    
    private String industryName;
}