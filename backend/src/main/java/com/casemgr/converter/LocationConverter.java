package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.Location;
import com.casemgr.request.LocationRequest;
import com.casemgr.response.LocationResponse;

@Mapper(componentModel = "spring")
public interface LocationConverter {
    
    LocationConverter INSTANCE = Mappers.getMapper(LocationConverter.class);

    Location toEntity(LocationRequest locationRequest);

    LocationResponse toResponse(Location location);

    List<LocationResponse> entityToResponse(List<Location> locations);
}