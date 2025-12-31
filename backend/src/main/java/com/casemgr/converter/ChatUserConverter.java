package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


import com.casemgr.entity.User;
import com.casemgr.request.RegisterRequest;
import com.casemgr.response.ChatUser;
import com.casemgr.response.UserResponse;

@Mapper(componentModel="spring")
public interface ChatUserConverter{
	ChatUserConverter INSTANCE = org.mapstruct.factory.Mappers.getMapper(ChatUserConverter.class);
	
	@Mapping(source = "UId", target = "UId")
	ChatUser entityToResponse(User user);
    List<ChatUser> entityToResponse(List<User> users);
}
