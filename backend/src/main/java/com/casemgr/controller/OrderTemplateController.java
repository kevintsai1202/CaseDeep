package com.casemgr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.repository.OrderTemplateRepository;
import com.casemgr.request.DiscountRequest;
import com.casemgr.request.InputBlockRequest;
import com.casemgr.request.ListItemRequest;
import com.casemgr.request.OptionBlockRequest;
import com.casemgr.request.OrderTemplateCreateRequest;
import com.casemgr.request.OrderTemplateSearchRequest;
import com.casemgr.request.OrderTemplateSkipContractRequest;
import com.casemgr.request.OrderTemplateUpdateDeliveryRequest;
import com.casemgr.request.OrderTemplateUpdateDescRefRequest;
import com.casemgr.request.OrderTemplateUpdatePaymentRequest;
import com.casemgr.request.OrderTemplateUpdateStartingPriceRequest;
import com.casemgr.request.UrlRequest;
import com.casemgr.response.BlockResponse;
import com.casemgr.response.DiscountResponse;
import com.casemgr.response.OrderTemplateResponse;
import com.casemgr.service.BlockService;
import com.casemgr.service.DiscountService;
import com.casemgr.service.OrderTemplateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Order Template Management", description = "APIs for managing order templates including creation, configuration, blocks, discounts, and template lifecycle operations")
@RestController
@RequestMapping("/api/ordertemplates")
@CrossOrigin
@RequiredArgsConstructor
public class OrderTemplateController {
	private final OrderTemplateService orderTemplateService;
	private final DiscountService discountService;
	private final BlockService blockService;
	private final OrderTemplateRepository orderTemplateRepository;

	/**
	 * Get current user's order templates.
	 * @return List of user's order templates
	 */
	@GetMapping("/me")
	@Operation(
		summary = "Get My Order Templates",
		description = "Retrieve all order templates created by the current authenticated user. " +
					  "This includes template details, configuration, and current status."
	)
	public ResponseEntity<List<OrderTemplateResponse>> list() {
		return ResponseEntity.ok(orderTemplateService.listMyTemplate());
	}

	/**
	 * Get all order templates in the system (Admin only).
	 * @return List of all order templates
	 */
	@Secured({"ROLE_ADMIN"})
	@GetMapping
	@Operation(
		summary = "Get All Order Templates (Admin)",
		description = "Retrieve all order templates in the system for administrative purposes. " +
					  "This endpoint is restricted to administrators and provides comprehensive template information."
	)
	public ResponseEntity<List<OrderTemplateResponse>> listAll() {
		return ResponseEntity.ok(orderTemplateService.list());
	}
	
	/**
	 * Get order templates by provider user ID.
	 * @param uId Provider user ID
	 * @return List of provider's order templates
	 */
	@GetMapping("/provider/{uid}")
	@Operation(
		summary = "Get Provider's Order Templates",
		description = "Retrieve all order templates created by a specific service provider. " +
					  "This allows clients to browse available service templates from a particular provider."
	)
	public ResponseEntity<List<OrderTemplateResponse>> listByProvider(
		@Parameter(description = "Provider user ID to retrieve templates for", required = true)
		@PathVariable("uid") Long uId) {
		return ResponseEntity.ok(orderTemplateService.listProviderTemplate(uId));
	}

	/**
	 * Search order templates by name. (with POST)
	 * @param searchRequest Search request containing template name
	 * @return List of matching order templates
	 */
	@PostMapping("/search")
	@Operation(
		summary = "Search Order Templates by Name",
		description = "Search for order templates by name using partial matching. " +
					  "This allows users to find templates based on template names, including names with spaces."
	)
	public ResponseEntity<List<OrderTemplateResponse>> searchByName(
		@Parameter(description = "Search request containing template name", required = true)
		@RequestBody @Validated OrderTemplateSearchRequest searchRequest) {
		return ResponseEntity.ok(orderTemplateService.findByName(searchRequest.getName()));
	}

	/**
	 * Create a new order template.
	 * @param orderReq Order template creation request
	 * @return Created order template details
	 */
	@PostMapping("/{submenu}")
	@Operation(
		summary = "Create Order Template",
		description = "Create a new order template with the specified name. This creates a basic template structure " +
					  "that can be further configured with blocks, discounts, payment methods, and other settings."
	)
	public ResponseEntity<OrderTemplateResponse> create(@PathVariable String submenu){
		return ResponseEntity.ok(orderTemplateService.createTemplate(submenu));
	}

	/**
	 * Update order template image by uploading a file.
	 * @param oId Order template ID
	 * @param file Image file to upload
	 * @return Updated order template details
	 */
	@PatchMapping(value = "/{oId}/image", consumes = "multipart/*" , headers = "content-type=multipart/form-data")
	@Operation(
		summary = "Update Template Image",
		description = "Upload and set a new image for the order template. The image will be processed and stored " +
					  "for display purposes when clients browse available templates."
	)
	public ResponseEntity<OrderTemplateResponse> updateImage(
		@Parameter(description = "Order template ID to update image for", required = true)
		@PathVariable("oId") Long oId,
		@Parameter(description = "Image file to upload", required = true)
		@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(orderTemplateService.uploadTemplateImage(oId, file));
	}

	/**
	 * Update order template image by URL.
	 * @param oId Order template ID
	 * @param urlReq URL request containing image URL
	 * @return Updated order template details
	 */
	@PatchMapping(value = "/{oId}/imageurl")
	@Operation(
		summary = "Update Template Image by URL",
		description = "Set a new image for the order template using an external URL. The image will be fetched " +
					  "from the provided URL and associated with the template."
	) 
	public ResponseEntity<OrderTemplateResponse> updateImage(
		@Parameter(description = "Order template ID to update image for", required = true) @PathVariable("oId") Long oId,
		@Parameter(description = "URL request containing the image URL", required = true) @RequestBody UrlRequest urlReq) {
		return ResponseEntity.ok(orderTemplateService.uploadTemplateImage(oId, urlReq.getFileUrl()));
	}

	/**
	 * Update order template name.
	 * @param oId Order template ID
	 * @param orderRequest Request containing new template name
	 * @return Updated order template details
	 */
	@PatchMapping(value = "/{oId}/name")
	@Operation(
		summary = "Update Template Name",
		description = "Update the display name of an order template. This name will be visible to clients " +
					  "when browsing available service templates."
	) 
	public ResponseEntity<OrderTemplateResponse> updateName(
		@Parameter(description = "Order template ID to update name for", required = true)
		@PathVariable("oId") Long oId,
		@Parameter(description = "Request containing the new template name", required = true)
		@RequestBody @Validated OrderTemplateCreateRequest orderRequest) {
		return ResponseEntity.ok(orderTemplateService.updateTemplateName(oId, orderRequest.getName()));
	}
	
	/**
	 * Update order template payment method configuration.
	 * @param oId Order template ID
	 * @param paymentReq Payment method configuration request
	 * @return Updated order template details
	 */
	@PatchMapping(value = "/{oId}/paymentmethod")
	@Operation(
		summary = "Update Template Payment Method",
		description = "Configure payment method options for the order template. Available options include: " +
					  "FullPayment (100%), various 2-installment plans (10%-90%, 20%-80%, etc.), " +
					  "3-installment (30%-40%-30%), 4-installment (25% each), and 5-installment (20% each)."
	)
	public ResponseEntity<OrderTemplateResponse> updatePayment(
		@Parameter(description = "Order template ID to update payment method for", required = true) @PathVariable("oId") Long oId,
		@Parameter(description = "Payment method configuration request", required = true) @RequestBody OrderTemplateUpdatePaymentRequest paymentReq) {
		return ResponseEntity.ok(orderTemplateService.updateTemplatePayment(oId, paymentReq));
	}


	/**
	 * Update order template delivery type configuration.
	 * @param oId Order template ID
	 * @param deliveryDatesReq Delivery configuration request
	 * @return Updated order template details
	 */
	@PatchMapping(value = "/{oId}/deliverytype")
	@Operation(
		summary = "Update Template Delivery Configuration",
		description = "Configure delivery type and timeline settings for the order template. " +
					  "This defines how and when deliverables will be provided to clients."
	)
	public ResponseEntity<OrderTemplateResponse> updateDeliveryDate(
		@Parameter(description = "Order template ID to update delivery configuration for", required = true) @PathVariable("oId") Long oId,
		@Parameter(description = "Delivery configuration request containing delivery type and timeline", required = true) @RequestBody OrderTemplateUpdateDeliveryRequest deliveryDatesReq) {
		return ResponseEntity.ok(orderTemplateService.updateTemplateDelivery(oId, deliveryDatesReq));
	}

	/**
	 * Update order template description/reference requirement setting.
	 * @param oId Order template ID
	 * @param descRefReq Description/reference requirement request
	 * @return Updated order template details
	 */
	@PatchMapping(value = "/{oId}/hasdescref")
	@Operation(
		summary = "Update Template Description/Reference Requirement",
		description = "Configure whether the order template requires clients to provide descriptions or references " +
					  "when placing orders. This setting affects the order form presented to clients."
	)
	public ResponseEntity<OrderTemplateResponse> updateDescRef(
		@Parameter(description = "Order template ID to update description requirement for", required = true)
		@PathVariable("oId") Long oId,
		@Parameter(description = "Request specifying whether description/reference is required", required = true) @RequestBody OrderTemplateUpdateDescRefRequest descRefReq) {
		return ResponseEntity.ok(orderTemplateService.updateTemplateDescRef(oId, descRefReq.getHasDescRef()));
	}

	/** Update order template starting price.
	 * @param oId Order template ID
	 * @param startingPriceReq Starting price update request
	 * @return Updated order template details
	 */
	@PatchMapping(value = "/{oId}/startingprice")
	@Operation(
		summary = "Update Template Starting Price",
		description = "Set the base starting price for the order template. This price serves as the foundation " +
					  "for calculating total costs before applying discounts and additional options."
	)
	public ResponseEntity<OrderTemplateResponse> updateStartingPrice(
		@Parameter(description = "Order template ID to update starting price for", required = true) @PathVariable("oId") Long oId,
		@Parameter(description = "Starting price update request containing new price value", required = true) @RequestBody OrderTemplateUpdateStartingPriceRequest startingPriceReq) {
		return ResponseEntity.ok(orderTemplateService.updateTemplateStartingPrice(oId, startingPriceReq));
	}

	/**
	 * Add a discount to the order template.
	 * @param oId Order template ID
	 * @param discountReq Discount configuration request
	 * @return Updated order template details
	 */
	@PostMapping(value = "/{oId}/discount")
	@Operation(
		summary = "Add Template Discount",
		description = "Add a discount configuration to the order template. Supports both percentage-based and " +
					  "fixed amount discounts. Multiple discounts can be applied to a single template."
	)
	public ResponseEntity<OrderTemplateResponse> addDiscount(
		@Parameter(description = "Order template ID to add discount to", required = true) @PathVariable("oId") Long oId,
		@Parameter(description = "Discount configuration request (Percentage or Fixed type)", required = true) @RequestBody DiscountRequest discountReq) {
		return ResponseEntity.ok(orderTemplateService.addTemplateDiscount(oId, discountReq));
	}

	/**
	 * Update an existing discount in the order template.
	 * @param oId Order template ID
	 * @param dId Discount ID
	 * @param discountReq Updated discount configuration
	 * @return Updated discount details
	 */
	@PutMapping(value = "/{oId}/discounts/{dId}")
	@Operation(
		summary = "Update Template Discount",
		description = "Update an existing discount configuration within an order template. " +
					  "Supports modification of discount type, value, and conditions."
	)
	public ResponseEntity<DiscountResponse> updateDiscount(
		@Parameter(description = "Order template ID containing the discount", required = true)
		@PathVariable("oId") Long oId,
		@Parameter(description = "Discount ID to update", required = true) @PathVariable("dId") Long dId,
		@Parameter(description = "Updated discount configuration (Percentage or Fixed type)", required = true)
		@RequestBody DiscountRequest discountReq) {
		return ResponseEntity.ok(orderTemplateService.updateTemplateDiscount(oId, dId, discountReq));
	}
	
	
	/**
	 * Add an input block to the order template.
	 * @param oId Order template ID
	 * @param inputBlockReq Input block configuration request
	 * @return Updated order template details
	 */
	@PostMapping(value = "/{oId}/input")
	@Operation(
		summary = "Add Template Input Block",
		description = "Add an input block to the order template. Input blocks allow clients to provide " +
					  "custom information, requirements, or specifications when placing orders."
	)
	public ResponseEntity<OrderTemplateResponse> addInput(
		@Parameter(description = "Order template ID to add input block to", required = true) @PathVariable("oId") Long oId,
		@Parameter(description = "Input block configuration request", required = true) @RequestBody InputBlockRequest inputBlockReq) {
		return ResponseEntity.ok(orderTemplateService.addTemplateInput(oId, inputBlockReq));
	}
	
	/** Add a contract item to the order template.
	 * @param otId Order template ID
	 * @param inputBlockReq Contract item configuration request
	 * @return Updated order template details
	 */	
	@PostMapping(value = "/{otId}/contractitem")
	@Operation(
		summary = "Add Template Contract Item",
		description = "Add a contract item to the order template. Contract items define specific deliverables, " +
					  "terms, or conditions that will be included in the final contract."
	)
	public ResponseEntity<OrderTemplateResponse> addContractItem(
		@Parameter(description = "Order template ID to add contract item to", required = true) @PathVariable("otId") Long otId,
		@Parameter(description = "Contract item configuration request", required = true) @RequestBody InputBlockRequest inputBlockReq) {
		return ResponseEntity.ok(orderTemplateService.addTemplateContractItem(otId, inputBlockReq));
	}
	
	/**
	 * Update an existing contract item in the order template.
	 * @param bId Block ID of the contract item
	 * @param inputBlockReq Updated contract item configuration
	 * @return Updated block details
	 */
	@PutMapping(value = "/contractitem/{bid}")
	@Operation(
		summary = "Update Template Contract Item",
		description = "Update an existing contract item within an order template. This allows modification of " +
					  "contract terms, deliverables, and conditions."
	)
	public ResponseEntity<BlockResponse> updateContractItem(
		@Parameter(description = "Block ID of the contract item to update", required = true) @PathVariable("bid") Long bId, @Parameter(description = "Updated contract item configuration", required = true) @RequestBody InputBlockRequest inputBlockReq) {
		return ResponseEntity.ok(orderTemplateService.updateTemplateContractItem(bId, inputBlockReq));
	}

	/**
	 * Delete a contract item from the order template.
	 * @param bId Block ID of the contract item to delete
	 * @return Empty response
	 */
	@DeleteMapping(value = "/contractitem/{bid}")
	@Operation(
		summary = "Delete Template Contract Item",
		description = "Remove a contract item from the order template. This will permanently delete the " +
					  "contract item and its associated terms and conditions."
	)
	public ResponseEntity<Void> deleteContractItem(
		@Parameter(description = "Block ID of the contract item to delete", required = true) @PathVariable("bid") Long bId) {
		orderTemplateService.deleteTemplateContractItem(bId);
		return ResponseEntity.noContent().build();
	}
	
	/**
	 * Add an option block to the order template.
	 * @param oId Order template ID
	 * @param optionBlockReq Option block configuration request
	 * @return Updated order template details
	 */
	@PostMapping(value = "/{oId}/option")
	@Operation(
		summary = "Add Template Option Block",
		description = "Add an option block to the order template. Option blocks provide clients with " +
					  "selectable choices and additional services that can be added to their orders."
	)
	public ResponseEntity<OrderTemplateResponse> addOption(
		@Parameter(description = "Order template ID to add option block to", required = true) @PathVariable("oId") Long oId,
		@Parameter(description = "Option block configuration request", required = true) @RequestBody OptionBlockRequest optionBlockReq) {
		return ResponseEntity.ok(orderTemplateService.addTemplateOption(oId, optionBlockReq));
	}


	@PutMapping(value = "/input/{bId}")
	@Operation(summary = "Update input block",  description = "Update input block by Block ID" )
	public ResponseEntity<BlockResponse> updateInput(@PathVariable("bId") Long oId, @RequestBody InputBlockRequest inputBlockReq) {
		return ResponseEntity.ok(orderTemplateService.updateTemplateInput(oId, inputBlockReq));
	}
	
	@PutMapping(value = "/option/{bId}")
	@Operation(summary = "Update option block",  description = "Update option block by Block ID" )
	public ResponseEntity<BlockResponse> updateOption(@PathVariable("bId") Long bId, @RequestBody OptionBlockRequest optionBlockReq) {
		return ResponseEntity.ok(orderTemplateService.updateTemplateOption(bId, optionBlockReq));
	}
	
	@PostMapping(value = "/option/{bId}")
	@Operation(summary = "Add option list item",  description = "Add option list item by Block ID" )
	public ResponseEntity<BlockResponse> addBlockListItem(@PathVariable("bId") Long bId, @RequestBody ListItemRequest listItemRequest) {
		return ResponseEntity.ok(blockService.addListItem(bId, listItemRequest));
	}	
	@PutMapping(value = "/option/{bId}/listitems/{liId}")
	@Operation(summary = "Update list item",  description = "Update list item by Listitems ID" )
	public ResponseEntity<BlockResponse> updateListItem(@PathVariable("bId") Long bId, @PathVariable("liId") Long liId, @RequestBody ListItemRequest listItemRequest) {
		return ResponseEntity.ok(blockService.updateListItem(bId, liId, listItemRequest));
	}
	
	/**
	 * Get detailed information about an order template.
	 * @param oId Order template ID
	 * @return Order template details
	 */
	@GetMapping(value = "/{oId}")
	@Operation(
		summary = "Get Template Details",
		description = "Retrieve comprehensive details about a specific order template including configuration, " +
					  "blocks, discounts, payment methods, and all associated settings."
	)
	public ResponseEntity<OrderTemplateResponse> detail(
		@Parameter(description = "Order template ID to retrieve details for", required = true)
		@PathVariable("oId") Long oId) {
		return ResponseEntity.ok(orderTemplateService.templateDetail(oId));
	}


	/**
	 * Delete an order template.
	 * @param oId Order template ID
	 */
	@DeleteMapping(value = "/{oId}")
	@Operation(
		summary = "Delete Order Template",
		description = "Permanently delete an order template and all its associated configurations, blocks, and settings. " +
					  "This action cannot be undone and will affect any active orders using this template."
	)
	public void deleteOrderTemplate(
		@Parameter(description = "Order template ID to delete", required = true) @PathVariable("oId") Long oId) {
		orderTemplateService.deleteOrderTemplate(oId);
	}
	
	/**
	 * Configure contract skip setting for the order template.
	 * @param otId Order template ID
	 * @param skipReq Skip contract configuration request
	 */
	@PatchMapping(value = "/{otId}/skipcontract")
	@Operation(
		summary = "Configure Contract Skip Setting",
		description = "Configure whether clients can skip the contract phase when using this order template. " +
					  "This setting affects the order workflow and legal requirements."
	) 
	public void setSkipContract(
		@Parameter(description = "Order template ID to configure skip contract setting for", required = true) @PathVariable("otId") Long otId,
		@Parameter(description = "Skip contract configuration request", required = true) @RequestBody OrderTemplateSkipContractRequest skipReq) {
		orderTemplateService.setAllowSkipContract(otId, skipReq.getSkipContract());
	}
	
	/**
	 * Delete a discount from the system.
	 * @param dId Discount ID to delete
	 */
	@DeleteMapping(value = "/discounts/{dId}")
	@Operation(
		summary = "Delete Discount",
		description = "Permanently remove a discount configuration from the system. " +
					  "This will affect any templates or orders currently using this discount."
	)
	public void deleteDiscount(
		@Parameter(description = "Discount ID to delete", required = true) @PathVariable("dId") Long dId) {
		discountService.deleteDiscount(dId);
	}
	
	
	/**
	 * Delete a block from the order template.
	 * @param bId Block ID to delete
	 */
	@DeleteMapping(value = "/blocks/{bId}")
	@Operation(
		summary = "Delete Template Block",
		description = "Remove a block (input or option) from the order template. This will permanently delete " +
					  "the block and all its associated configurations and list items."
	)
	public void deleteBlock(
		@Parameter(description = "Block ID to delete (input or option block)", required = true) @PathVariable("bId") Long bId) {
		blockService.delete(bId);
	}

	/**
	 * Delete a list item from a block.
	 * @param liId List item ID to delete
	 */
	@DeleteMapping(value = "/listitems/{liId}")
	@Operation(
		summary = "Delete List Item",
		description = "Remove a specific list item from a block within the order template. " +
					  "This will permanently delete the list item and its associated data."
	)
	public void deleteListItem(
		@Parameter(description = "List item ID to delete", required = true) @PathVariable("liId") Long liId) {
		blockService.deleteListItem(liId);
	}
}
