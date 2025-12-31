package com.casemgr.controller;

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

import com.casemgr.converter.SysListItemConverter;
import com.casemgr.entity.SysListItem;
import com.casemgr.repository.SysListItemRepository;
import com.casemgr.request.ContractRequest;
import com.casemgr.request.SysListItemRequest;
import com.casemgr.response.IndustryResponse;
import com.casemgr.response.ContractResponse;
import com.casemgr.response.OrderResponse;
import com.casemgr.response.SysListItemResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "System Lists", description = "System list management APIs for defining and managing system-wide configuration lists such as currencies, languages, areas, and units.")
@RestController
//@RequestMapping("/api/admin/systemlist")
@CrossOrigin
@RequiredArgsConstructor
public class SysListItemController {
	private final SysListItemRepository sysListItemRepository;
	
//	public SysListItemController(SysListItemRepository sysListItemRepository) {
//		this.sysListItemRepository = sysListItemRepository;
//	}
	
	/**
	 * Retrieve all system list items.
	 * @return List of all system list items
	 */
	@GetMapping("/api/systemlists")
	@Operation(
		summary = "Get All System List Items",
		description = "Retrieve all system list items across all types including currencies, languages, areas, units, and other system configurations."
	)
	public ResponseEntity<List<SysListItemResponse>> list() {
		List<SysListItem> sysListItems = this.sysListItemRepository.findAll();
		return ResponseEntity.ok(SysListItemConverter.INSTANCE.entityToResponse(sysListItems));
	}
	
	/**
	 * Retrieve details of a specific system list item.
	 * @param id System list item ID
	 * @return System list item details
	 */
	@GetMapping("/api/systemlists/{id}")
	@Operation(
		summary = "Get System List Item Details",
		description = "Retrieve detailed information about a specific system list item by its unique identifier."
	)
	public ResponseEntity<SysListItemResponse> detail(
		@Parameter(description = "System list item ID to retrieve", required = true)
		@PathVariable("id") Long id) {
		return ResponseEntity.ok(SysListItemConverter.INSTANCE.entityToResponse(sysListItemRepository.getReferenceById(id)));
	}
	
	/**
	 * Retrieve system list items filtered by type.
	 * @param type System list type (blocktype, currency, language, area, unit)
	 * @return List of system list items of the specified type
	 */
	@GetMapping("/api/typies/{type}")
	@Operation(
		summary = "Get System List Items by Type",
		description = "Retrieve system list items filtered by their type. " +
					  "Available types include: blocktype, currency, language, area, unit."
	)
	public ResponseEntity<List<SysListItemResponse>> list(
		@Parameter(description = "System list type (blocktype, currency, language, area, unit)", required = true, example = "currency")
		@PathVariable("type") String type) {
		List<SysListItem> sysListItems =  this.sysListItemRepository.findByType(type);
		return ResponseEntity.ok(SysListItemConverter.INSTANCE.entityToResponse(sysListItems));
	}
	
	/**
	 * Create a new system list item (Admin only).
	 * @param sysListItemRequest System list item creation request
	 * @return Created system list item details
	 */
	@PostMapping("/api/admin/systemlists")
	@Operation(
		summary = "Create System List Item",
		description = "Create a new system list item for system configuration. " +
					  "This endpoint is restricted to administrators only."
	)
	public ResponseEntity<SysListItemResponse> create(
		@Parameter(description = "System list item creation request", required = true)
		@RequestBody @Validated SysListItemRequest sysListItemRequest){
		SysListItem sysListItem = sysListItemRepository.save(SysListItemConverter.INSTANCE.requestToEntity(sysListItemRequest));
		return ResponseEntity.ok(SysListItemConverter.INSTANCE.entityToResponse(sysListItem));
	}
	
	/**
	 * Update an existing system list item (Admin only).
	 * @param id System list item ID to update
	 * @param sysListItemRequest System list item update request
	 * @return Updated system list item details
	 */
	@PutMapping("/api/admin/systemlists/{id}")
	@Operation(
		summary = "Update System List Item",
		description = "Update an existing system list item's configuration. " +
					  "This endpoint is restricted to administrators only."
	)
	public ResponseEntity<SysListItemResponse> update(
		@Parameter(description = "System list item ID to update", required = true)
		@PathVariable("id") Long id,
		@Parameter(description = "System list item update request", required = true)
		@RequestBody @Validated SysListItemRequest sysListItemRequest){
		SysListItem sysListItem = sysListItemRepository.getReferenceById(id);
		sysListItem.setDescription(sysListItemRequest.getDescription());
		sysListItem.setIconUrl(sysListItemRequest.getIconUrl());
		sysListItem.setName(sysListItemRequest.getName());
		sysListItem.setSort(sysListItemRequest.getSort());
		sysListItem.setType(sysListItemRequest.getType());
		SysListItem savedSysListItem = sysListItemRepository.save(sysListItem);
		return ResponseEntity.ok(SysListItemConverter.INSTANCE.entityToResponse(savedSysListItem));
	}
}
