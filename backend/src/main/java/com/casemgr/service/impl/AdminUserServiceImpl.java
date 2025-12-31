package com.casemgr.service.impl;

import com.casemgr.converter.AdminRoleIndustryScopeConverter;
import com.casemgr.entity.AdminRoleIndustryScope;
import com.casemgr.entity.Industry;
import com.casemgr.entity.Role;
import com.casemgr.entity.User;
import com.casemgr.repository.AdminRoleIndustryScopeRepository;
import com.casemgr.repository.IndustryRepository;
import com.casemgr.repository.RoleRepository;
import com.casemgr.repository.UserRepository;
import com.casemgr.request.RoleIndustryScopeRequest;
import com.casemgr.request.UserBatchOrderRequest;
import com.casemgr.request.UserOrderRequest;
import com.casemgr.response.AdminRoleIndustryScopeResponse;
import com.casemgr.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理員用戶管理服務實現類
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final IndustryRepository industryRepository;
    private final AdminRoleIndustryScopeRepository adminRoleIndustryScopeRepository;
//    private final AdminRoleIndustryScopeConverter adminRoleIndustryScopeConverter;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsersOrderedByDisplay() {
        return userRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Override
    @Transactional
    public boolean moveUserUp(Long userId) {
        // 獲取當前用戶
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("用戶不存在: {}", userId);
            return false;
        }

        User currentUser = userOpt.get();
        Integer currentOrder = currentUser.getDisplayOrder();

        // 找到前一個用戶（displayOrder 小於當前用戶且最接近的）
        Optional<User> previousUserOpt = userRepository.findTopByDisplayOrderLessThanOrderByDisplayOrderDesc(currentOrder);
        
        if (previousUserOpt.isEmpty()) {
            log.info("用戶已經在最頂端: {}", userId);
            return false;
        }

        // 交換順序
        User previousUser = previousUserOpt.get();
        Integer previousOrder = previousUser.getDisplayOrder();
        
        currentUser.setDisplayOrder(previousOrder);
        previousUser.setDisplayOrder(currentOrder);
        
        userRepository.save(currentUser);
        userRepository.save(previousUser);
        
        log.info("成功將用戶 {} 向上移動", userId);
        return true;
    }

    @Override
    @Transactional
    public boolean moveUserDown(Long userId) {
        // 獲取當前用戶
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("用戶不存在: {}", userId);
            return false;
        }

        User currentUser = userOpt.get();
        Integer currentOrder = currentUser.getDisplayOrder();

        // 找到下一個用戶（displayOrder 大於當前用戶且最接近的）
        Optional<User> nextUserOpt = userRepository.findTopByDisplayOrderGreaterThanOrderByDisplayOrderAsc(currentOrder);
        
        if (nextUserOpt.isEmpty()) {
            log.info("用戶已經在最底端: {}", userId);
            return false;
        }

        // 交換順序
        User nextUser = nextUserOpt.get();
        Integer nextOrder = nextUser.getDisplayOrder();
        
        currentUser.setDisplayOrder(nextOrder);
        nextUser.setDisplayOrder(currentOrder);
        
        userRepository.save(currentUser);
        userRepository.save(nextUser);
        
        log.info("成功將用戶 {} 向下移動", userId);
        return true;
    }

    @Override
    @Transactional
    public boolean updateUserOrder(UserBatchOrderRequest orderRequest) {
        try {
            List<User> usersToUpdate = new ArrayList<>();
            
            for (UserOrderRequest userOrder : orderRequest.getOrders()) {
                Optional<User> userOpt = userRepository.findById(userOrder.getUserId());
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    user.setDisplayOrder(userOrder.getDisplayOrder());
                    usersToUpdate.add(user);
                } else {
                    log.warn("批量更新順序時找不到用戶: {}", userOrder.getUserId());
                }
            }
            
            userRepository.saveAll(usersToUpdate);
            log.info("成功批量更新 {} 個用戶的順序", usersToUpdate.size());
            return true;
            
        } catch (Exception e) {
            log.error("批量更新用戶順序失敗", e);
            return false;
        }
    }

    @Override
    public List<AdminRoleIndustryScopeResponse> getUserIndustryScopes(Long userId) {
        List<AdminRoleIndustryScope> scopes = adminRoleIndustryScopeRepository.findByUser_uId(userId);
        return scopes.stream()
                .map(AdminRoleIndustryScopeConverter.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public User createUser(User user, String password, List<String> roleNames, List<RoleIndustryScopeRequest> roleIndustryScopes) {
        // 設置密碼
        user.setPassword(passwordEncoder.encode(password));
        
        // 設置預設值
        if (user.getDisplayOrder() == null) {
            // 獲取最大的 displayOrder 並加 1
            Optional<Integer> maxOrderOpt = userRepository.findMaxDisplayOrder();
            user.setDisplayOrder(maxOrderOpt.orElse(-1) + 1);
        }
        
        // 保存用戶
        User savedUser = userRepository.save(user);
        
        // 處理角色和產業範圍
        if (roleIndustryScopes != null && !roleIndustryScopes.isEmpty()) {
            createIndustryScopesFromRequest(savedUser, roleIndustryScopes);
        }
        
        log.info("成功創建用戶: {}", savedUser.getUsername());
        return savedUser;
    }

    @Override
    @Transactional
    public User updateUser(Long userId, User userUpdate, List<String> roleNames, List<RoleIndustryScopeRequest> roleIndustryScopes) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用戶不存在: " + userId);
        }
        
        User existingUser = userOpt.get();
        
        // 更新基本資訊
        if (userUpdate.getUsername() != null) {
            existingUser.setUsername(userUpdate.getUsername());
        }
        if (userUpdate.getEmail() != null) {
            existingUser.setEmail(userUpdate.getEmail());
        }
        if (userUpdate.isEnabled() != existingUser.isEnabled()) {
            existingUser.setEnabled(userUpdate.isEnabled());
        }
        
        User savedUser = userRepository.save(existingUser);
        
        // 更新角色和產業範圍
        if (roleIndustryScopes != null) {
            // 刪除現有的產業範圍
            adminRoleIndustryScopeRepository.deleteByUser_uId(userId);
            
            // 創建新的產業範圍
            if (!roleIndustryScopes.isEmpty()) {
                createIndustryScopesFromRequest(savedUser, roleIndustryScopes);
            }
        }
        
        log.info("成功更新用戶: {}", userId);
        return savedUser;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        try {
            // 刪除用戶的產業範圍
            adminRoleIndustryScopeRepository.deleteByUser_uId(userId);
            
            // 刪除用戶
            userRepository.deleteById(userId);
            
            log.info("成功刪除用戶: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("刪除用戶失敗: {}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public User setUserEnabled(Long userId, boolean enabled) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用戶不存在: " + userId);
        }
        
        User user = userOpt.get();
        user.setEnabled(enabled);
        User savedUser = userRepository.save(user);
        
        log.info("成功{}用戶: {}", enabled ? "啟用" : "停用", userId);
        return savedUser;
    }

    @Override
    @Transactional
    public boolean resetUserPassword(Long userId, String newPassword) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("重置密碼失敗，用戶不存在: {}", userId);
                return false;
            }
            
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            log.info("成功重置用戶密碼: {}", userId);
            return true;
        } catch (Exception e) {
            log.error("重置用戶密碼失敗: {}", userId, e);
            return false;
        }
    }

    @Override
    public List<AdminRoleIndustryScopeResponse> getUserIndustryScopesByRole(Long userId, String roleName) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<AdminRoleIndustryScope> scopes = adminRoleIndustryScopeRepository
                .findByUserAndRole_RoleName(userOpt.get(), roleName);
                
        return scopes.stream()
                .map(AdminRoleIndustryScopeConverter.INSTANCE::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 創建用戶的產業範圍權限
     */
    private void createIndustryScopes(User user, List<Long> roleIds, List<Long> industryIds) {
        List<Role> roles = roleRepository.findAllById(roleIds);
        List<Industry> industries = industryRepository.findAllById(industryIds);
        
        List<AdminRoleIndustryScope> scopes = new ArrayList<>();
        
        for (Role role : roles) {
            for (Industry industry : industries) {
                AdminRoleIndustryScope scope = new AdminRoleIndustryScope();
                scope.setUser(user);
                scope.setRole(role);
                scope.setIndustry(industry);
                scopes.add(scope);
            }
        }
        
        if (!scopes.isEmpty()) {
            adminRoleIndustryScopeRepository.saveAll(scopes);
            log.info("為用戶 {} 創建了 {} 個產業範圍權限", user.getUsername(), scopes.size());
        }
    }

    /**
     * 使用角色列表創建用戶的產業範圍權限
     */
    private void createIndustryScopesWithRoles(User user, List<Role> roles, List<Long> industryIds) {
        List<Industry> industries = industryRepository.findAllById(industryIds);
        
        List<AdminRoleIndustryScope> scopes = new ArrayList<>();
        
        for (Role role : roles) {
            for (Industry industry : industries) {
                AdminRoleIndustryScope scope = new AdminRoleIndustryScope();
                scope.setUser(user);
                scope.setRole(role);
                scope.setIndustry(industry);
                scopes.add(scope);
            }
        }
        
        if (!scopes.isEmpty()) {
            adminRoleIndustryScopeRepository.saveAll(scopes);
            log.info("為用戶 {} 創建了 {} 個產業範圍權限", user.getUsername(), scopes.size());
        }
    }

    /**
     * 根據角色產業範圍請求創建用戶的產業範圍權限
     */
    private void createIndustryScopesFromRequest(User user, List<RoleIndustryScopeRequest> roleIndustryScopes) {
        List<AdminRoleIndustryScope> scopes = new ArrayList<>();
        
        for (RoleIndustryScopeRequest request : roleIndustryScopes) {
            // 根據角色名稱查詢角色實體
            Optional<Role> roleOpt = roleRepository.findByRoleName(request.getRoleName());
            if (roleOpt.isEmpty()) {
                log.warn("找不到角色: {}", request.getRoleName());
                continue;
            }
            
            Role role = roleOpt.get();
            
            if (Boolean.FALSE.equals(request.getIsAllIndustries()) && (request.getIndustryIds() != null) && !request.getIndustryIds().isEmpty()) {
                // 如果指定了特定產業ID列表
                List<Industry> industries = industryRepository.findAllById(request.getIndustryIds());
                for (Industry industry : industries) {
                    AdminRoleIndustryScope scope = new AdminRoleIndustryScope();
                    scope.setUser(user);
                    scope.setRole(role);
                    scope.setIndustry(industry);
                    scopes.add(scope);
                }
            }
        }
        
        if (!scopes.isEmpty()) {
            adminRoleIndustryScopeRepository.saveAll(scopes);
            log.info("為用戶 {} 創建了 {} 個產業範圍權限", user.getUsername(), scopes.size());
        }
    }

    /**
     * 多條件查詢使用者（基本版本）
     * - 支援 userType、searchKeyword、industry（若非 All Industries 則依授權關聯過濾）
     * - 支援分頁與排序
     */
    @Override
    @Transactional(readOnly = true)
    public com.casemgr.response.UserSearchResponse searchUsers(com.casemgr.request.UserSearchRequest request) {
        var pageable = org.springframework.data.domain.PageRequest.of(
                Math.max(0, request.getPage()),
                Math.max(1, request.getSize()),
                ("desc".equalsIgnoreCase(request.getSortDir())
                        ? org.springframework.data.domain.Sort.Direction.DESC
                        : org.springframework.data.domain.Sort.Direction.ASC),
                request.getSortBy() == null ? "displayOrder" : request.getSortBy());

        org.springframework.data.jpa.domain.Specification<User> spec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            if (request.getUserType() != null) {
                predicates.add(cb.equal(root.get("userType"), request.getUserType()));
            }

            if (request.getSearchKeyword() != null && !request.getSearchKeyword().isBlank()) {
                String kw = "%" + request.getSearchKeyword().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), kw),
                        cb.like(cb.lower(root.get("email")), kw),
                        cb.like(cb.lower(root.get("title")), kw)
                ));
            }

            if (request.getIndustry() != null && !"All Industries".equalsIgnoreCase(request.getIndustry())) {
                var scopes = root.join("adminRoleIndustryScopes", jakarta.persistence.criteria.JoinType.LEFT)
                                  .join("industry", jakarta.persistence.criteria.JoinType.LEFT);
                predicates.add(cb.equal(scopes.get("name"), request.getIndustry()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        var page = userRepository.findAll(spec, pageable);

        var users = new java.util.ArrayList<com.casemgr.response.ProviderResponse>();
        for (User u : page.getContent()) {
            com.casemgr.response.ProviderResponse pr = new com.casemgr.response.ProviderResponse();
            pr.setUserId(u.getUId());
            pr.setUsername(u.getUsername());
            pr.setEmail(u.getEmail());
            pr.setBusinessProfile(u.getBusinessProfile());
            pr.setTitle(u.getTitle());
            pr.setContent(u.getContent());
            pr.setVedioUrl(u.getVedioUrl());
            pr.setSignatureUrl(u.getSignatureUrl());
            pr.setCertified(u.getCertified());
            pr.setRegistrationDate(u.getCreateTime());
            pr.setLastActiveDate(u.getUpdateTime());
            pr.setDisplayOrder(u.getDisplayOrder());
            pr.setRankingScore(u.getRankingScore());
            pr.setCommissionRate(u.getCommissionRate());
            users.add(pr);
        }

        // 統計（簡版）
        var statistics = buildUserStatistics(request);

        return new com.casemgr.response.UserSearchResponse(users, statistics);
    }

    /**
     * 取得使用者統計資訊
     */
    @Override
    @Transactional(readOnly = true)
    public com.casemgr.response.UserStatistics getUserStatistics(com.casemgr.request.UserStatisticsRequest request) {
        return buildUserStatistics(convert(request));
    }

    private com.casemgr.request.UserSearchRequest convert(com.casemgr.request.UserStatisticsRequest req) {
        var r = new com.casemgr.request.UserSearchRequest();
        r.setIndustry(req.getIndustry());
        r.setUserType(req.getUserType());
        r.setPage(0);
        r.setSize(1);
        r.setSortBy("displayOrder");
        r.setSortDir("asc");
        return r;
    }

    /**
     * 建立統計資訊（total/weeklyNew 與基本分布）
     */
    private com.casemgr.response.UserStatistics buildUserStatistics(com.casemgr.request.UserSearchRequest req) {
        org.springframework.data.jpa.domain.Specification<User> baseSpec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (req.getUserType() != null) {
                predicates.add(cb.equal(root.get("userType"), req.getUserType()));
            }
            if (req.getIndustry() != null && !"All Industries".equalsIgnoreCase(req.getIndustry())) {
                var scopes = root.join("adminRoleIndustryScopes", jakarta.persistence.criteria.JoinType.LEFT)
                                  .join("industry", jakarta.persistence.criteria.JoinType.LEFT);
                predicates.add(cb.equal(scopes.get("name"), req.getIndustry()));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        long total = userRepository.count(baseSpec);

        // 近 7 天新用戶
        java.time.LocalDateTime sevenDaysAgo = java.time.LocalDateTime.now().minusDays(7);
        org.springframework.data.jpa.domain.Specification<User> weeklySpec = baseSpec.and((root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("createTime"), sevenDaysAgo)
        );
        long weeklyNew = userRepository.count(weeklySpec);

        var stats = new com.casemgr.response.UserStatistics();
        stats.setTotalCount(total);
        stats.setWeeklyNewCount(weeklyNew);
        stats.setIndustryBreakdown(java.util.Collections.emptyMap());
        stats.setUserTypeBreakdown(java.util.Collections.emptyMap());
        return stats;
    }
}
