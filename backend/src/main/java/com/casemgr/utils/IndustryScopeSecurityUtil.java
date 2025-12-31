package com.casemgr.utils;

import com.casemgr.entity.AdminRoleIndustryScope;
import com.casemgr.entity.User;
import com.casemgr.repository.AdminRoleIndustryScopeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 產業範圍安全工具類別
 * 用於檢查用戶是否有權限訪問特定產業的資料
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IndustryScopeSecurityUtil {

    private final AdminRoleIndustryScopeRepository adminRoleIndustryScopeRepository;

    /**
     * 檢查當前用戶是否有權限訪問指定產業
     * @param industryId 產業ID
     * @return 是否有權限
     */
    public boolean hasAccessToIndustry(Long industryId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // 如果是超級管理員，擁有所有權限
        if (isSuperAdmin(currentUser)) {
            return true;
        }

        // 檢查用戶的產業範圍權限
        List<AdminRoleIndustryScope> scopes = adminRoleIndustryScopeRepository.findByUser(currentUser);
        return scopes.stream()
                .anyMatch(scope -> scope.getIndustry().getId().equals(industryId));
    }

    /**
     * 檢查當前用戶是否有權限訪問多個產業
     * @param industryIds 產業ID列表
     * @return 是否有權限訪問所有指定的產業
     */
    public boolean hasAccessToAllIndustries(List<Long> industryIds) {
        if (industryIds == null || industryIds.isEmpty()) {
            return true;
        }

        for (Long industryId : industryIds) {
            if (!hasAccessToIndustry(industryId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 檢查當前用戶是否有權限訪問至少一個指定的產業
     * @param industryIds 產業ID列表
     * @return 是否有權限訪問至少一個產業
     */
    public boolean hasAccessToAnyIndustry(List<Long> industryIds) {
        if (industryIds == null || industryIds.isEmpty()) {
            return true;
        }

        for (Long industryId : industryIds) {
            if (hasAccessToIndustry(industryId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 獲取當前用戶可訪問的產業ID列表
     * @return 產業ID列表
     */
    public Set<Long> getAccessibleIndustryIds() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Set.of();
        }

        // 如果是超級管理員，返回空集合（表示可以訪問所有）
        if (isSuperAdmin(currentUser)) {
            return Set.of();
        }

        List<AdminRoleIndustryScope> scopes = adminRoleIndustryScopeRepository.findByUser(currentUser);
        return scopes.stream()
                .map(scope -> scope.getIndustry().getId())
                .collect(Collectors.toSet());
    }

    /**
     * 檢查當前用戶是否有特定角色的產業權限
     * @param roleName 角色名稱
     * @param industryId 產業ID
     * @return 是否有權限
     */
    public boolean hasRoleAccessToIndustry(String roleName, Long industryId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // 如果是超級管理員，擁有所有權限
        if (isSuperAdmin(currentUser)) {
            return true;
        }

        List<AdminRoleIndustryScope> scopes = 
                adminRoleIndustryScopeRepository.findByUserAndRole_RoleName(currentUser, roleName);
                
        return scopes.stream()
                .anyMatch(scope -> scope.getIndustry().getId().equals(industryId));
    }

    /**
     * 獲取當前用戶的某個角色可訪問的產業ID列表
     * @param roleName 角色名稱
     * @return 產業ID列表
     */
    public Set<Long> getAccessibleIndustryIdsByRole(String roleName) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return Set.of();
        }

        // 如果是超級管理員，返回空集合（表示可以訪問所有）
        if (isSuperAdmin(currentUser)) {
            return Set.of();
        }

        List<AdminRoleIndustryScope> scopes = 
                adminRoleIndustryScopeRepository.findByUserAndRole_RoleName(currentUser, roleName);
                
        return scopes.stream()
                .map(scope -> scope.getIndustry().getId())
                .collect(Collectors.toSet());
    }

    /**
     * 獲取當前登入的用戶
     * @return 當前用戶，如果未登入則返回 null
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }

        return null;
    }

    /**
     * 檢查用戶是否為超級管理員
     * @param user 用戶
     * @return 是否為超級管理員
     */
    private boolean isSuperAdmin(User user) {
        // 這裡可以根據實際的角色結構來判斷
        // 例如：檢查是否有 SUPER_ADMIN 角色
        return user.getRoles() != null && 
               user.getRoles().stream()
                   .anyMatch(role -> "SUPER_ADMIN".equals(role.getRoleName()));
    }

    /**
     * 構建產業範圍查詢條件
     * 用於在查詢時自動過濾用戶無權訪問的產業資料
     * @return SQL 查詢條件字串，如果用戶可以訪問所有產業則返回 null
     */
    public String buildIndustryFilterCondition(String industryIdColumn) {
        Set<Long> accessibleIds = getAccessibleIndustryIds();
        
        // 如果返回空集合且是超級管理員，則可以訪問所有
        if (accessibleIds.isEmpty() && isSuperAdmin(getCurrentUser())) {
            return null;
        }
        
        // 如果沒有可訪問的產業，返回永假條件
        if (accessibleIds.isEmpty()) {
            return "1=0";
        }
        
        // 構建 IN 查詢條件
        String ids = accessibleIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
                
        return industryIdColumn + " IN (" + ids + ")";
    }
}