package com.casemgr.service;

import java.util.List;

import com.casemgr.request.RoleRequest;
import com.casemgr.response.RoleResponse;

import jakarta.persistence.EntityNotFoundException;

/**
 * Role service interface for role management operations
 * Provides CRUD operations for role entities with proper exception handling
 */
public interface RoleService {    
    /**
     * 獲取所有角色列表
     * 只返回 enabled = true 的角色
     * 
     * @return 角色回應物件列表
     */
    List<RoleResponse> list();
    /**
     * 根據ID獲取單一角色詳細資訊
     * 
     * @param id 角色ID
     * @return 角色回應物件
     * @throws EntityNotFoundException 當找不到指定ID的角色時
     */
    RoleResponse detail(Long id) throws EntityNotFoundException;
    
    /**
     * 建立新角色
     * 檢查 roleName 是否重複，若重複則拋出適當例外
     * 
     * @param request 角色建立請求物件
     * @return 建立後的角色回應物件
     * @throws IllegalArgumentException 當角色名稱重複時
     */
    RoleResponse create(RoleRequest request) throws IllegalArgumentException;
    
    /**
     * 更新角色資訊
     * 確保目標實體存在，若不存在則拋出 EntityNotFoundException
     * 
     * @param id 要更新的角色ID
     * @param request 角色更新請求物件
     * @return 更新後的角色回應物件
     * @throws EntityNotFoundException 當找不到指定ID的角色時
     * @throws IllegalArgumentException 當角色名稱重複時
     */
    RoleResponse update(Long id, RoleRequest request) throws EntityNotFoundException, IllegalArgumentException;
    
    /**
     * 刪除角色（軟刪除）
     * 執行軟刪除，設定 enabled = false
     * 
     * @param id 要刪除的角色ID
     * @throws EntityNotFoundException 當找不到指定ID的角色時
     */
    void delete(Long id) throws EntityNotFoundException;
}