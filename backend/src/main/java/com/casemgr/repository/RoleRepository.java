package com.casemgr.repository;

import java.util.Optional;
import java.util.Set;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * 根據角色名稱查找角色
     * @param roleName 角色名稱
     * @return 角色實體的 Optional 包裝
     */
    Optional<Role> findByRoleName(String roleName);
    
    /**
     * 根據角色名稱查找角色（忽略大小寫）
     * @param roleName 角色名稱
     * @return 角色實體的 Optional 包裝
     */
    Optional<Role> findByRoleNameIgnoreCase(String roleName);
    
    /**
     * 根據角色名稱列表查找多個角色
     * @param roleNames 角色名稱列表
     * @return 角色實體集合
     */
    Set<Role> findByRoleNameIn(List<String> roleNames);
}
