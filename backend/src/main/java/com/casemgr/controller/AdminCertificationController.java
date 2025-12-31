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
import org.springframework.web.bind.annotation.RequestBody; // Added for update body
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.enumtype.CEStatus; // Added import
import com.casemgr.exception.BusinessException; // Added import
import com.casemgr.request.AdminCertificationUpdateRequest; // Assuming a request DTO for update
import com.casemgr.response.CertificationResponse;
import com.casemgr.service.CertificationService; // Use interface

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid; // Added import
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Admin Certification Management", description = "Administrative APIs for managing user certifications, reviews, and approval processes")
@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/api/admin/certifications"}) // Base path for admin certification operations
public class AdminCertificationController {

    private final CertificationService certificationService;

    @Secured("ROLE_CERTIFICATION_MANAGE")
    @Operation(
        summary = "List Certifications for Admin Review",
        description = "Retrieve a paginated list of certification records filtered by the administrator's region. " +
                      "This endpoint provides certifications that require review, approval, or status updates by administrative staff."
    )
    @GetMapping
    public ResponseEntity<Page<CertificationResponse>> listCertificationsForAdmin(
        @Parameter(description = "Pagination parameters including page number, size, and sorting options")
        Pageable pageable) {
        // Service method already implements region filtering based on logged-in admin
        Page<CertificationResponse> certificationPage = certificationService.listCertificationsForAdmin(pageable);
        return ResponseEntity.ok(certificationPage);
    }

    @Secured("ROLE_CERTIFICATION_MANAGE")
    @Operation(
        summary = "Update Certification Status and Details",
        description = "Update the status, review score, and administrative comments for a specific certification record. " +
                      "This endpoint allows administrators to approve, reject, or modify certification details during the review process."
    )
    @PutMapping("/{certificationId}")
    public ResponseEntity<?> updateCertificationByAdmin(
        @Parameter(description = "Unique identifier of the certification to update", required = true)
        @PathVariable Long certificationId,
        @Parameter(description = "Update request containing new status, score, and review comments", required = true)
        @RequestBody @Valid AdminCertificationUpdateRequest updateRequest) {
        try {
            // Extract data from request DTO
            CEStatus newStatus = updateRequest.getNewStatus();
            String comment = updateRequest.getReviewComment();
            Double score = updateRequest.getScore();

            CertificationResponse updatedCertification = certificationService.updateCertificationByAdmin(certificationId, newStatus, comment, score);
            return ResponseEntity.ok(updatedCertification);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException | IllegalStateException e) { // Catch potential business rule violations
            log.warn("Invalid update for certification {}: {}", certificationId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating certification {}", certificationId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred."));
        }
    }

    // Additional admin-specific certification endpoints can be added here (e.g., get certification details, bulk operations)

}