package com.casemgr.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.enumtype.RevenueShareStatus;
import com.casemgr.request.RevenueShareStatusUpdateRequest;
import com.casemgr.response.RevenueShareResponse;
import com.casemgr.response.RevenueShareStatsResponse;
import com.casemgr.service.RevenueShareService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Revenue Share", description = "Revenue sharing management APIs for tracking and managing revenue distribution between clients and service providers.")
@RestController
@RequestMapping("/api/revenue-shares")
@RequiredArgsConstructor
@Slf4j
public class RevenueShareController {
    
    private final RevenueShareService revenueShareService;
    
    /**
     * Retrieve paginated list of revenue shares with optional filtering.
     * @param page Page number for pagination (0-based)
     * @param size Number of items per page
     * @param clientId Optional client ID filter
     * @param providerId Optional provider ID filter
     * @return Paginated list of revenue shares
     */
    @GetMapping
    @Operation(
        summary = "List Revenue Shares",
        description = "Retrieve a paginated list of revenue shares with optional filtering by client or provider. " +
                      "This endpoint allows tracking of revenue distribution across different transactions."
    )
    public ResponseEntity<Page<RevenueShareResponse>> listRevenueShares(
            @Parameter(description = "Page number for pagination (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Optional client ID to filter revenue shares")
            @RequestParam(required = false) Long clientId,
            @Parameter(description = "Optional provider ID to filter revenue shares")
            @RequestParam(required = false) Long providerId) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<RevenueShareResponse> result;
        
        if (clientId != null) {
            result = revenueShareService.listByClientId(clientId, pageable);
        } else if (providerId != null) {
            result = revenueShareService.listByProviderId(providerId, pageable);
        } else {
            result = revenueShareService.listAll(pageable);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * Retrieve a specific revenue share by ID.
     * @param id Revenue share ID
     * @return Revenue share details
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get Revenue Share by ID",
        description = "Retrieve detailed information about a specific revenue share record by its unique identifier."
    )
    public ResponseEntity<RevenueShareResponse> getRevenueShare(
            @Parameter(description = "Revenue share ID to retrieve", required = true)
            @PathVariable Long id) {
        RevenueShareResponse response = revenueShareService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve a revenue share by its unique number.
     * @param revenueShareNo Revenue share number
     * @return Revenue share details
     */
    @GetMapping("/number/{revenueShareNo}")
    @Operation(
        summary = "Get Revenue Share by Number",
        description = "Retrieve revenue share information using its unique revenue share number identifier."
    )
    public ResponseEntity<RevenueShareResponse> getRevenueShareByNo(
            @Parameter(description = "Revenue share number to retrieve", required = true)
            @PathVariable String revenueShareNo) {
        RevenueShareResponse response = revenueShareService.getByRevenueShareNo(revenueShareNo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve all revenue shares associated with a specific order.
     * @param orderId Order ID to retrieve revenue shares for
     * @return List of revenue shares for the order
     */
    @GetMapping("/order/{orderId}")
    @Operation(
        summary = "Get Revenue Shares by Order ID",
        description = "Retrieve all revenue share records associated with a specific order. " +
                      "An order may have multiple revenue shares for different participants."
    )
    public ResponseEntity<List<RevenueShareResponse>> getRevenueShareByOrderId(
            @Parameter(description = "Order ID to retrieve revenue shares for", required = true)
            @PathVariable Long orderId) {
        List<RevenueShareResponse> responses = revenueShareService.listByOrderId(orderId);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Update the payment status of a revenue share.
     * @param id Revenue share ID to update
     * @param request Status update request with new status and optional payment time
     * @return Updated revenue share details
     */
    @PutMapping("/{id}/status")
    @Operation(
        summary = "Update Revenue Share Status",
        description = "Update the payment status of a revenue share record. When marking as paid, " +
                      "the payment time will be automatically set to current time if not provided."
    )
    public ResponseEntity<RevenueShareResponse> updateStatus(
            @Parameter(description = "Revenue share ID to update status for", required = true)
            @PathVariable Long id,
            @Parameter(description = "Status update request with new status and optional payment time", required = true)
            @Valid @RequestBody RevenueShareStatusUpdateRequest request) {
        
        LocalDateTime paymentTime = request.getPaymentTime();
        if (request.getStatus() == RevenueShareStatus.Paid && paymentTime == null) {
            paymentTime = LocalDateTime.now();
        }
        
        RevenueShareResponse response = revenueShareService.updateStatus(id, request.getStatus(), paymentTime);
        
        log.info("Revenue Share status updated. ID: {}, New status: {}", id, request.getStatus());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve revenue share statistics with optional filtering.
     * @param clientId Optional client ID to filter statistics
     * @param providerId Optional provider ID to filter statistics
     * @return Revenue share statistics
     */
    @GetMapping("/stats")
    @Operation(
        summary = "Get Revenue Share Statistics",
        description = "Retrieve comprehensive statistics about revenue shares including totals, averages, " +
                      "and status breakdowns. Can be filtered by client or provider for specific insights."
    )
    public ResponseEntity<RevenueShareStatsResponse> getStats(
            @Parameter(description = "Optional client ID to filter statistics")
            @RequestParam(required = false) Long clientId,
            @Parameter(description = "Optional provider ID to filter statistics")
            @RequestParam(required = false) Long providerId) {
        
        RevenueShareStatsResponse stats;
        
        if (clientId != null) {
            stats = revenueShareService.getStatsByClient(clientId);
        } else if (providerId != null) {
            stats = revenueShareService.getStatsByProvider(providerId);
        } else {
            stats = revenueShareService.getOverallStats();
        }
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Retrieve all revenue shares for a specific client (non-paginated).
     * @param clientId Client ID to retrieve revenue shares for
     * @return List of revenue shares for the client
     */
    @GetMapping("/client/{clientId}")
    @Operation(
        summary = "Get Revenue Shares by Client",
        description = "Retrieve all revenue share records for a specific client without pagination. " +
                      "This provides a complete view of all revenue distributions for the client."
    )
    public ResponseEntity<List<RevenueShareResponse>> getRevenueSharesByClient(
            @Parameter(description = "Client ID to retrieve revenue shares for", required = true)
            @PathVariable Long clientId) {
        List<RevenueShareResponse> responses = revenueShareService.listByClientId(clientId);
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Retrieve all revenue shares for a specific provider (non-paginated).
     * @param providerId Provider ID to retrieve revenue shares for
     * @return List of revenue shares for the provider
     */
    @GetMapping("/provider/{providerId}")
    @Operation(
        summary = "Get Revenue Shares by Provider",
        description = "Retrieve all revenue share records for a specific service provider without pagination. " +
                      "This provides a complete view of all revenue distributions for the provider."
    )
    public ResponseEntity<List<RevenueShareResponse>> getRevenueSharesByProvider(
            @Parameter(description = "Provider ID to retrieve revenue shares for", required = true)
            @PathVariable Long providerId) {
        List<RevenueShareResponse> responses = revenueShareService.listByProviderId(providerId);
        return ResponseEntity.ok(responses);
    }
}