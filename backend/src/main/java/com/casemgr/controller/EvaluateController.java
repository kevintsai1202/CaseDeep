package com.casemgr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.entity.Evaluate;
import com.casemgr.exception.BusinessException;
import com.casemgr.request.EvaluateRequest;
import com.casemgr.response.ClientEvaluateSummary;
import com.casemgr.response.ProviderEvaluateSummary;
import com.casemgr.service.impl.EvaluateServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Tag(name = "Evaluation Management", description = "APIs for managing project evaluations, ratings, and feedback between clients and service providers")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/evaluates")
@CrossOrigin
public class EvaluateController {
	private final EvaluateServiceImpl evaluateService;
	/**
	 * Get client evaluation for a specific project by order ID.
	 * @param oId Order ID
	 * @param item Evaluation item type
	 * @return Client evaluation details
	 */
	@GetMapping("/orders/{oId}/client/{item}")
	@Operation(
		summary = "Get Client Evaluation by Order ID",
		description = "Retrieve client evaluation for a specific project using order ID. " +
					  "Evaluation items: 0=Responsibility, 1=Principles, 2=Helpfulness"
	)
	public ResponseEntity<Evaluate> getClientEvaluateByProject(
		@Parameter(description = "Order ID to retrieve evaluation for", required = true)
		@PathVariable("oId") Long oId,
		@Parameter(description = "Evaluation item type: 0=Responsibility, 1=Principles, 2=Helpfulness", required = true)
		@PathVariable("item") Integer item) {
		Evaluate evaluate = evaluateService.getClientEvaluateByOrder(oId, item);
		return ResponseEntity.ok(evaluate);
	}
	
	/**
	 * Get client evaluation for a specific project by order number.
	 * @param base62no Base62 encoded order number
	 * @param item Evaluation item type
	 * @return Client evaluation details
	 */
	@GetMapping("/orders/{base62no}/client/{item}")
	@Operation(
		summary = "Get Client Evaluation by Order Number",
		description = "Retrieve client evaluation for a specific project using base62 encoded order number. " +
					  "Evaluation items: 0=Responsibility, 1=Principles, 2=Helpfulness"
	)
	public ResponseEntity<Evaluate> getClientEvaluateByOrder(
		@Parameter(description = "Base62 encoded order number", required = true)
		@PathVariable("base62no") String base62no,
		@Parameter(description = "Evaluation item type: 0=Responsibility, 1=Principles, 2=Helpfulness", required = true)
		@PathVariable("item") Integer item) {
		Evaluate evaluate = evaluateService.getClientEvaluateByOrder(base62no, item);
		return ResponseEntity.ok(evaluate);
	}
	
	/**
	 * Get provider evaluations for a specific project.
	 * @param base62no Base62 encoded order number
	 * @return List of provider evaluations
	 */
	@GetMapping("/orders/{base62no}/provider")
	@Operation(
		summary = "Get Provider Evaluations by Order",
		description = "Retrieve all provider evaluations for a specific project using base62 encoded order number. " +
					  "Returns comprehensive evaluation data from the service provider's perspective."
	)
	public ResponseEntity<List<Evaluate>> getProviderEvaluateByOrder(
		@Parameter(description = "Base62 encoded order number", required = true)
		@PathVariable("base62no") String base62no) {
		return ResponseEntity.ok(evaluateService.getProviderEvaluateByOrder(base62no));
	}
	
	/**
	 * Get all client evaluations for a specific project.
	 * @param base62no Base62 encoded order number
	 * @return List of client evaluations
	 */
	@GetMapping("/orders/{base62no}/client")
	@Operation(
		summary = "Get All Client Evaluations by Order",
		description = "Retrieve all client evaluations for a specific project using base62 encoded order number. " +
					  "Returns comprehensive evaluation data from the client's perspective."
	)
	public ResponseEntity<List<Evaluate>> getClientProviderEvaluateByOrder(
		@Parameter(description = "Base62 encoded order number", required = true)
		@PathVariable("base62no") String base62no) {
		return ResponseEntity.ok(evaluateService.getClientEvaluateByOrder(base62no));
	}
	
	/**
	 * Get provider evaluation for a specific project by order ID.
	 * @param oId Order ID
	 * @param item Evaluation item type
	 * @return Provider evaluation details
	 */
	@GetMapping("/orders/{oId}/provider/{item}")
	@Operation(
		summary = "Get Provider Evaluation by Order ID",
		description = "Retrieve provider evaluation for a specific project using order ID. " +
					  "Evaluation items: 0=Responsibility, 1=Professionalism, 2=Cooperation"
	)
	public ResponseEntity<Evaluate> getProviderEvaluateByOrder(
		@Parameter(description = "Order ID to retrieve evaluation for", required = true)
		@PathVariable("oId") Long oId,
		@Parameter(description = "Evaluation item type: 0=Responsibility, 1=Professionalism, 2=Cooperation", required = true)
		@PathVariable("item") Integer item) {
		Evaluate evaluate = evaluateService.getProviderEvaluateByOrder(oId, item);
		return ResponseEntity.ok(evaluate);
	}
	
	/**
	 * Get provider evaluation summary for a specific user.
	 * @param uId User ID
	 * @return Provider evaluation summary
	 */
	@GetMapping("/users/{uId}/provider")
	@Operation(
		summary = "Get User's Provider Evaluation Summary",
		description = "Retrieve comprehensive provider evaluation summary for a specific user, " +
					  "including average ratings, total evaluations, and performance metrics across all projects."
	)
	public ResponseEntity<ProviderEvaluateSummary> getProviderEvaluateByUser(
		@Parameter(description = "User ID to retrieve provider evaluation summary for", required = true)
		@PathVariable("uId") Long uId) {
		return ResponseEntity.ok(evaluateService.getProviderEvaluateSummaryByUser(uId));
	}
	
	/**
	 * Get client evaluation summary for a specific user.
	 * @param uId User ID
	 * @return Client evaluation summary
	 */
	@GetMapping("/users/{uId}/client")
	@Operation(
		summary = "Get User's Client Evaluation Summary",
		description = "Retrieve comprehensive client evaluation summary for a specific user, " +
					  "including average ratings, total evaluations, and performance metrics across all projects."
	)
	public ResponseEntity<ClientEvaluateSummary> getClientEvaluateByUser(
		@Parameter(description = "User ID to retrieve client evaluation summary for", required = true)
		@PathVariable("uId") Long uId) {
		return ResponseEntity.ok(evaluateService.getClientEvaluateSummaryByUser(uId));
	}
	
//	/*
//	 * Get user's Evaluate summary
//	 * @return EvaluateSummary
//	 */
//	@GetMapping("/user/{uId}")
//	@Operation(summary = "取得User的綜合評價", description = "取得User的綜合評價")
//	public ResponseEntity<ClientEvaluateSummary> getEvaluateSummaryByUser(@PathVariable("uId") Long uId) {
//		return ResponseEntity.ok(evaluateService.getClientEvaluateSummaryByUser(uId));
//	}
	
	/**
	 * Submit client evaluation for a project.
	 * @param base62no Base62 encoded order number
	 * @param evaluateRequest Evaluation request containing rating and comments
	 * @return Created evaluation
	 */
	@PostMapping("/orders/{base62no}/client")
	@Operation(
		summary = "Submit Client Evaluation",
		description = "Submit a client evaluation for a specific project. This allows clients to rate and provide feedback " +
					  "on the service provider's performance including responsibility, professionalism, and cooperation."
	)
	public ResponseEntity<Evaluate> setClientEvaluateByOrder(
		@Parameter(description = "Base62 encoded order number", required = true)
		@PathVariable("base62no") String base62no,
		@Parameter(description = "Evaluation request containing item type, rating, and comments", required = true)
		@RequestBody EvaluateRequest evaluateRequest) throws EntityNotFoundException, EntityExistsException, BusinessException {
		
		return ResponseEntity.ok(evaluateService.setClientEvaluate(base62no,evaluateRequest.getItem(),  evaluateRequest.getRate(), evaluateRequest.getComment()));
	}
	
	/**
	 * Submit provider evaluation for a project.
	 * @param base62no Base62 encoded order number
	 * @param evaluateRequest Evaluation request containing rating and comments
	 * @return Created evaluation
	 */
	@PostMapping("/orders/{base62no}/provider")
	@Operation(
		summary = "Submit Provider Evaluation",
		description = "Submit a provider evaluation for a specific project. This allows service providers to rate and provide feedback " +
					  "on the client's performance including responsibility, principles, and helpfulness."
	)
	public ResponseEntity<Evaluate> setProviderEvaluateByOrder(
		@Parameter(description = "Base62 encoded order number", required = true)
		@PathVariable("base62no") String base62no,
		@Parameter(description = "Evaluation request containing item type, rating, and comments", required = true)
		@RequestBody EvaluateRequest evaluateRequest) throws EntityNotFoundException, EntityExistsException, BusinessException {
		return ResponseEntity.ok(evaluateService.setProviderEvaluate(base62no,evaluateRequest.getItem(),  evaluateRequest.getRate(), evaluateRequest.getComment()));
	}
	
	/**
	 * Submit provider evaluation for a project by order ID.
	 * @param oId Order ID
	 * @param evaluateRequest Evaluation request containing rating and comments
	 * @return Created evaluation
	 */
	@PostMapping("/orders/{oId}/provider")
	@Operation(
		summary = "Submit Provider Evaluation by Order ID",
		description = "Submit a provider evaluation for a specific project using order ID. This allows service providers to rate and provide feedback " +
					  "on the client's performance including responsibility, principles, and helpfulness."
	)
	public ResponseEntity<Evaluate> setProviderEvaluateByOrder(
		@Parameter(description = "Order ID for the project", required = true)
		@PathVariable("oId") Long oId,
		@Parameter(description = "Evaluation request containing item type, rating, and comments", required = true)
		@RequestBody EvaluateRequest evaluateRequest) throws EntityNotFoundException, EntityExistsException, BusinessException {
		return ResponseEntity.ok(evaluateService.setProviderEvaluate(oId, evaluateRequest.getItem() ,evaluateRequest.getRate(), evaluateRequest.getComment()));
	}
	
	/**
	 * Update an existing evaluation.
	 * @param eId Evaluation ID
	 * @param rate Updated rating
	 * @param comment Updated comment
	 * @return Updated evaluation
	 */
	@PutMapping("/{eId}")
	@Operation(
		summary = "Update Evaluation",
		description = "Update an existing evaluation's rating and comments. This allows users to modify their previously submitted evaluations " +
					  "within the allowed time frame and business rules."
	)
	public ResponseEntity<Evaluate> updateEvaluate(
		@Parameter(description = "Evaluation ID to update", required = true)
		@PathVariable("eId") Long eId,
		@Parameter(description = "Updated rating value", required = true)
		Integer rate,
		@Parameter(description = "Updated comment text")
		String comment) throws EntityNotFoundException, BusinessException {
		return ResponseEntity.ok(evaluateService.updateEvaluate(eId, rate, comment));
	}
	
}
