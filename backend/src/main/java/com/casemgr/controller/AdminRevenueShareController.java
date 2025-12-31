package com.casemgr.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.request.RevenueShareUpdateRequest;
import com.casemgr.response.RevenueShareResponse;
import com.casemgr.service.RevenueShareService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Admin Revenue Share Management", description = "Administrative APIs for managing revenue sharing records, payments, and status updates")
@RestController
@RequestMapping("/api/admin/revenue-shares")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminRevenueShareController {
    
    private final RevenueShareService revenueShareService;
    
    /**
     * Admin query all Revenue Share records
     */
    @Operation(
        summary = "List All Revenue Share Records",
        description = "Retrieve a paginated list of all revenue sharing records in the system. " +
                      "This endpoint provides comprehensive revenue share data for administrative oversight, payment processing, and financial reporting."
    )
    @GetMapping
    public ResponseEntity<Page<RevenueShareResponse>> listAllRevenueShares(
        @Parameter(description = "Pagination parameters including page number, size, and sorting options")
        Pageable pageable) {
        Page<RevenueShareResponse> result = revenueShareService.listAll(pageable);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Admin update Revenue Share record
     */
    @Operation(
        summary = "Update Revenue Share Record",
        description = "Update the status and payment information of a specific revenue share record. " +
                      "This endpoint allows administrators to process payments, update status, and manage revenue distribution."
    )
    @PutMapping("/{id}")
    public ResponseEntity<RevenueShareResponse> updateRevenueShare(
            @Parameter(description = "Unique identifier of the revenue share record to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Update request containing new status and payment information", required = true)
            @RequestBody RevenueShareUpdateRequest request) {
        
        RevenueShareResponse response = revenueShareService.updateStatus(
            id, 
            request.getStatus(), 
            request.getPaymentTime()
        );
        
        log.info("Admin updated Revenue Share. ID: {}, Status: {}, Payment Time: {}", 
            id, request.getStatus(), request.getPaymentTime());
        
        return ResponseEntity.ok(response);
    }
}