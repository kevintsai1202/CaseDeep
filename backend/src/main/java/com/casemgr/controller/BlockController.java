package com.casemgr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.request.BlockRequest;
import com.casemgr.response.BlockResponse;
import com.casemgr.service.BlockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;

@Tag(name = "Block Management", description = "APIs for managing contract blocks, block data, and block lifecycle operations")
@RestController
@RequestMapping("/api/blocks")
@CrossOrigin
public class BlockController {
	private BlockService blockService;

	public BlockController(BlockService blockService) {
		this.blockService = blockService;
	}
	
	/**
	 * Get all contract's blocks
	 * @return BlockResponse list
	 */
	@GetMapping("/contract/{cId}")
	@Operation(
		summary = "Get Blocks by Contract ID",
		description = "Retrieve all blocks associated with a specific contract. " +
					  "This endpoint provides block data for contract management and block tracking within a contract context."
	)
	public ResponseEntity<List<BlockResponse>> getBlocksByContract(
		@Parameter(description = "Unique identifier of the contract to retrieve blocks for", required = true)
		@PathVariable("cId") Long cId) {
		return ResponseEntity.ok(blockService.listContractBlocks(cId));
	}
	
	/**
	 * Get all blocks
	 * @return BlockResponse list
	 */
	@GetMapping
	@Operation(
		summary = "Get All Blocks",
		description = "Retrieve a complete list of all blocks in the system. " +
					  "This endpoint provides comprehensive block data for system-wide block management and overview."
	)
	public ResponseEntity<List<BlockResponse>> list() {
		return ResponseEntity.ok(blockService.list());
	}
	
	/**
	 * Get block detail information
	 * @param id block id
	 * @return BlockResponse
	 */
	@GetMapping(value = "/{id}")
	@Operation(
		summary = "Get Block Details by ID",
		description = "Retrieve detailed information about a specific block using its unique identifier. " +
					  "This endpoint provides comprehensive block data including content, metadata, and associated contract information."
	)
	public ResponseEntity<BlockResponse> detail(
		@Parameter(description = "Unique identifier of the block to retrieve", required = true)
		@PathVariable("id") Long id) {
		return ResponseEntity.ok(blockService.detail(id));
	}
	
	@PutMapping(value = "/{bid}")
	@Operation(
		summary = "Update Block Data",
		description = "Modify the data and properties of an existing block. " +
					  "This endpoint allows updating block content, metadata, and configuration settings."
	)
	public ResponseEntity<BlockResponse> update(
		@Parameter(description = "Unique identifier of the block to update", required = true)
		@PathVariable("bid") Long bid,
		@Parameter(description = "Block update request containing modified block information", required = true)
		@RequestBody @Validated BlockRequest blockRequest) {
		return ResponseEntity.ok(blockService.update(bid, blockRequest));
	}
	
	/**
	 * Create new block from contract
	 * @param cid Contract id
	 * @param blockRequest Block info
	 * @return BlockResponse
	 */
	@PostMapping(value = "/contract/{cid}")
	@Operation(
		summary = "Create New Block",
		description = "Create a new block within a specific contract. " +
					  "This endpoint allows adding new blocks to contracts for content organization and management."
	)
	public ResponseEntity<BlockResponse> create(
		@Parameter(description = "Unique identifier of the contract to create the block in", required = true)
		@PathVariable("cid") Long cid,
		@Parameter(description = "Block creation request containing block information and content", required = true)
		@RequestBody @Validated BlockRequest blockRequest){
		return ResponseEntity.ok(blockService.create(cid, blockRequest));
	}
	
	
	/**
	 * Delete block by id
	 * @param id block id
	 */
	@SneakyThrows
	@DeleteMapping(value = "/{id}")
	@Operation(
		summary = "Delete Block",
		description = "Permanently delete a block from the system. " +
					  "This operation will remove the block and all its associated data. Use with caution as this action cannot be undone."
	)
	public void delete(
		@Parameter(description = "Unique identifier of the block to delete", required = true)
		@PathVariable("id") Long id) {
		blockService.delete(id);
	}
	
}
