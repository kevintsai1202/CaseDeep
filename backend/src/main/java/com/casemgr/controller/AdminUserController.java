package com.casemgr.controller;

import com.casemgr.entity.User;
import com.casemgr.request.RoleIndustryScopeRequest;
import com.casemgr.request.UserBatchOrderRequest;
import com.casemgr.response.AdminRoleIndustryScopeResponse;
import com.casemgr.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理員用戶管理控制器
 * 提供用戶管理相關的 REST API 端點
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "APIs for administrative user management including user creation, updates, role assignments, and permission management")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * Get all users list ordered by display sequence
     */
    @GetMapping
    @Operation(
        summary = "Get All Users",
        description = "Retrieve a complete list of all users in the system ordered by their display sequence. " +
                      "This endpoint is used for administrative user management and provides comprehensive user information."
    )
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = adminUserService.getAllUsersOrderedByDisplay();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get user list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Move user up in display order
     */
    @PutMapping("/{userId}/move-up")
    @Operation(
        summary = "Move User Up",
        description = "Move the specified user up by one position in the display order list. " +
                      "This affects the user's position in administrative interfaces and user listings."
    )
    public ResponseEntity<Map<String, Object>> moveUserUp(
            @Parameter(description = "User ID to move up in the list") @PathVariable Long userId) {
        try {
            boolean result = adminUserService.moveUserUp(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "移動成功" : "用戶已在最頂端或不存在");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to move user up", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Move user down in display order
     */
    @PutMapping("/{userId}/move-down")
    @Operation(
        summary = "Move User Down",
        description = "Move the specified user down by one position in the display order list. " +
                      "This affects the user's position in administrative interfaces and user listings."
    )
    public ResponseEntity<Map<String, Object>> moveUserDown(
            @Parameter(description = "User ID to move down in the list") @PathVariable Long userId) {
        try {
            boolean result = adminUserService.moveUserDown(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "Move successful" : "User is already at the bottom or does not exist");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to move user down", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Batch update user display order
     */
    @PutMapping("/batch-order")
    @Operation(
        summary = "Batch Update User Order",
        description = "Update the display order of multiple users in a single operation. " +
                      "This allows for efficient reordering of user lists in administrative interfaces."
    )
    public ResponseEntity<Map<String, Object>> updateUserOrder(
            @Parameter(description = "Batch order request containing user IDs and their new positions", required = true)
            @Valid @RequestBody UserBatchOrderRequest orderRequest) {
        try {
            boolean result = adminUserService.updateUserOrder(orderRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "Batch update successful" : "Batch update failed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to batch update user order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get user's industry scope permissions
     */
    @GetMapping("/{userId}/industry-scopes")
    @Operation(
        summary = "Get User Industry Scopes",
        description = "Retrieve all industry scope permissions for a specific user. " +
                      "Industry scopes define which industries and categories a user can access and manage."
    )
    public ResponseEntity<List<AdminRoleIndustryScopeResponse>> getUserIndustryScopes(
            @Parameter(description = "User ID to retrieve industry scopes for") @PathVariable Long userId) {
        try {
            List<AdminRoleIndustryScopeResponse> scopes = adminUserService.getUserIndustryScopes(userId);
            return ResponseEntity.ok(scopes);
        } catch (Exception e) {
            log.error("Failed to get user industry scopes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get user's industry scopes by role
     */
    @GetMapping("/{userId}/industry-scopes/role/{roleName}")
    @Operation(
        summary = "Get User Industry Scopes by Role",
        description = "Retrieve industry scope permissions for a specific user filtered by role. " +
                      "This shows which industries a user can access through a particular role assignment."
    )
    public ResponseEntity<List<AdminRoleIndustryScopeResponse>> getUserIndustryScopesByRole(
            @Parameter(description = "User ID to retrieve scopes for") @PathVariable Long userId,
            @Parameter(description = "Role name to filter industry scopes") @PathVariable String roleName) {
        try {
            List<AdminRoleIndustryScopeResponse> scopes =
                    adminUserService.getUserIndustryScopesByRole(userId, roleName);
            return ResponseEntity.ok(scopes);
        } catch (Exception e) {
            log.error("Failed to get user industry scopes by role", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create new user
     */
    @PostMapping
    @Operation(
        summary = "Create User",
        description = "Create a new administrative user with specified roles and permissions. " +
                      "This endpoint allows administrators to add new users to the system with " +
                      "appropriate role assignments and industry scope permissions."
    )
    public ResponseEntity<User> createUser(
            @Parameter(description = "User creation request containing user details, roles, and permissions", required = true)
            @Valid @RequestBody CreateUserRequest request) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setEnabled(true);
            
            User createdUser = adminUserService.createUser(
                    user,
                    request.getPassword(),
                    request.getRoleNames(),
                    request.getRoleIndustryScopes()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            log.error("Failed to create user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update user information
     */
    @PutMapping("/{userId}")
    @Operation(
        summary = "Update User",
        description = "Update existing user information including personal details, roles, and permissions. " +
                      "This endpoint allows administrators to modify user accounts and their access levels."
    )
    public ResponseEntity<User> updateUser(
            @Parameter(description = "User ID to update") @PathVariable Long userId,
            @Parameter(description = "User update request containing modified user information", required = true)
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            User userUpdate = new User();
            userUpdate.setUsername(request.getUsername());
            userUpdate.setEmail(request.getEmail());
            userUpdate.setEnabled(request.isEnabled());
            
            User updatedUser = adminUserService.updateUser(
                    userId,
                    userUpdate,
                    request.getRoleNames(),
                    request.getRoleIndustryScopes()
            );
            
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Failed to update user", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{userId}")
    @Operation(
        summary = "Delete User",
        description = "Permanently delete a user from the system. This action removes the user account, " +
                      "associated roles, and permissions. This operation cannot be undone."
    )
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID to delete") @PathVariable Long userId) {
        try {
            boolean result = adminUserService.deleteUser(userId);
            if (result) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to delete user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Enable or disable user
     */
    @PutMapping("/{userId}/enable")
    @Operation(
        summary = "Enable/Disable User",
        description = "Enable or disable a user account. Disabled users cannot log in or access the system. " +
                      "This is a non-destructive way to temporarily restrict user access."
    )
    public ResponseEntity<User> setUserEnabled(
            @Parameter(description = "User ID to enable/disable") @PathVariable Long userId,
            @Parameter(description = "Whether to enable (true) or disable (false) the user") @RequestParam boolean enabled) {
        try {
            User user = adminUserService.setUserEnabled(userId, enabled);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Failed to set user enabled status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Reset user password
     */
    @PutMapping("/{userId}/reset-password")
    @Operation(
        summary = "Reset User Password",
        description = "Reset a user's password to a new value. This is an administrative function " +
                      "that allows administrators to change user passwords without knowing the current password."
    )
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "User ID to reset password for") @PathVariable Long userId,
            @Parameter(description = "Password reset request containing the new password", required = true)
            @Valid @RequestBody ResetPasswordRequest request) {
        try {
            boolean result = adminUserService.resetUserPassword(userId, request.getNewPassword());
            if (result) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error("Failed to reset password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create user request object
     */
    @lombok.Data
    public static class CreateUserRequest {
        @jakarta.validation.constraints.NotBlank(message = "Username cannot be empty")
        private String username;
        
        @jakarta.validation.constraints.Email(message = "Invalid email format")
        private String email;
        
        @jakarta.validation.constraints.NotBlank(message = "Password cannot be empty")
        @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
        
        private List<String> roleNames;
        private List<RoleIndustryScopeRequest> roleIndustryScopes;
    }

    /**
     * Update user request object
     */
    @lombok.Data
    public static class UpdateUserRequest {
        private String username;
        private String email;
        private boolean enabled;
        private List<String> roleNames;
        private List<RoleIndustryScopeRequest> roleIndustryScopes;
    }

    /**
     * Reset password request object
     */
    @lombok.Data
    public static class ResetPasswordRequest {
        @jakarta.validation.constraints.NotBlank(message = "New password cannot be empty")
        @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters")
        private String newPassword;
    }
}
