package com.casemgr.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.request.RoleRequest;
import com.casemgr.response.RoleResponse;
import com.casemgr.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Role management REST controller
 * Provides CRUD operations for role management with admin-only access
 * All endpoints require ADMIN role authorization
 */
@Tag(name = "Role Management", description = "Role management APIs for creating, updating, and managing user roles with admin-only access.")
@RestController
@RequestMapping("/api/admin/roles")
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class RoleController {

    private final RoleService roleService;

    /**
     * Get all roles in the system.
     * @return ResponseEntity containing list of all roles
     */
    @GetMapping
    @Operation(
        summary = "Get All Roles",
        description = "Retrieve a list of all active roles in the system. This endpoint is restricted to administrators only.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved role list"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
        }
    )
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        log.info("Retrieving all roles list");
        
        try {
            List<RoleResponse> roles = roleService.list();
            log.info("Successfully retrieved {} roles", roles.size());
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            log.error("Error occurred while retrieving roles list", e);
            throw e;
        }
    }

    /**
     * Get role details by ID.
     * @param id Role ID
     * @return ResponseEntity containing role details or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get Role by ID",
        description = "Retrieve detailed information about a specific role by its unique identifier.",
        parameters = {
            @Parameter(name = "id", description = "Role ID", required = true)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved role details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Role not found")
        }
    )
    public ResponseEntity<RoleResponse> getRoleById(
            @Parameter(description = "Role ID to retrieve", required = true)
            @PathVariable Long id) {
        log.info("Retrieving role details - id: {}", id);
        
        try {
            RoleResponse role = roleService.detail(id);
            log.info("Successfully retrieved role details - roleName: {}", role.getRoleName());
            return ResponseEntity.ok(role);
        } catch (EntityNotFoundException e) {
            log.warn("Role not found - id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error occurred while retrieving role details - id: {}", id, e);
            throw e;
        }
    }

    /**
     * Create a new role.
     * @param request Role creation request containing role information
     * @return ResponseEntity containing created role details with 201 status
     */
    @PostMapping
    @Operation(
        summary = "Create New Role",
        description = "Create a new role in the system with specified permissions and configurations.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Role creation request data",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RoleRequest.class)
            )
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Role created successfully"),
            @ApiResponse(responseCode = "400", description = "Request data validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "409", description = "Role name already exists")
        }
    )
    public ResponseEntity<RoleResponse> createRole(
            @Parameter(description = "Role creation request data", required = true)
            @Valid @RequestBody RoleRequest request) {
        log.info("Creating new role - roleName: {}", request.getRoleName());
        
        try {
            RoleResponse createdRole = roleService.create(request);
            log.info("Successfully created role - id: {}, roleName: {}", createdRole.getId(), createdRole.getRoleName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to create role - data validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error occurred while creating role - roleName: {}", request.getRoleName(), e);
            throw e;
        }
    }

    /**
     * Update an existing role.
     * @param id Role ID to update
     * @param request Role update request containing updated role information
     * @return ResponseEntity containing updated role details
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update Role",
        description = "Update an existing role's information by its ID including permissions and configurations.",
        parameters = {
            @Parameter(name = "id", description = "Role ID", required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Role update request data",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RoleRequest.class)
            )
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Request data validation failed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "409", description = "Role name already exists")
        }
    )
    public ResponseEntity<RoleResponse> updateRole(
            @Parameter(description = "Role ID to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Role update request data", required = true)
            @Valid @RequestBody RoleRequest request) {
        log.info("Updating role information - id: {}, roleName: {}", id, request.getRoleName());
        
        try {
            RoleResponse updatedRole = roleService.update(id, request);
            log.info("Successfully updated role - id: {}, roleName: {}", updatedRole.getId(), updatedRole.getRoleName());
            return ResponseEntity.ok(updatedRole);
        } catch (EntityNotFoundException e) {
            log.warn("Role not found for update - id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.warn("Failed to update role - data validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error occurred while updating role - id: {}, roleName: {}", id, request.getRoleName(), e);
            throw e;
        }
    }

    /**
     * Delete a role by ID (soft delete).
     * @param id Role ID to delete
     * @return ResponseEntity with 204 No Content status on success
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete Role",
        description = "Soft delete a specific role by ID (sets enabled=false). The role will be deactivated but not permanently removed from the system.",
        parameters = {
            @Parameter(name = "id", description = "Role ID", required = true)
        },
        responses = {
            @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Role not found")
        }
    )
    public ResponseEntity<Void> deleteRole(
            @Parameter(description = "Role ID to delete", required = true)
            @PathVariable Long id) {
        log.info("Deleting role - id: {}", id);
        
        try {
            roleService.delete(id);
            log.info("Successfully deleted role - id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Role not found for deletion - id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error occurred while deleting role - id: {}", id, e);
            throw e;
        }
    }
}