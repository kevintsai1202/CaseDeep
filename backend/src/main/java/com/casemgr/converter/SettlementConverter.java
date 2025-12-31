package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.Settlement;
import com.casemgr.request.SettlementRequest;
import com.casemgr.response.SettlementResponse;


@Mapper(componentModel="spring") 
public interface SettlementConverter{
	SettlementConverter INSTANCE = Mappers.getMapper(SettlementConverter.class);
	
	Settlement requestToEntity(SettlementRequest projectRequest);
	List<SettlementResponse> entityToResponse(List<Settlement> settlements);
	SettlementResponse entityToResponse(Settlement settlement);
}
