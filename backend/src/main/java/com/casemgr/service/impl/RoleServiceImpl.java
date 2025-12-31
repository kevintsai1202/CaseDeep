package com.casemgr.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casemgr.converter.RoleConverter;
import com.casemgr.entity.Role;
import com.casemgr.repository.RoleRepository;
import com.casemgr.request.RoleRequest;
import com.casemgr.response.RoleResponse;
import com.casemgr.service.RoleService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Role service implementation for role management operations
 * Provides CRUD operations for role entities with proper transaction management and exception handling
 */
@Slf4j
@Service("roleService")
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
	
    private final RoleRepository roleRepository;
    private final RoleConverter roleConverter;

    /**
     * 獲取所有角色列表
     * 只返回 enabled = true 的角色（透過 @Where 註解自動過濾）
     * 
     * @return 角色回應物件列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> list() {
        log.info("獲取所有角色列表");
        
        List<Role> roles = roleRepository.findAll();
        List<RoleResponse> responses = roles.stream()
            .map(roleConverter::toResponse)
            .collect(Collectors.toList());
            
        log.info("找到 {} 個角色", responses.size());
        return responses;
    }

    /**
     * 根據ID獲取單一角色詳細資訊
     * 
     * @param id 角色ID
     * @return 角色回應物件
     * @throws EntityNotFoundException 當找不到指定ID的角色時
     */
    @Override
    @Transactional(readOnly = true)
    public RoleResponse detail(Long id) throws EntityNotFoundException {
        log.info("獲取角色詳細資訊 - id: {}", id);
        
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
            
        RoleResponse response = roleConverter.toResponse(role);
        log.info("成功獲取角色詳細資訊 - roleName: {}", response.getRoleName());
        
        return response;
    }

    /**
     * 建立新角色
     * 檢查 roleName 是否重複，若重複則拋出適當例外
     * 
     * @param request 角色建立請求物件
     * @return 建立後的角色回應物件
     * @throws IllegalArgumentException 當角色名稱重複時
     */
    @Override
    @Transactional
    public RoleResponse create(RoleRequest request) throws IllegalArgumentException {
        log.info("建立新角色 - roleName: {}", request.getRoleName());
        
        // 檢查角色名稱是否重複
        Optional<Role> existingRole = roleRepository.findByRoleName(request.getRoleName());
        if (existingRole.isPresent()) {
            log.warn("角色名稱重複 - roleName: {}", request.getRoleName());
            throw new IllegalArgumentException("Role name already exists: " + request.getRoleName());
        }
        
        try {
            // 轉換請求物件為實體
            Role role = roleConverter.toEntity(request);
            
            // 儲存角色
            Role savedRole = roleRepository.save(role);
            
            // 轉換為回應物件
            RoleResponse response = roleConverter.toResponse(savedRole);
            
            log.info("成功建立角色 - id: {}, roleName: {}", response.getId(), response.getRoleName());
            return response;
            
        } catch (DataIntegrityViolationException e) {
            log.error("資料完整性違反 - roleName: {}", request.getRoleName(), e);
            throw new IllegalArgumentException("Role name already exists: " + request.getRoleName());
        }
    }

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
    @Override
    @Transactional
    public RoleResponse update(Long id, RoleRequest request) throws EntityNotFoundException, IllegalArgumentException {
        log.info("更新角色資訊 - id: {}, roleName: {}", id, request.getRoleName());
        
        // 確認目標角色存在
        Role existingRole = roleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        
        // 檢查角色名稱是否與其他角色重複（排除自己）
        Optional<Role> roleWithSameName = roleRepository.findByRoleName(request.getRoleName());
        if (roleWithSameName.isPresent() && !roleWithSameName.get().getId().equals(id)) {
            log.warn("角色名稱重複 - roleName: {}", request.getRoleName());
            throw new IllegalArgumentException("Role name already exists: " + request.getRoleName());
        }
        
        try {
            // 更新角色資訊
            existingRole.setRoleName(request.getRoleName());
            existingRole.setDescription(request.getDescription());
            
            // 儲存更新後的角色
            Role updatedRole = roleRepository.save(existingRole);
            
            // 轉換為回應物件
            RoleResponse response = roleConverter.toResponse(updatedRole);
            
            log.info("成功更新角色 - id: {}, roleName: {}", response.getId(), response.getRoleName());
            return response;
            
        } catch (DataIntegrityViolationException e) {
            log.error("資料完整性違反 - id: {}, roleName: {}", id, request.getRoleName(), e);
            throw new IllegalArgumentException("Role name already exists: " + request.getRoleName());
        }
    }

    /**
     * 刪除角色（軟刪除）
     * 執行軟刪除，設定 enabled = false
     * 透過 @SQLDelete 註解自動執行軟刪除
     * 
     * @param id 要刪除的角色ID
     * @throws EntityNotFoundException 當找不到指定ID的角色時
     */
    @Override
    @Transactional
    public void delete(Long id) throws EntityNotFoundException {
        log.info("刪除角色 - id: {}", id);
        
        // 確認目標角色存在
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        
        // 執行軟刪除（透過 @SQLDelete 註解自動執行 UPDATE t_role SET enabled=false WHERE id=?）
        roleRepository.delete(role);
        
        log.info("成功刪除角色 - id: {}, roleName: {}", id, role.getRoleName());
    }
}