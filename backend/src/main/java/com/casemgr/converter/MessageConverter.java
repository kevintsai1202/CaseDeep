package com.casemgr.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.Message;
import com.casemgr.response.ChatNotification;

@Mapper(componentModel="spring")
public interface MessageConverter{ 

	MessageConverter INSTANCT = Mappers.getMapper(MessageConverter.class);
	ChatNotification entityToResponse(Message message);
}
