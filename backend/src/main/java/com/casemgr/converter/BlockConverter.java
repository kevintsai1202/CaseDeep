package com.casemgr.converter;
import java.util.List;

import org.mapstruct.Mapper;


import com.casemgr.entity.Block;
import com.casemgr.request.BlockRequest;
import com.casemgr.request.InputBlockRequest;
import com.casemgr.request.OptionBlockRequest;
import com.casemgr.response.BlockResponse;


@Mapper(componentModel="spring")
public interface BlockConverter{
	BlockConverter INSTANCE = org.mapstruct.factory.Mappers.getMapper(BlockConverter.class);
	
	Block requestToEntity(BlockRequest blockRequest);
	Block inputRequestToEntity(InputBlockRequest inputBlockRequest);
	Block optionRequestToEntity(OptionBlockRequest optionBlockRequest);
    BlockResponse entityToResponse(Block entity);
    List<BlockResponse> entityToResponse(List<Block> entities);
}
