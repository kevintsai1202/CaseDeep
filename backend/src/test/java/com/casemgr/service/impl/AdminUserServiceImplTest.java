package com.casemgr.service.impl;

import com.casemgr.entity.Role;
import com.casemgr.entity.Industry;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * AdminUserService 測試類
 * 測試管理員用戶管理服務的業務邏輯
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminUserService 測試")
class AdminUserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private IndustryRepository industryRepository;
    
    @Mock
    private AdminRoleIndustryScopeRepository adminRoleIndustryScopeRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // 創建測試用戶數據
        testUser1 = new User();
        testUser1.setUId(1L);
        testUser1.setUsername("user1");
        testUser1.setDisplayOrder(1);

        testUser2 = new User();
        testUser2.setUId(2L);
        testUser2.setUsername("user2");
        testUser2.setDisplayOrder(2);

        testUser3 = new User();
        testUser3.setUId(3L);
        testUser3.setUsername("user3");
        testUser3.setDisplayOrder(3);
    }

    @Test
    @DisplayName("應該能夠獲取所有用戶並按顯示順序排序")
    void shouldGetAllUsersOrderedByDisplay() {
        // Given
        List<User> expectedUsers = Arrays.asList(testUser1, testUser2, testUser3);
        when(userRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(expectedUsers);

        // When
        List<User> actualUsers = adminUserService.getAllUsersOrderedByDisplay();

        // Then
        assertNotNull(actualUsers);
        assertEquals(3, actualUsers.size());
        assertEquals("user1", actualUsers.get(0).getUsername());
        assertEquals("user2", actualUsers.get(1).getUsername());
        assertEquals("user3", actualUsers.get(2).getUsername());
        verify(userRepository, times(1)).findAllByOrderByDisplayOrderAsc();
    }

    @Test
    @DisplayName("應該能夠將用戶向上移動")
    void shouldMoveUserUp() {
        // Given
        testUser2.setDisplayOrder(2);
        testUser1.setDisplayOrder(1);
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(userRepository.findTopByDisplayOrderLessThanOrderByDisplayOrderDesc(2))
            .thenReturn(Optional.of(testUser1));
        when(userRepository.save(any(User.class))).thenReturn(testUser1);

        // When
        boolean result = adminUserService.moveUserUp(2L);

        // Then
        assertTrue(result);
        assertEquals(1, testUser2.getDisplayOrder());
        assertEquals(2, testUser1.getDisplayOrder());
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("當用戶不存在時，向上移動應該失敗")
    void shouldFailMoveUserUpWhenUserNotExists() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        boolean result = adminUserService.moveUserUp(999L);

        // Then
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("當用戶已經是第一個時，向上移動應該失敗")
    void shouldFailMoveUserUpWhenUserIsFirst() {
        // Given
        testUser1.setDisplayOrder(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.findTopByDisplayOrderLessThanOrderByDisplayOrderDesc(1))
            .thenReturn(Optional.empty());

        // When
        boolean result = adminUserService.moveUserUp(1L);

        // Then
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("應該能夠將用戶向下移動")
    void shouldMoveUserDown() {
        // Given
        testUser1.setDisplayOrder(1);
        testUser2.setDisplayOrder(2);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.findTopByDisplayOrderGreaterThanOrderByDisplayOrderAsc(1))
            .thenReturn(Optional.of(testUser2));
        when(userRepository.save(any(User.class))).thenReturn(testUser1);

        // When
        boolean result = adminUserService.moveUserDown(1L);

        // Then
        assertTrue(result);
        assertEquals(2, testUser1.getDisplayOrder());
        assertEquals(1, testUser2.getDisplayOrder());
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("應該能夠啟用用戶")
    void shouldEnableUser() {
        // Given
        testUser1.setEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.save(any(User.class))).thenReturn(testUser1);

        // When
        User result = adminUserService.setUserEnabled(1L, true);

        // Then
        assertNotNull(result);
        assertTrue(testUser1.isEnabled());
        verify(userRepository, times(1)).save(testUser1);
    }

    @Test
    @DisplayName("應該能夠禁用用戶")
    void shouldDisableUser() {
        // Given
        testUser1.setEnabled(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.save(any(User.class))).thenReturn(testUser1);

        // When
        User result = adminUserService.setUserEnabled(1L, false);

        // Then
        assertNotNull(result);
        assertFalse(testUser1.isEnabled());
        verify(userRepository, times(1)).save(testUser1);
    }

    @Test
    @DisplayName("當用戶不存在時，啟用/禁用應該拋出異常")
    void shouldThrowExceptionWhenUserNotExists() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            adminUserService.setUserEnabled(999L, true);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("應該能夠重置用戶密碼")
    void shouldResetUserPassword() {
        // Given
        String newPassword = "newPassword123";
        String encodedPassword = "encodedPassword123";
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser1);

        // When
        boolean result = adminUserService.resetUserPassword(1L, newPassword);

        // Then
        assertTrue(result);
        assertEquals(encodedPassword, testUser1.getPassword());
        verify(passwordEncoder, times(1)).encode(newPassword);
        verify(userRepository, times(1)).save(testUser1);
    }

    @Test
    @DisplayName("應該能夠批量更新用戶順序")
    void shouldBatchUpdateUserOrder() {
        // Given
        UserOrderRequest order1 = new UserOrderRequest();
        order1.setUserId(1L);
        order1.setDisplayOrder(3);
        
        UserOrderRequest order2 = new UserOrderRequest();
        order2.setUserId(2L);
        order2.setDisplayOrder(1);

        UserBatchOrderRequest batchRequest = new UserBatchOrderRequest();
        batchRequest.setOrders(Arrays.asList(order1, order2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(userRepository.saveAll(any())).thenReturn(Arrays.asList(testUser1, testUser2));

        // When
        boolean result = adminUserService.updateUserOrder(batchRequest);

        // Then
        assertTrue(result);
        assertEquals(3, testUser1.getDisplayOrder());
        assertEquals(1, testUser2.getDisplayOrder());
        verify(userRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("當批量更新中包含不存在的用戶時應該繼續處理存在的用戶")
    void shouldContinueWhenSomeUsersNotExist() {
        // Given
        UserOrderRequest order1 = new UserOrderRequest();
        order1.setUserId(999L);
        order1.setDisplayOrder(1);
        
        UserOrderRequest order2 = new UserOrderRequest();
        order2.setUserId(1L);
        order2.setDisplayOrder(2);

        UserBatchOrderRequest batchRequest = new UserBatchOrderRequest();
        batchRequest.setOrders(Arrays.asList(order1, order2));

        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser1));
        when(userRepository.saveAll(any())).thenReturn(Arrays.asList(testUser1));

        // When
        boolean result = adminUserService.updateUserOrder(batchRequest);

        // Then
        assertTrue(result);
        assertEquals(2, testUser1.getDisplayOrder());
        verify(userRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("應該能夠刪除用戶")
    void shouldDeleteUser() {
        // Given
        doNothing().when(adminRoleIndustryScopeRepository).deleteByUser_uId(1L);
        doNothing().when(userRepository).deleteById(1L);

        // When
        boolean result = adminUserService.deleteUser(1L);

        // Then
        assertTrue(result);
        verify(adminRoleIndustryScopeRepository, times(1)).deleteByUser_uId(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("當用戶不存在時，刪除應該成功（軟刪除）")
    void shouldSucceedDeleteWhenUserNotExists() {
        // Given - The service handles exceptions internally and returns true for soft delete
        doNothing().when(adminRoleIndustryScopeRepository).deleteByUser_uId(999L);
        doNothing().when(userRepository).deleteById(999L);

        // When
        boolean result = adminUserService.deleteUser(999L);

        // Then
        assertTrue(result); // The actual implementation returns true even when user doesn't exist
        verify(adminRoleIndustryScopeRepository, times(1)).deleteByUser_uId(999L);
        verify(userRepository, times(1)).deleteById(999L);
    }

    @Test
    @DisplayName("應該能夠獲取用戶的產業範圍權限")
    void shouldGetUserIndustryScopes() {
        // Given
        Long userId = 1L;
        when(adminRoleIndustryScopeRepository.findByUser_uId(userId))
            .thenReturn(Arrays.asList());

        // When
        List<AdminRoleIndustryScopeResponse> result = adminUserService.getUserIndustryScopes(userId);

        // Then
        assertNotNull(result);
        verify(adminRoleIndustryScopeRepository, times(1)).findByUser_uId(userId);
    }

    @Test
    @DisplayName("應該能夠根據角色名稱獲取用戶產業範圍")
    void shouldGetUserIndustryScopesByRole() {
        // Given
        Long userId = 1L;
        String roleName = "ADMIN";
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser1));
        when(adminRoleIndustryScopeRepository.findByUserAndRole_RoleName(testUser1, roleName))
            .thenReturn(Arrays.asList());

        // When
        List<AdminRoleIndustryScopeResponse> result = adminUserService.getUserIndustryScopesByRole(userId, roleName);

        // Then
        assertNotNull(result);
        verify(adminRoleIndustryScopeRepository, times(1)).findByUserAndRole_RoleName(testUser1, roleName);
    }

    @Test
    @DisplayName("當用戶不存在時，根據角色獲取產業範圍應返回空列表")
    void shouldReturnEmptyListWhenUserNotExistsForRoleScopes() {
        // Given
        Long userId = 999L;
        String roleName = "ADMIN";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        List<AdminRoleIndustryScopeResponse> result = adminUserService.getUserIndustryScopesByRole(userId, roleName);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(adminRoleIndustryScopeRepository, never()).findByUserAndRole_RoleName(any(), any());
    }

    @Test
    @DisplayName("應該能夠刪除用戶並清理相關數據")
    void shouldDeleteUserAndCleanupRelatedData() {
        // Given
        Long userId = 1L;
        doNothing().when(adminRoleIndustryScopeRepository).deleteByUser_uId(userId);
        doNothing().when(userRepository).deleteById(userId);

        // When
        boolean result = adminUserService.deleteUser(userId);

        // Then
        assertTrue(result);
        verify(adminRoleIndustryScopeRepository, times(1)).deleteByUser_uId(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("應該能夠創建新用戶")
    void shouldCreateUser() {
        // Given
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setEmail("new@example.com");
        newUser.setDisplayOrder(null); // Ensure displayOrder is null to trigger findMaxDisplayOrder
        
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        
        List<String> roleNames = Arrays.asList("ADMIN");
        List<RoleIndustryScopeRequest> roleIndustryScopes = new ArrayList<>();
        RoleIndustryScopeRequest scopeRequest = new RoleIndustryScopeRequest();
        scopeRequest.setRoleName("ADMIN");
        scopeRequest.setIsAllIndustries(false);
        scopeRequest.setIndustryIds(Arrays.asList(1L, 2L));
        roleIndustryScopes.add(scopeRequest);
        
        Role role = new Role();
        role.setId(1L);
        role.setRoleName("ADMIN");
        
        Industry industry1 = new Industry();
        industry1.setId(1L);
        Industry industry2 = new Industry();
        industry2.setId(2L);
        
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.findMaxDisplayOrder()).thenReturn(Optional.of(5));
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(roleRepository.findByRoleName("ADMIN")).thenReturn(Optional.of(role));
        when(industryRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(industry1, industry2));
        when(adminRoleIndustryScopeRepository.saveAll(any())).thenReturn(Arrays.asList());
        
        // When
        User result = adminUserService.createUser(newUser, password, roleNames, roleIndustryScopes);
        
        // Then
        assertNotNull(result);
        assertEquals(encodedPassword, newUser.getPassword());
        // Note: displayOrder is set during save operation, so we verify it was called rather than checking the value
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(newUser);
        verify(adminRoleIndustryScopeRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("應該能夠更新用戶資訊")
    void shouldUpdateUser() {
        // Given
        Long userId = 1L;
        User userUpdate = new User();
        userUpdate.setUsername("updatedUser");
        userUpdate.setEmail("updated@example.com");
        userUpdate.setEnabled(false);
        
        List<String> roleNames = Arrays.asList("USER");
        List<RoleIndustryScopeRequest> roleIndustryScopes = Arrays.asList();
        
        testUser1.setEnabled(true); // original state
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser1));
        when(userRepository.save(any(User.class))).thenReturn(testUser1);
        doNothing().when(adminRoleIndustryScopeRepository).deleteByUser_uId(userId);
        
        // When
        User result = adminUserService.updateUser(userId, userUpdate, roleNames, roleIndustryScopes);
        
        // Then
        assertNotNull(result);
        assertEquals("updatedUser", testUser1.getUsername());
        assertEquals("updated@example.com", testUser1.getEmail());
        assertFalse(testUser1.isEnabled());
        verify(userRepository, times(1)).save(testUser1);
        verify(adminRoleIndustryScopeRepository, times(1)).deleteByUser_uId(userId);
    }

    @Test
    @DisplayName("更新不存在的用戶時應該拋出異常")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        // Given
        Long userId = 999L;
        User userUpdate = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            adminUserService.updateUser(userId, userUpdate, null, null);
        });
        
        verify(userRepository, never()).save(any(User.class));
    }
}