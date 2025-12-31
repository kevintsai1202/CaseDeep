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
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.request.PriceListItemRequest;
import com.casemgr.request.PricePackageAddOrderRequest;
import com.casemgr.request.PricePackageCreateRequest;
import com.casemgr.response.PriceListItemResponse;
import com.casemgr.response.PricePackageResponse;
import com.casemgr.service.impl.PricePackageServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Price Package", description = "Price package management APIs for service providers to create, manage, and configure pricing packages for their services.")
@RestController
@RequestMapping("/api/pricepackages")
@CrossOrigin
@RequiredArgsConstructor
public class PricePackageController {
	private final PricePackageServiceImpl pricePackageService;
	/**
	 * Retrieve all price packages created by a specific provider.
	 * @param userId Provider's user ID
	 * @return List of price packages created by the provider
	 */
	@GetMapping("/provider/{userId}")
	@Operation(
		summary = "Get Provider's Price Packages",
		description = "Retrieve all price packages created by a specific service provider. " +
					  "This allows clients to browse available pricing options from a particular provider."
	)
	public ResponseEntity<List<PricePackageResponse>> getPriciesByProvider(
		@Parameter(description = "Provider's user ID to retrieve price packages for", required = true)
		@PathVariable("userId") Long userId){
		return ResponseEntity.ok(pricePackageService.getProviderPricies(userId));
	}
	
	/**
	 * Retrieve all price packages created by the current authenticated user.
	 * @return List of current user's price packages
	 */
	@GetMapping("/me")
	@Operation(
		summary = "Get My Price Packages",
		description = "Retrieve all price packages created by the currently authenticated provider. " +
					  "This endpoint allows providers to view and manage their own pricing configurations."
	)
	public ResponseEntity<List<PricePackageResponse>> getMyPricies(){
		return ResponseEntity.ok(pricePackageService.getMyPricies());
	}
	
	/**
	 * Create a new price package for the authenticated provider.
	 * @param req Price package creation request with package details
	 * @return Created price package details
	 */
	@PostMapping
	@Operation(
		summary = "Create Price Package",
		description = "Create a new price package for the authenticated service provider. " +
					  "The package includes pricing tiers, service descriptions, and configuration options. " +
					  "Note: File uploads must be done separately after package creation."
	)
	public ResponseEntity<PricePackageResponse> create(
		@Parameter(description = "Price package creation request with package details", required = true)
		@RequestBody @Validated PricePackageCreateRequest req){
		return ResponseEntity.ok(pricePackageService.createPricePackage(req));
	}
	
	/**
	 * Upload an image file for a price package.
	 * @param priceId Price package ID to upload image for
	 * @param file Image file to upload
	 * @return Updated price package with image information
	 */
	@PutMapping(value = "/image/{priceId}", consumes = "multipart/*" , headers = "content-type=multipart/form-data")
	@Operation(
		summary = "Upload Price Package Image",
		description = "Upload an image file for a specific price package. This image will be displayed " +
					  "to clients when browsing available pricing options. Supports common image formats."
	)
	public ResponseEntity<PricePackageResponse> updateImage(
		@Parameter(description = "Price package ID to upload image for", required = true)
		@PathVariable("priceId") Long priceId,
		@Parameter(description = "Image file to upload for the price package", required = true)
		@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(pricePackageService.uploadFile(priceId, file));
	}
	
	/**
	 * Update a specific item within a price package.
	 * @param itemId Price list item ID to update
	 * @param itemReq Updated item details
	 * @return Updated price list item details
	 */
	@PutMapping(value = "/itemlists/{itemId}")
	@Operation(
		summary = "Update Price Package Item",
		description = "Update a specific item within a price package. This allows modification of " +
					  "individual pricing tiers, service descriptions, or item configurations within the package."
	)
	public ResponseEntity<PriceListItemResponse> updateItem(
		@Parameter(description = "Price list item ID to update", required = true)
		@PathVariable("itemId") Long itemId,
		@Parameter(description = "Updated item details and configuration", required = true)
		@RequestBody PriceListItemRequest itemReq) {
		return ResponseEntity.ok(pricePackageService.updateListItem(itemId, itemReq));
	}
	
	/**
	 * Delete a price package permanently.
	 * @param priceId Price package ID to delete
	 * @return Empty response
	 */
	@DeleteMapping(value = "/{priceId}")
	@Operation(
		summary = "Delete Price Package",
		description = "Permanently delete a price package and all its associated items. " +
					  "This action cannot be undone and will remove the package from client visibility."
	)
	public ResponseEntity deletePrice(
		@Parameter(description = "Price package ID to delete", required = true)
		@PathVariable("priceId") Long priceId) {
		pricePackageService.deletePricePackage(priceId);
		return ResponseEntity.noContent().build();
	}
	
	/**
	 * Delete a specific item from a price package.
	 * @param itemId Price list item ID to delete
	 * @return Empty response
	 */
	@DeleteMapping(value = "/itemlists/{itemId}")
	@Operation(
		summary = "Delete Price Package Item",
		description = "Remove a specific item from a price package. This will permanently delete " +
					  "the pricing tier or service option from the package configuration."
	)
	public ResponseEntity deleteItem(
		@Parameter(description = "Price list item ID to delete", required = true)
		@PathVariable("itemId") Long itemId) {
		pricePackageService.deleteListItem(itemId);
		return ResponseEntity.noContent().build();
	}
	
	/**
	 * Add a new item to an existing price package.
	 * @param priceId Price package ID to add item to
	 * @param itemReq New item details and configuration
	 * @return Created price list item details
	 */
	@PostMapping(value = "/itemlists/{priceId}")
	@Operation(
		summary = "Add Price Package Item",
		description = "Add a new pricing tier or service option to an existing price package. " +
					  "This allows providers to expand their pricing options with additional tiers or services."
	)
	public ResponseEntity<PriceListItemResponse> addItem(
		@Parameter(description = "Price package ID to add new item to", required = true)
		@PathVariable("priceId") Long priceId,
		@Parameter(description = "New item details and configuration", required = true)
		@RequestBody PriceListItemRequest itemReq) {
		return ResponseEntity.ok(pricePackageService.addListItem(priceId, itemReq));
	}
	
	/**
	 * Link a price package to an order template.
	 * @param priceId Price package ID to link
	 * @param priceReq Request containing order template ID for linking
	 * @return Updated price package with template association
	 */
	@PutMapping(value = "/{priceId}")
	@Operation(
		summary = "Link Price Package to Order Template",
		description = "Associate a price package with an order template to enable automated pricing " +
					  "for orders created from that template. Order template list can be retrieved from /api/ordertemplates."
	)
	public ResponseEntity<PricePackageResponse> linkToOrderTemplate(
		@Parameter(description = "Price package ID to link to order template", required = true)
		@PathVariable("priceId") Long priceId,
		@Parameter(description = "Request containing order template ID for linking", required = true)
		@RequestBody PricePackageAddOrderRequest priceReq) {
		return ResponseEntity.ok(pricePackageService.addOrderTemplate(priceId, priceReq.getOId()));
	}
	
}
