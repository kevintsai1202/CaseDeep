package com.casemgr.controller;

import java.util.Map; // Added import

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.enumtype.PaymentStatus; // Use the correct PaymentStatus enum
import com.casemgr.response.CommissionResponse;
import com.casemgr.service.CommissionService; // Use interface

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Admin Commission Management", description = "Administrative APIs for managing commission payments, status updates, and financial processing")
@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/api/admin/commissions"}) // Base path for admin commission operations
public class AdminCommissionController {

    private final CommissionService commissionService;

    @Secured("ROLE_COMMISSION_MANAGE")
    @Operation(
        summary = "List Commissions for Admin Review",
        description = "Retrieve a paginated list of commission records filtered by the administrator's region. " +
                      "This endpoint provides commission data for payment processing, status tracking, and financial management."
    )
    @GetMapping
    public ResponseEntity<Page<CommissionResponse>> listCommissionsForAdmin(
        @Parameter(description = "Pagination parameters including page number, size, and sorting options")
        Pageable pageable) {
        // Service method already implements region filtering based on logged-in admin
        Page<CommissionResponse> commissionPage = commissionService.listCommissionsForAdmin(pageable);
        return ResponseEntity.ok(commissionPage);
    }

    @Secured("ROLE_COMMISSION_MANAGE")
    @Operation(
        summary = "Update Commission Payment Status",
        description = "Update the payment status of a specific commission record. " +
                      "This endpoint allows administrators to mark commissions as paid, pending, failed, or other status values during payment processing."
    )
    @PutMapping("/{commissionId}/status")
    public ResponseEntity<?> updateCommissionStatus(
        @Parameter(description = "Unique identifier of the commission to update", required = true)
        @PathVariable Long commissionId,
        @Parameter(description = "New payment status to set for the commission", required = true)
        @RequestParam PaymentStatus newStatus) {
        try {
            CommissionResponse updatedCommission = commissionService.updateCommissionStatus(commissionId, newStatus);
            return ResponseEntity.ok(updatedCommission);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating status for commission {}", commissionId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred."));
        }
    }

    @Secured("ROLE_COMMISSION_MANAGE")
    @Operation(
        summary = "Get Commission by ID String",
        description = "Retrieve detailed information about a specific commission using its string identifier. " +
                      "This endpoint provides comprehensive commission data including payment details, status, and related order information."
    )
    @GetMapping("/{commissionIdStr}")
    public ResponseEntity<CommissionResponse> getCommissionByIdStr(
        @Parameter(description = "String identifier of the commission to retrieve", required = true)
        @PathVariable String commissionIdStr) {
        CommissionResponse commission = commissionService.findByCommissionIdStr(commissionIdStr);
        if (commission == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(commission);
    }

    // Additional admin-specific commission endpoints can be added here (e.g., bulk status updates, commission reports)

}