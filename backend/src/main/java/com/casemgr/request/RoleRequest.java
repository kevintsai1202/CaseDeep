package com.casemgr.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Role creation/update request DTO
 * Used for receiving POST and PUT requests for role management
 */
@Getter
@Setter
public class RoleRequest {
    
    /**
     * Role name field with validation constraints
     * Must not be blank and cannot exceed 50 characters
     */
    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 50, message = "Role name cannot exceed 50 characters")
    private String roleName;

    /**
     * Role description field with size constraint
     * Optional field that cannot exceed 200 characters
     */
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;
}