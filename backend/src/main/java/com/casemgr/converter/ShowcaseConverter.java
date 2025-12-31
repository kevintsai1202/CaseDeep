package com.casemgr.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.Showcase;
import com.casemgr.request.ShowcaseRequest;

@Mapper(componentModel="spring")
public interface ShowcaseConverter{ 
	ShowcaseConverter INSTANCT = Mappers.getMapper(ShowcaseConverter.class);
	Showcase requestToEntity(ShowcaseRequest showcaseRequest);
}
