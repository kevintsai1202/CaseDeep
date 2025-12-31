package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.FeeCode;
import com.casemgr.entity.User;
import com.casemgr.request.RegisterRequest;
import com.casemgr.response.ChatUser;
import com.casemgr.response.FeeCodeResponse;
import com.casemgr.response.UserResponse;


@Mapper(componentModel="spring")
public interface FeeCodeConverter{ 
	FeeCodeConverter INSTANCT = Mappers.getMapper(FeeCodeConverter.class);
	FeeCodeResponse entityToResponse(FeeCode feeCode);
    List<FeeCodeResponse> entityToResponse(List<FeeCode> feeCodies);
}
