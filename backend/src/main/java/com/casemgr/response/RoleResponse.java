package com.casemgr.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Role response DTO for API responses
 * Contains role information returned to clients
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse implements Serializable {
    
    /**
     * Role unique identifier
     */
    private Long id;
    
    /**
     * Role name (e.g., 'ADMIN', 'PROVIDER')
     */
    private String roleName;
    
    /**
     * Role description
     */
    private String description;
}