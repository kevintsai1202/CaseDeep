package com.casemgr.repository;

import com.casemgr.entity.AdminRoleIndustryScope;
import com.casemgr.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 管理員角色產業範圍資料庫操作介面
 * 提供對 AdminRoleIndustryScope 實體的 CRUD 操作
 */
@Repository
public interface AdminRoleIndustryScopeRepository extends JpaRepository<AdminRoleIndustryScope, Long> {

    /**
     * 根據用戶 ID 查詢所有相關的產業範圍
     * @param userId 用戶 ID
     * @return 與該用戶相關的產業範圍列表
     */
    /**
     * 根據用戶 ID (uId) 查詢所有相關的產業範圍
     * @param userUId 用戶的 uId
     * @return 與該用戶相關的產業範圍列表
     */
    List<AdminRoleIndustryScope> findByUser_uId(Long userUId);

    /**
     * 根據用戶 ID (uId) 刪除所有相關的產業範圍
     * @param userUId 用戶的 uId
     */
    void deleteByUser_uId(Long userUId);
    
    /**
     * 根據用戶和角色名稱查詢產業範圍
     * @param user 用戶實體
     * @param roleName 角色名稱
     * @return 符合條件的產業範圍列表
     */
    List<AdminRoleIndustryScope> findByUserAndRole_RoleName(User user, String roleName);
    
    /**
     * 根據用戶查詢所有產業範圍
     * @param user 用戶實體
     * @return 該用戶的所有產業範圍列表
     */
    List<AdminRoleIndustryScope> findByUser(User user);
}