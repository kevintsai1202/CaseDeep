package com.casemgr.converter;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.User;
import com.casemgr.request.RegisterRequest;
import com.casemgr.response.ChatUser;
import com.casemgr.response.IntroResponse;
import com.casemgr.response.UserResponse;

@Mapper(componentModel="spring")
public interface UserConverter{
	UserConverter INSTANCT = Mappers.getMapper(UserConverter.class);
	User requestToEntity(RegisterRequest userRequest);
	@Mappings({@Mapping(target = "userId", source = "UId")})
	Set<UserResponse> entityToResponse(Set<User> user);
	@Mappings({@Mapping(target = "userId", source = "UId")})
	UserResponse entityToResponse(User user);
	
	@Mappings({@Mapping(target = "userId", source = "UId")})
	IntroResponse entityToIntroResponse(User user);
}
