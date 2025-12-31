package com.casemgr.request;

import java.util.List;
import java.util.Set;

import com.casemgr.enumtype.UserType;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUserUpdateRequest {

    // Fields that admin can update
    // Username and Email are typically not updatable or handled separately

    @Size(min = 8, message = "Password must be at least 8 characters long")
    // Add more password complexity validation if needed
    private String password; // Optional: Only provide if changing password

    private UserType userType;

    private String region; // e.g., "US", "CA"

    private List<String> roleNames; // Full set of roles the user should have

    private Boolean locked; // Allow admin to lock/unlock account

    private Boolean enabled; // Allow admin to enable/disable account (soft delete)

    // Add other fields admin might need to update, e.g., commissionRate
    private Double commissionRate;
/**
     * 角色產業範圍列表
     */
    private List<RoleIndustryScopeRequest> roleIndustryScopes;
}