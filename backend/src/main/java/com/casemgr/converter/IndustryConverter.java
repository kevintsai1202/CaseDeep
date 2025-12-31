package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.Industry;
import com.casemgr.request.IndustryRequest;
import com.casemgr.response.IndustryResponse;


@Mapper(componentModel="spring")
public interface IndustryConverter{ 
	
	IndustryConverter INSTANCE = Mappers.getMapper(IndustryConverter.class);

	Industry toEntity(IndustryRequest industryRequest);

	IndustryResponse toResponse(Industry industry);

	List<IndustryResponse> entityToResponse(List<Industry> industries);
}
