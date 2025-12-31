package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;


import com.casemgr.entity.OrderTemplate;
import com.casemgr.request.OrderTemplateRequest;

import com.casemgr.response.OrderTemplateResponse;


@Mapper(componentModel="spring")
public interface OrderTemplateConverter{
	OrderTemplateConverter INSTANCE = org.mapstruct.factory.Mappers.getMapper(OrderTemplateConverter.class);
	OrderTemplate requestToEntity(OrderTemplateRequest orderTemplateRequest);
	List<OrderTemplateResponse> entityToResponse(List<OrderTemplate> orderTemplates);
	OrderTemplateResponse entityToResponse(OrderTemplate order);
//	List<OrderTemplateResponse> entityToTemplateResponse(List<OrderTemplate> orders);
//	OrderTemplateResponse entityToTemplateResponse(OrderTemplate orderTemplate);
}

