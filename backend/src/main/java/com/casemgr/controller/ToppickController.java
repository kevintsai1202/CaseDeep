package com.casemgr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.response.ToppickResponse;
import com.casemgr.service.ToppickService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Top Pick Management", description = "APIs for managing and retrieving top pick service providers based on various criteria including industry, location, and order templates")
@RestController
@RequestMapping("/api/toppicks")
@CrossOrigin
@RequiredArgsConstructor
public class ToppickController {
	private final ToppickService toppickService;

	@GetMapping("/{industry}")
	@Operation(
		summary = "Get top pick service providers by industry",
		description = "Retrieves a list of top-rated service providers filtered by industry category. " +
					  "This endpoint returns the most recommended providers in a specific industry based on ratings, performance, and other quality metrics."
	)
	public ResponseEntity<List<ToppickResponse>> toppickListByIndustry(
		@Parameter(description = "Industry category to filter top picks (e.g., 'technology', 'marketing', 'design')", required = true)
		@PathVariable("industry") String industry
	) {
		return ResponseEntity.ok(toppickService.getToppicByIndustry(industry));
	}
	
	@GetMapping("/{industry}/{ordertemplatename}")
	@Operation(
		summary = "Get top pick service providers by industry and order template",
		description = "Retrieves a list of top-rated service providers filtered by both industry category and specific order template. " +
					  "This endpoint helps find the best providers for specific service types within an industry."
	)
	public ResponseEntity<List<ToppickResponse>> toppickListByIndustryAndTemplate(
		@Parameter(description = "Industry category to filter top picks", required = true)
		@PathVariable("industry") String industry,
		@Parameter(description = "Order template name to filter providers by specific service type", required = true)
		@PathVariable("ordertemplatename") String ordertemplatename
	) {
		return ResponseEntity.ok(toppickService.getToppicByIndustryAndTemplateName(industry, ordertemplatename));
	}
	
	@GetMapping("/{industry}/locations/{location}")
	@Operation(
		summary = "Get top pick service providers by industry and location",
		description = "Retrieves a list of top-rated service providers filtered by industry category and geographical location. " +
					  "This endpoint helps find the best local providers in a specific industry."
	)
	public ResponseEntity<List<ToppickResponse>> toppickListByIndustryAndLocation(
		@Parameter(description = "Industry category to filter top picks", required = true)
		@PathVariable("industry") String industry,
		@Parameter(description = "Location/region to filter providers by geographical area", required = true)
		@PathVariable("location") String location
	) {
		return ResponseEntity.ok(toppickService.getToppicByIndustryAndLocation(industry, location));
	}
	
	@GetMapping("/{industry}/{ordertemplatename}/locations/{location}")
	@Operation(
		summary = "Get top pick service providers by industry, order template, and location",
		description = "Retrieves a list of top-rated service providers filtered by industry category, specific order template, and geographical location. " +
					  "This endpoint provides the most targeted search for finding the best local providers for specific services within an industry."
	)
	public ResponseEntity<List<ToppickResponse>> toppickListByIndustryTemplateAndLocation(
		@Parameter(description = "Industry category to filter top picks", required = true)
		@PathVariable("industry") String industry,
		@Parameter(description = "Order template name to filter providers by specific service type", required = true)
		@PathVariable("ordertemplatename") String ordertemplatename,
		@Parameter(description = "Location/region to filter providers by geographical area", required = true)
		@PathVariable("location") String location
	) {
		return ResponseEntity.ok(toppickService.getToppicByIndustryTemplateNameAndLocation(industry, ordertemplatename, location));
	}
	

	
	
}
