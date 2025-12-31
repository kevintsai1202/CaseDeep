package com.casemgr.service.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casemgr.converter.BlockConverter;
import com.casemgr.converter.ListItemConverter;
import com.casemgr.entity.Block;
import com.casemgr.entity.Contract;
import com.casemgr.entity.ListItem;
import com.casemgr.repository.BlockRepository;
import com.casemgr.repository.ContractRepository;
import com.casemgr.repository.ListItemRepository;
import com.casemgr.request.BlockRequest;
import com.casemgr.request.ListItemRequest;
import com.casemgr.response.BlockResponse;
import com.casemgr.service.BlockService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service("BlockService")
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

	private final ContractRepository contractRepository;
	private final BlockRepository blockRepository;
//	private final SysListItemRepository sysListItemRepository;
	private final ListItemRepository listItemRepository;
	
//	public BlockServiceImpl(ContractRepository contractRepository, BlockRepository blockRepository, SysListItemRepository sysListItemRepository) {
//		this.blockRepository = blockRepository;
//		this.contractRepository = contractRepository;
//		this.sysListItemRepository = sysListItemRepository;
//	}


	@Override
	public BlockResponse detail(Long bId) throws EntityNotFoundException {
		Optional<Block> optionalBlock = blockRepository.findById(bId);
		if(optionalBlock.isPresent()) {
			Block block = optionalBlock.get();
			return new BlockResponse(block);
//			return BlockConverter.INSTANCE.entityToResponse(block);
		}else {
			throw new EntityNotFoundException("no record found id="+bId+" for Block");
		}
	}

	@Override
	public List<BlockResponse> list() {
		List<Block> blocks = blockRepository.findAll();	
		List<BlockResponse> newBlocks= new ArrayList();
		for ( Block block: blocks ) {	
			newBlocks.add(new BlockResponse(block));
		}
		return newBlocks;
	}

	@Transactional
	@Override
	public void delete(Long bid) throws EntityNotFoundException {
		Optional<Block> optionalBlock = blockRepository.findById(bid);
		if (optionalBlock.isPresent()) {
			Block block = optionalBlock.get();
			blockRepository.delete(block);
		}else {
			throw new EntityNotFoundException("no record found id="+bid+" for block");
		}

	}

	@Transactional
	public BlockResponse create(Long cId, BlockRequest blockRequest) throws EntityNotFoundException {
		Block block = BlockConverter.INSTANCE.requestToEntity(blockRequest);
		Optional<Contract> optionalContract = contractRepository.findById(cId);
//		SysListItem sysType = sysListItemRepository.getReferenceById(blockRequest.getType());

		Contract contract=null;
		if(optionalContract.isPresent()) {
			contract = optionalContract.get();
			block.setContract(contract);
			block.setType(blockRequest.getType());
			block = blockRepository.save(block);
			return new BlockResponse(block);
		}else {
			throw new EntityNotFoundException("no record found id="+cId+" for contract");
		}
	}

	@Transactional
	@Override
	public BlockResponse update(Long bId, BlockRequest blockRequest) throws EntityNotFoundException {
//		Block block = BlockConverter.INSTANCE.requestToEntity(blockRequest);
		Optional<Block> optionalBlock = blockRepository.findById(bId);
		if(optionalBlock.isPresent()) {
			Block block = optionalBlock.get();
//			block.setBId(bId);
			block.setContext(blockRequest.getContext());
			block.setMultiple(blockRequest.getMultiple());
			block.setName(blockRequest.getName());
			block.setSort(blockRequest.getSort());
			block = blockRepository.save(block);
			return new BlockResponse(block);
		}else {
			throw new EntityNotFoundException("no record found id="+bId+" for Block");
		}
	}

	@Override
	public List<BlockResponse> listContractBlocks(Long cId) throws EntityNotFoundException {
		Optional<Contract> optionalContract = contractRepository.findById(cId);
		Contract contract;
		if(optionalContract.isPresent()) {
			contract = optionalContract.get();
			List<Block> blocks = contract.getBlocks();
			
			List<BlockResponse> newBlocks= new ArrayList();
			for ( Block block: blocks ) {	
				newBlocks.add(new BlockResponse(block));
			}
			return newBlocks;
		}else {
			throw new EntityNotFoundException("no record found id="+cId+" for Contract");
		}
	}


	@Override
	@Transactional
	public BlockResponse addListItem(Long bId, ListItemRequest listItemReq) {
		Block block =  blockRepository.getReferenceById(bId);
		ListItem newListItem = ListItemConverter.INSTANCE.requestToEntity(listItemReq);
		newListItem.setBlock(block);
		ListItem savedListItem = listItemRepository.save(newListItem);
		return new BlockResponse(block);
	}

	@Override
	@Transactional
	public BlockResponse updateListItem(Long bId, Long liId, ListItemRequest listItemReq) {
//		Block block =  blockRepository.getReferenceById(bId);
//		ListItem listItem = ListItemConverter.INSTANCE.requestToEntity(listItemReq);
		ListItem listItem = listItemRepository.getReferenceById(liId);
//		listItem.setLId(liId);
//		listItem.setBlock(block);
		listItem.setBlockSort(listItemReq.getBlockSort());
		listItem.setName(listItemReq.getName());
		listItem.setQuantity(listItemReq.getQuantity());
		listItem.setUnitPrice(listItemReq.getUnitPrice());
		listItem.setSelected(listItemReq.getSelected());
		
		listItem = listItemRepository.save(listItem);
		Block block = listItem.getBlock();
		return new BlockResponse(block);
	}

	@Override
	public void deleteListItem(Long lId) throws EntityNotFoundException {
		Optional<ListItem> optionalListItem = listItemRepository.findById(lId);
		if (optionalListItem.isPresent()) {
			ListItem listItem = optionalListItem.get();
			listItemRepository.delete(listItem);
		}else {
			throw new EntityNotFoundException("no record found id="+lId+" for ListItem");
		}
	}

}
