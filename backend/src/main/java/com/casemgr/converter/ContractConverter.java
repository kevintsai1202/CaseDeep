package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import com.casemgr.entity.Contract;
import com.casemgr.entity.Order;
import com.casemgr.request.ContractRequest;
import com.casemgr.response.ContractResponse;


@Mapper(componentModel="spring")
public interface ContractConverter{ 
	ContractConverter INSTANCE = org.mapstruct.factory.Mappers.getMapper(ContractConverter.class);
	Contract requestToEntity(ContractRequest contractRequest);
	ContractResponse entityToResponse(Contract contract);
	List<ContractResponse> entityToResponse(List<Contract> contracts);
}
