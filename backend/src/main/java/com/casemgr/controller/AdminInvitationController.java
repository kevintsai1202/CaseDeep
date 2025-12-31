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

import com.casemgr.enumtype.InvitationStatus; // Added import
import com.casemgr.response.InvitationResponse;
import com.casemgr.service.InvitationService; // Use interface

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Admin Invitation Management", description = "Administrative APIs for managing user invitations, referral rewards, and invitation status tracking")
@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/api/admin/invitations"}) // Base path for admin invitation operations
public class AdminInvitationController {

    private final InvitationService invitationService;

    @Secured("ROLE_INVITE_MANAGE")
    @Operation(
        summary = "List Invitations for Admin Review",
        description = "Retrieve a paginated list of invitation records filtered by the administrator's region. " +
                      "This endpoint provides invitation data for reward processing, status tracking, and referral program management."
    )
    @GetMapping
    public ResponseEntity<Page<InvitationResponse>> listInvitationsForAdmin(
        @Parameter(description = "Pagination parameters including page number, size, and sorting options")
        Pageable pageable) {
        // Service method already implements region filtering based on logged-in admin
        Page<InvitationResponse> invitationPage = invitationService.listInvitationsForAdmin(pageable);
        return ResponseEntity.ok(invitationPage);
    }

    @Secured("ROLE_INVITE_MANAGE")
    @Operation(
        summary = "Update Invitation Reward Status",
        description = "Update the reward payment status of a specific invitation record. " +
                      "This endpoint allows administrators to process referral rewards, mark invitations as completed, or update status during reward distribution."
    )
    @PutMapping("/{invitationId}/status")
    public ResponseEntity<?> updateInvitationStatus(
        @Parameter(description = "Unique identifier of the invitation to update", required = true)
        @PathVariable Long invitationId,
        @Parameter(description = "New invitation status to set for reward processing", required = true)
        @RequestParam InvitationStatus newStatus) {
        try {
            InvitationResponse updatedInvitation = invitationService.updateInvitationStatus(invitationId, newStatus);
            return ResponseEntity.ok(updatedInvitation);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating status for invitation {}", invitationId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred."));
        }
    }

    // Additional admin-specific invitation endpoints can be added here (e.g., invitation analytics, bulk operations)

}