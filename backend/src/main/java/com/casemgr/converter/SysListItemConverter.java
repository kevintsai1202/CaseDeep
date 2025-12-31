package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.SysListItem;
import com.casemgr.request.SysListItemRequest;
import com.casemgr.response.SysListItemResponse;


@Mapper(componentModel="spring")
public interface SysListItemConverter{  
	SysListItemConverter INSTANCE = Mappers.getMapper(SysListItemConverter.class);
	SysListItem requestToEntity(SysListItemRequest sysListItemRequest);
	List<SysListItemResponse> entityToResponse(List<SysListItem> sysListItems);
	SysListItemResponse entityToResponse(SysListItem sysListItem);
}
