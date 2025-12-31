package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import com.casemgr.entity.Discount;
import com.casemgr.entity.Order;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.request.DiscountRequest;
import com.casemgr.request.OrderCreateRequest;
import com.casemgr.request.OrderTemplateRequest;
import com.casemgr.response.DiscountResponse;
import com.casemgr.response.OrderResponse;
import com.casemgr.response.OrderTemplateResponse;


import org.mapstruct.Mapping;

@Mapper(componentModel="spring") 
public interface DiscountConverter{
	DiscountConverter INSTANCE = org.mapstruct.factory.Mappers.getMapper(DiscountConverter.class);
	
	Discount requestToEntity(DiscountRequest discountRequest);
	List<DiscountResponse> entityToResponse(List<Discount> Discounts);
	@Mapping(target="description", source="description")
	DiscountResponse entityToResponse(Discount discount);
}

