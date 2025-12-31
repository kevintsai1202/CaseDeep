package com.casemgr.request;

import java.util.List;
import java.util.Set;

import com.casemgr.enumtype.UserType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserCreateRequest {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 45, message = "Username must be between 3 and 45 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    // Add more password complexity validation if needed
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    // Optional fields, admin might specify or use defaults
    private UserType userType; // Defaults to CLIENT if null in service

    @NotBlank(message = "Region cannot be blank")
    private String region; // e.g., "US", "CA"

    private List<String> roleNames; // Set of role names like "ROLE_USER_MANAGE"
/**
     * 角色產業範圍列表
     */
    private List<RoleIndustryScopeRequest> roleIndustryScopes;
}