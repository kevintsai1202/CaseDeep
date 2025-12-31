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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.request.IndustryRequest;
import com.casemgr.entity.SysListItem;
import com.casemgr.repository.SysListItemRepository;
import com.casemgr.response.IndustryResponse;
import com.casemgr.response.OrderTemplateResponse;
import com.casemgr.service.IndustryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Industry Management", description = "APIs for managing industry categories, classifications, and related order templates")
@RestController
@RequestMapping("/api/industries")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class IndustryController {
	private final IndustryService industryService;
	private final SysListItemRepository sysListItemRepository;

	/**
	 * Get industry list
	 * @return List of industries
	 */
	@GetMapping()
	@Operation(
		summary = "Get Industry List",
		description = "Retrieve a complete list of all industries and categories available in the system. " +
					  "This includes both parent industries and their sub-categories with hierarchical structure."
	)
	public ResponseEntity<List<IndustryResponse>> list() {
		return ResponseEntity.ok(industryService.list());
	}
	
	/**
	 * Get industry details by ID
	 * @param id Industry ID
	 * @return Industry detailed information
	 */
	@GetMapping(value = "/{id}")
	@Operation(
		summary = "Get Industry Details by ID",
		description = "Retrieve detailed information about a specific industry by its unique identifier. " +
					  "Returns comprehensive industry data including name, description, parent relationships, and metadata."
	)
	public ResponseEntity<IndustryResponse> detail(
	           @Parameter(description = "Unique identifier of the industry to retrieve", required = true)
	           @PathVariable("id") Long id) {
		return ResponseEntity.ok(industryService.detail(id));
	}
	
	/**
	 * Create new industry
	 * @param industryRequest Request body containing industry information
	 * @return Created industry information
	 */
	@PostMapping()
	@Operation(
		summary = "Create New Industry",
		description = "Create a new industry category in the system. This can be either a parent industry " +
					  "or a sub-category under an existing parent. Requires appropriate administrative permissions."
	)
	public ResponseEntity<IndustryResponse> create(
		@Parameter(description = "Industry creation request containing name, description, and parent information", required = true)
		@RequestBody @Validated IndustryRequest industryRequest){
		return ResponseEntity.ok(industryService.create(industryRequest));
	}

	/**
	 * Update existing industry
	 * @param id Industry ID to update
	 * @param industryRequest Request body containing updated industry information
	 * @return Updated industry information
	 */
	@PutMapping(value = "/{id}")
	@Operation(
		summary = "Update Industry by ID",
		description = "Update an existing industry's information including name, description, parent relationships, " +
					  "and other metadata. Changes may affect related order templates and user permissions."
	)
	public ResponseEntity<IndustryResponse> update(
	           @Parameter(description = "Unique identifier of the industry to update", required = true)
	           @PathVariable("id") Long id,
	           @Parameter(description = "Industry update request containing modified information", required = true)
	           @RequestBody @Validated IndustryRequest industryRequest){
		return ResponseEntity.ok(industryService.update(id, industryRequest));
	}
	
	/**
	 * Delete industry by ID
	 * @param id Industry ID to delete
	 */
	@SneakyThrows
	@DeleteMapping(value = "/{id}")
	@Operation(
		summary = "Delete Industry by ID",
		description = "Permanently delete an industry from the system. This action will also affect " +
					  "related sub-industries, order templates, and user permissions. Use with caution as this operation cannot be undone."
	)
	public ResponseEntity<Void> delete(
		@Parameter(description = "Unique identifier of the industry to delete", required = true)
		@PathVariable("id") Long id) {
		industryService.delete(id);
        return ResponseEntity.noContent().build();
	}

	/**
	 * Get system list items by type (e.g., order template names)
	 * @param type Type name (corresponds to SysListItem type field)
	 * @return List of related item names
	 */
	@GetMapping(value = "/type/{type}")
	@Operation(
		summary = "Get System List Items by Type",
		description = "Retrieve a list of system list item names filtered by type. " +
					  "This is commonly used to fetch related items like order templates associated with a specific industry type."
	)
	public ResponseEntity<List<String>> getSysListItemsByType(
	           @Parameter(description = "Type name to filter system list items (e.g., industry name used as template type)", required = true)
	           @PathVariable("type") String type) {
		List<SysListItem> sysListItems =  this.sysListItemRepository.findByType(type);
		return ResponseEntity.ok(sysListItems.stream().map(SysListItem::getName).toList());
	}
	
	/**
	 * Get order templates by parent industry
	 * @param parentIndustry Parent industry name
	 * @param region Region filter (optional)
	 * @param page Page number (0-based, default 0)
	 * @param size Page size (default 20)
	 * @return List of order templates
	 */
	@GetMapping("/{parentIndustry}/ordertemplates")
	@Operation(
		summary = "Get Order Templates by Parent Industry",
		description = "Retrieve order templates associated with a specific parent industry. " +
					  "Results can be filtered by region and support pagination for large datasets."
	)
	public ResponseEntity<List<OrderTemplateResponse>> getOrderTemplatesByParentIndustry(
	        @Parameter(description = "Name of the parent industry to filter templates", required = true)
	        @PathVariable("parentIndustry") String parentIndustry,
	        @Parameter(description = "Optional region filter to narrow results by geographic area")
	        @RequestParam(required = false) String region,
	        @Parameter(description = "Page number for pagination (0-based indexing)")
	        @RequestParam(required = false, defaultValue = "0") int page,
	        @Parameter(description = "Number of items per page for pagination")
	        @RequestParam(required = false, defaultValue = "20") int size) {
	    
	    log.info("Getting OrderTemplates by parent industry: {}, region: {}, page: {}, size: {}",
	             parentIndustry, region, page, size);
	    
	    try {
	        List<OrderTemplateResponse> templates = industryService.getOrderTemplatesByParentIndustry(
	            parentIndustry, region, page, size);
	        
	        log.info("Found {} OrderTemplates for parent industry: {}", templates.size(), parentIndustry);
	        return ResponseEntity.ok(templates);
	        
	    } catch (EntityNotFoundException e) {
	        log.error("Industry not found: {}", parentIndustry, e);
	        return ResponseEntity.notFound().build();
	    } catch (Exception e) {
	        log.error("Error getting OrderTemplates by parent industry: {}", parentIndustry, e);
	        throw e;
	    }
	}
	
	/**
	 * Get order templates by parent and child industry
	 * @param parentIndustry Parent industry name
	 * @param childIndustry Child industry name
	 * @param region Region filter (optional)
	 * @param page Page number (0-based, default 0)
	 * @param size Page size (default 20)
	 * @return List of order templates
	 */
	@GetMapping("/{parentIndustry}/{childIndustry}/ordertemplates")
	@Operation(
		summary = "Get Order Templates by Industry Hierarchy",
		description = "Retrieve order templates filtered by both parent and child industry categories. " +
					  "This provides more specific template matching based on the complete industry hierarchy."
	)
	public ResponseEntity<List<OrderTemplateResponse>> getOrderTemplatesByIndustryHierarchy(
	        @Parameter(description = "Name of the parent industry category", required = true)
	        @PathVariable("parentIndustry") String parentIndustry,
	        @Parameter(description = "Name of the child industry sub-category", required = true)
	        @PathVariable("childIndustry") String childIndustry,
	        @Parameter(description = "Optional region filter to narrow results by geographic area")
	        @RequestParam(required = false) String region,
	        @Parameter(description = "Page number for pagination (0-based indexing)")
	        @RequestParam(required = false, defaultValue = "0") int page,
	        @Parameter(description = "Number of items per page for pagination")
	        @RequestParam(required = false, defaultValue = "20") int size) {
	    
	    log.info("Getting OrderTemplates by industry hierarchy: parent={}, child={}, region={}, page={}, size={}",
	             parentIndustry, childIndustry, region, page, size);
	    
	    try {
	        List<OrderTemplateResponse> templates = industryService.getOrderTemplatesByIndustryHierarchy(
	            parentIndustry, childIndustry, region, page, size);
	        
	        log.info("Found {} OrderTemplates for industry hierarchy: {} > {}",
	                 templates.size(), parentIndustry, childIndustry);
	        return ResponseEntity.ok(templates);
	        
	    } catch (EntityNotFoundException e) {
	        log.error("Industry not found: {} > {}", parentIndustry, childIndustry, e);
	        return ResponseEntity.notFound().build();
	    } catch (Exception e) {
	        log.error("Error getting OrderTemplates by industry hierarchy: {} > {}",
	                  parentIndustry, childIndustry, e);
	        throw e;
	    }
	}
	
	/**
	 * Get all sub-industries of a specific industry
	 * @param name Parent industry name
	 * @return List of sub-industries
	 */
	@GetMapping("/{name}/subindustries")
	@Operation(
		summary = "Get Sub-Industries by Parent Name",
		description = "Retrieve all sub-industry categories that belong to a specific parent industry. " +
					  "This shows the hierarchical structure and helps in understanding industry classifications."
	)
	public ResponseEntity<List<IndustryResponse>> getSubIndustries(
	        @Parameter(description = "Name of the parent industry to retrieve sub-industries for", required = true)
	        @PathVariable("name") String name) {
	    
	    log.info("Getting sub-industries for parent name: {}", name);
	    
	    try {
	        List<IndustryResponse> subIndustries = industryService.getSubIndustries(name);
	        log.info("Found {} sub-industries for parent name: {}", subIndustries.size(), name);
	        return ResponseEntity.ok(subIndustries);
	    } catch (EntityNotFoundException e) {
	        log.error("Parent industry not found with name: {}", name, e);
	        return ResponseEntity.notFound().build();
	    } catch (Exception e) {
	        log.error("Error getting sub-industries for parent name: {}", name, e);
	        throw e;
	    }
	}
	
	/**
	 * Get all parent industries
	 * @return List of parent industries ordered by name
	 */
	@GetMapping("/parents")
	@Operation(
	 summary = "Get Parent Industries",
	 description = "Retrieve all parent industries ordered by name. " +
	      "Parent industries are those with no parent industry (parentIndustry is null). " +
	      "This endpoint provides a clean list of top-level industry categories."
	)
	public ResponseEntity<List<IndustryResponse>> getParentIndustries() {
	 log.info("Getting all parent industries");
	 
	 try {
	  List<IndustryResponse> parentIndustries = industryService.listParentIndustries();
	  log.info("Found {} parent industries", parentIndustries.size());
	  return ResponseEntity.ok(parentIndustries);
	 } catch (Exception e) {
	  log.error("Error getting parent industries", e);
	  throw e;
	 }
	}
}
