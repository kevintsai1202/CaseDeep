package com.casemgr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.converter.ListItemConverter;
import com.casemgr.entity.Block;
import com.casemgr.entity.ListItem;
import com.casemgr.repository.BlockRepository;
import com.casemgr.repository.ListItemRepository;
import com.casemgr.repository.SysListItemRepository;
import com.casemgr.request.ListItemRequest;
import com.casemgr.response.ListItemResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "List Item Management", description = "APIs for managing list items that can be associated with blocks or settlements, including creation, updates, and batch operations")
@RestController
@RequestMapping("/api/listitems")
@RequiredArgsConstructor
@CrossOrigin
public class ListItemController {
	private final ListItemRepository listItemRepository;
	private final BlockRepository blockRepository;
	private final SysListItemRepository sysListItemRepository;
//	public ListItemController(ListItemRepository listItemRepository, BlockRepository blockRepository, SysListItemRepository sysListItemRepository) {
//		this.listItemRepository = listItemRepository;
//		this.blockRepository = blockRepository;
//		this.sysListItemRepository = sysListItemRepository;
//	}
	
	/**
	 * Get all list items in the system.
	 * @return List of all list items
	 */
	@GetMapping()
	@Operation(
		summary = "Get All List Items",
		description = "Retrieve a comprehensive list of all list items in the system, including their details such as " +
					  "name, quantity, unit price, and associated block or settlement information."
	)
	public ResponseEntity<List<ListItemResponse>> list() {
		List<ListItem> listItems = this.listItemRepository.findAll();
		List<ListItemResponse> allListItems = new ArrayList<>();
		for (ListItem listItem: listItems) {
			allListItems.add(ListItemConverter.INSTANCE.entityToItemResponse(listItem));
		}
		return ResponseEntity.ok(allListItems);
	}
	
	/**
	 * Get all list items associated with a specific block.
	 * @param bId Block ID
	 * @return List of list items for the specified block
	 */
	@GetMapping("/blocks/{bId}")
	@Operation(
		summary = "Get List Items by Block",
		description = "Retrieve all list items associated with a specific block. This includes items with their quantities, " +
					  "unit prices, and other details that are part of the block's content."
	)
	public ResponseEntity<List<ListItemResponse>> blockListItems(
		@Parameter(description = "Block ID to retrieve list items for", required = true)
		@PathVariable("bId") Long bId) {
		Block block =  blockRepository.getReferenceById(bId);
		List<ListItem> listItems = block.getListItems();
		
		List<ListItemResponse> allListItems = new ArrayList<>();
		for (ListItem listItem: listItems) {
			allListItems.add(ListItemConverter.INSTANCE.entityToItemResponse(listItem));
		}
		return ResponseEntity.ok(allListItems);
//		return ResponseEntity.ok(ListItemConverter.INSTANCE.entityToResponse(listItems));
	}
	
	/**
	 * Create a new list item for a specific block.
	 * @param bId Block ID
	 * @param listItemRequest List item creation request
	 * @return Created list item details
	 */
	@PostMapping("/blocks/{bId}")
	@Operation(
		summary = "Create List Item for Block",
		description = "Create a new list item and associate it with a specific block. The list item will include " +
					  "details such as name, quantity, unit, unit price, and sorting information."
	)
	public ResponseEntity<ListItemResponse> createBlockListItems(
		@Parameter(description = "Block ID to associate the list item with", required = true)
		@PathVariable("bId") Long bId,
		@Parameter(description = "List item creation request containing item details", required = true)
		@RequestBody @Validated ListItemRequest listItemRequest) {
		Block block =  blockRepository.getReferenceById(bId);
		ListItem newListItem = ListItemConverter.INSTANCE.requestToEntity(listItemRequest);
		newListItem.setBlock(block);
		ListItem savedListItem = listItemRepository.save(newListItem);
		return ResponseEntity.ok(ListItemConverter.INSTANCE.entityToItemResponse(savedListItem));
	}
	
	/**
	 * Create multiple list items for a specific block in batch.
	 * @param bId Block ID
	 * @param listItemRequests List of list item creation requests
	 * @return List of created list items
	 */
	@PostMapping("/blocks/{bId}/batch")
	@Operation(
		summary = "Batch Create List Items for Block",
		description = "Create multiple list items in a single operation and associate them with a specific block. " +
					  "This is efficient for adding multiple items at once with their respective quantities, prices, and details."
	)
	public ResponseEntity<List<ListItemResponse>> createBatchBlockListItems(
		@Parameter(description = "Block ID to associate the list items with", required = true)
		@PathVariable("bId") Long bId,
		@Parameter(description = "List of list item creation requests for batch processing", required = true)
		@RequestBody @Validated List<ListItemRequest> listItemRequests) {
		Block block =  blockRepository.getReferenceById(bId);
		List<ListItem> newListItems = ListItemConverter.INSTANCE.requestToEntity(listItemRequests);
//		newListItem.setBlock(block);
		List<ListItem> newBlockListItems = new ArrayList<>();
		newListItems.forEach(listItem -> {
			listItem.setBlock(block);
			newBlockListItems.add(listItem);
		});
		
		List<ListItem> savedBlockListItems = listItemRepository.saveAll(newBlockListItems);
		List<ListItemResponse> responseListItems = new ArrayList<>();
		
		savedBlockListItems.forEach(savedBlockListItem -> {
			responseListItems.add(ListItemConverter.INSTANCE.entityToItemResponse(savedBlockListItem));
		});
		return ResponseEntity.ok(responseListItems);
	}
	
	/**
	 * Update an existing list item.
	 * @param liId List item ID
	 * @param listItemRequest List item update request
	 * @return Updated list item details
	 */
	@PutMapping("/{liId}")
	@Operation(
		summary = "Update List Item",
		description = "Update an existing list item's details including name, quantity, unit, unit price, " +
					  "selection status, and sorting information. All modifiable fields can be updated through this endpoint."
	)
	public ResponseEntity<ListItemResponse> update(
		@Parameter(description = "List item ID to update", required = true)
		@PathVariable("liId") Long liId,
		@Parameter(description = "List item update request containing modified details", required = true)
		@RequestBody @Validated ListItemRequest listItemRequest) {
		ListItem listItem= listItemRepository.getReferenceById(liId);

		listItem.setBlockSort(listItemRequest.getBlockSort());
		listItem.setName(listItemRequest.getName());
		listItem.setQuantity(listItemRequest.getQuantity());
		listItem.setSelected(listItemRequest.getSelected());
//		listItem.setSettlementSort(listItemRequest.getSettlementSort());
		listItem.setUnit(listItemRequest.getUnit());
		listItem.setUnitPrice(listItemRequest.getUnitPrice());
		
		ListItem savedListItem = listItemRepository.save(listItem);
		return ResponseEntity.ok(ListItemConverter.INSTANCE.entityToItemResponse(savedListItem));
	}
	
}
