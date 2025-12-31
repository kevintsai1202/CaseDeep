package com.casemgr.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.casemgr.entity.AdminRoleIndustryScope;
import com.casemgr.response.AdminRoleIndustryScopeResponse;

/**
 * AdminRoleIndustryScope 實體與 Response DTO 之間的轉換器
 * 使用 MapStruct 自動生成映射代碼
 */
@Mapper
public interface AdminRoleIndustryScopeConverter {
    
    AdminRoleIndustryScopeConverter INSTANCE = 
        Mappers.getMapper(AdminRoleIndustryScopeConverter.class);
    
    @Mapping(source = "role.roleName", target = "roleName")
    @Mapping(source = "industry.id", target = "industryId")
    @Mapping(source = "industry.name", target = "industryName")
    AdminRoleIndustryScopeResponse toResponse(AdminRoleIndustryScope scope);
}