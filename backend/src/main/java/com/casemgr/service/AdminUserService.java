package com.casemgr.service;

import com.casemgr.entity.User;
import com.casemgr.request.RoleIndustryScopeRequest;
import com.casemgr.request.UserBatchOrderRequest;
import com.casemgr.request.UserOrderRequest;
import com.casemgr.response.AdminRoleIndustryScopeResponse;

import java.util.List;

/**
 * 管理員用戶管理服務介面
 * 提供用戶管理相關的業務邏輯操作
 */
public interface AdminUserService {

    /**
     * 獲取所有用戶（按顯示順序排序）
     * @return 排序後的用戶列表
     */
    List<User> getAllUsersOrderedByDisplay();

    /**
     * 將用戶向上移動
     * @param userId 用戶ID
     * @return 是否成功移動
     */
    boolean moveUserUp(Long userId);

    /**
     * 將用戶向下移動
     * @param userId 用戶ID
     * @return 是否成功移動
     */
    boolean moveUserDown(Long userId);

    /**
     * 批量更新用戶順序
     * @param orderRequest 批量排序請求
     * @return 是否成功更新
     */
    boolean updateUserOrder(UserBatchOrderRequest orderRequest);

    /**
     * 獲取指定用戶的產業範圍權限
     * @param userId 用戶ID
     * @return 產業範圍權限列表
     */
    List<AdminRoleIndustryScopeResponse> getUserIndustryScopes(Long userId);

    /**
     * 創建新用戶
     * @param user 用戶實體
     * @param password 密碼
     * @param roleNames 角色名稱列表
     * @param roleIndustryScopes 角色產業範圍列表
     * @return 創建的用戶
     */
    User createUser(User user, String password, List<String> roleNames, List<RoleIndustryScopeRequest> roleIndustryScopes);

    /**
     * 更新用戶資訊
     * @param userId 用戶ID
     * @param user 更新的用戶資訊
     * @param roleNames 角色名稱列表
     * @param roleIndustryScopes 角色產業範圍列表
     * @return 更新後的用戶
     */
    User updateUser(Long userId, User user, List<String> roleNames, List<RoleIndustryScopeRequest> roleIndustryScopes);

    /**
     * 刪除用戶
     * @param userId 用戶ID
     * @return 是否成功刪除
     */
    boolean deleteUser(Long userId);

    /**
     * 啟用或停用用戶
     * @param userId 用戶ID
     * @param enabled 是否啟用
     * @return 更新後的用戶
     */
    User setUserEnabled(Long userId, boolean enabled);

    /**
     * 重置用戶密碼
     * @param userId 用戶ID
     * @param newPassword 新密碼
     * @return 是否成功重置
     */
    boolean resetUserPassword(Long userId, String newPassword);

    /**
     * 根據角色名稱獲取用戶的產業範圍
     * @param userId 用戶ID
     * @param roleName 角色名稱
     * @return 產業範圍列表
     */
    List<AdminRoleIndustryScopeResponse> getUserIndustryScopesByRole(Long userId, String roleName);

    /**
     * 多條件查詢使用者
     * @param request 查詢請求（產業/類型/關鍵字/分頁/排序）
     * @return 含清單與統計之回應
     */
    com.casemgr.response.UserSearchResponse searchUsers(com.casemgr.request.UserSearchRequest request);

    /**
     * 取得使用者統計資訊
     * @param request 統計請求（產業/類型）
     * @return 使用者統計
     */
    com.casemgr.response.UserStatistics getUserStatistics(com.casemgr.request.UserStatisticsRequest request);
}
