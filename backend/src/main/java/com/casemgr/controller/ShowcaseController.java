package com.casemgr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.entity.Industry;
import com.casemgr.entity.Showcase;
import com.casemgr.repository.IndustryRepository;
import com.casemgr.repository.ShowcaseRepository;
import com.casemgr.request.ShowcaseRequest;
import com.casemgr.response.FiledataResponse;
import com.casemgr.response.IntroResponse;
import com.casemgr.response.ShowcaseResponse;
import com.casemgr.service.impl.ShowcaseServiceImpl;
import com.casemgr.service.impl.UserServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Showcase", description = "Showcase management APIs for service providers to create, manage, and display their work portfolios and project examples.")
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping({ "/api/showcase" })
@Slf4j
public class ShowcaseController {
	private final ShowcaseServiceImpl showcaseService;
	private final UserServiceImpl userService;
	
	/**
	 * Create a new showcase for the authenticated user.
	 * @param showcaseRequest Showcase creation request with showcase details
	 * @return Created showcase details
	 */
	@PostMapping
	@Operation(
		summary = "Create Showcase",
		description = "Create a new showcase to display work portfolio and project examples. " +
					  "Showcases help providers demonstrate their capabilities to potential clients."
	)
	public ResponseEntity<ShowcaseResponse> createShowcase(
		@Parameter(description = "Showcase creation request with showcase details", required = true)
		ShowcaseRequest showcaseRequest) {
		return ResponseEntity.ok(showcaseService.createShowcase(showcaseRequest));
	}
	
	/**
	 * Upload a new file to an existing showcase.
	 * @param sId Showcase ID to upload file to
	 * @param file File to upload to the showcase
	 * @return File data response with upload details
	 */
	@PostMapping(value = "/{sId}", consumes = "multipart/*" , headers = "content-type=multipart/form-data")
	@Operation(
		summary = "Upload Showcase File",
		description = "Upload a new file (image, document, etc.) to an existing showcase. " +
					  "This adds visual content to demonstrate work quality and capabilities."
	)
	public ResponseEntity<FiledataResponse> uploadNewFile(
		@Parameter(description = "Showcase ID to upload file to", required = true)
		@PathVariable("sId") Long sId,
		@Parameter(description = "File to upload to the showcase", required = true)
		@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(showcaseService.addFile(sId, file));
	}
	
	/**
	 * Replace an existing file in a showcase.
	 * @param fId File data ID to replace
	 * @param file New file to replace the existing one
	 * @return New file data response
	 */
	@PostMapping(value = "/filedata/{fId}/change", consumes = "multipart/*" , headers = "content-type=multipart/form-data")
	@Operation(
		summary = "Replace Showcase File",
		description = "Replace an existing file in a showcase with a new one. The old file will be deleted " +
					  "and replaced with the new file. Returns the new file data information."
	)
	public ResponseEntity<FiledataResponse> changeFile(
		@Parameter(description = "File data ID to replace", required = true)
		@PathVariable("fId") Long fId,
		@Parameter(description = "New file to replace the existing one", required = true)
		@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(showcaseService.changeFile(fId, file));
	}
	
	/**
	 * Link a showcase to an order template.
	 * @param sId Showcase ID to link
	 * @param oId Order template ID to link to
	 * @return Updated showcase details
	 */
	@PutMapping("/{sId}/{oId}")
	@Operation(
		summary = "Link Showcase to Order Template",
		description = "Associate a showcase with an order template to categorize the showcase by service type. " +
					  "Order template list can be retrieved from /api/ordertemplates."
	)
	public ResponseEntity<ShowcaseResponse> setCategory(
		@Parameter(description = "Showcase ID to link", required = true)
		@PathVariable("sId") Long sId,
		@Parameter(description = "Order template ID to link to", required = true)
		@PathVariable("oId") Long oId) {
		return ResponseEntity.ok(showcaseService.addOrderTemplate(sId, oId));
	}
	
	/**
	 * Retrieve all showcases created by the authenticated user.
	 * @return List of current user's showcases
	 */
	@GetMapping("/me")
	@Operation(
		summary = "Get My Showcases",
		description = "Retrieve all showcases created by the currently authenticated user. " +
					  "This allows providers to view and manage their own portfolio displays."
	)
	public ResponseEntity<List<ShowcaseResponse>> getMyShowcase(){
		return ResponseEntity.ok(showcaseService.getMyShowcase());
	}
	
	/**
	 * Retrieve all showcases created by a specific provider.
	 * @param uId Provider's user ID
	 * @return List of showcases created by the provider
	 */
	@GetMapping("/provider/{uId}")
	@Operation(
		summary = "Get Provider's Showcases",
		description = "Retrieve all showcases created by a specific service provider. " +
					  "This allows clients to browse a provider's portfolio and work examples."
	)
	public ResponseEntity<List<ShowcaseResponse>> getProviderShowcase(
		@Parameter(description = "Provider's user ID to retrieve showcases for", required = true)
		@PathVariable("uId") Long uId){
		return ResponseEntity.ok(showcaseService.getProviderShowcase(uId));
	}

}
