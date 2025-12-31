package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.ListItem;
import com.casemgr.request.ListItemRequest;
import com.casemgr.request.PriceListItemRequest;
import com.casemgr.response.ListItemResponse;
import com.casemgr.response.PriceListItemResponse;


@Mapper(componentModel="spring")
public interface ListItemConverter{
	ListItemConverter INSTANCE = Mappers.getMapper(ListItemConverter.class);
	
	ListItem requestToEntity(ListItemRequest listItemRequest);
	List<ListItem> requestToEntity(List<ListItemRequest> listItemRequests);
	@Mappings({@Mapping(target = "blockSort", source = "sort")})
	ListItem priceItemRequestToEntity(PriceListItemRequest priceListItemRequest);
	
	@Mappings({@Mapping(target = "blockSort", source = "sort")})
	List<ListItem> priceItemRequestToEntity(List<PriceListItemRequest> priceListItemRequests);
	
	
	@Mappings({@Mapping(target = "sort", source = "blockSort")})
	ListItemResponse entityToItemResponse(ListItem listItem);
	
	@Mappings({@Mapping(target = "sort", source = "blockSort")})
	List<ListItemResponse> entityToItemResponse(List<ListItem> listItems);
	
	@Mappings({@Mapping(target = "sort", source = "blockSort")})
	PriceListItemResponse entityToPriceItemResponse(ListItem listItem);
	
	@Mappings({@Mapping(target = "sort", source = "blockSort")})
	List<PriceListItemResponse> entityToPriceItemResponse(List<ListItem> listItems);
	
}
