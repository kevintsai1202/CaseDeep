package com.casemgr.service;

import java.util.List;

import com.casemgr.request.BlockRequest;
import com.casemgr.request.ListItemRequest;
import com.casemgr.response.BlockResponse;

import jakarta.persistence.EntityNotFoundException;

public interface BlockService {
	BlockResponse create(Long cId, BlockRequest blockRequest) throws EntityNotFoundException;//Create block from model
	BlockResponse update(Long bId, BlockRequest blockRequest) throws EntityNotFoundException;
	BlockResponse detail(Long bId) throws EntityNotFoundException;
	List<BlockResponse> list();
	List<BlockResponse> listContractBlocks(Long cId)throws EntityNotFoundException;
	void delete(Long bId) throws EntityNotFoundException;
	void deleteListItem(Long lId) throws EntityNotFoundException;
	
	BlockResponse addListItem(Long bId, ListItemRequest listItemReq);
	BlockResponse updateListItem(Long bId, Long liId, ListItemRequest listItemReq);
}
