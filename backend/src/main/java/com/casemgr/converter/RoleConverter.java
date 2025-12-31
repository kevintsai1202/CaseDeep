package com.casemgr.converter;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.casemgr.entity.Role;
import com.casemgr.request.RoleRequest;
import com.casemgr.response.RoleResponse;

/**
 * MapStruct converter for Role entity transformations
 * Handles conversion between Role entity, RoleRequest, and RoleResponse objects
 * Uses Spring component model for dependency injection
 */
@Mapper(componentModel = "spring")
public interface RoleConverter{
    /**
     * Static instance for direct access to converter methods
     */
    RoleConverter INSTANCE = Mappers.getMapper(RoleConverter.class);

    /**
     * Converts RoleRequest DTO to Role entity
     * Used when creating or updating role entities from API requests
     * 
     * @param request the RoleRequest containing role data from API
     * @return Role entity ready for persistence
     */
    Role toEntity(RoleRequest request);

    /**
     * Converts Role entity to RoleResponse DTO
     * Used when returning role data to API clients
     * 
     * @param entity the Role entity from database
     * @return RoleResponse DTO for API response
     */
    RoleResponse toResponse(Role entity);

    /**
     * Converts list of Role entities to list of RoleResponse DTOs
     * Used for batch conversion when returning multiple roles
     * 
     * @param entities list of Role entities from database
     * @return list of RoleResponse DTOs for API response
     */
    List<RoleResponse> toResponseList(List<Role> entities);
}