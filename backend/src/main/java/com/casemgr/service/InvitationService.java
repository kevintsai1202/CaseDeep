package com.casemgr.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.casemgr.enumtype.InvitationStatus;
import com.casemgr.response.InvitationResponse; // Assuming a response DTO will be created

import jakarta.persistence.EntityNotFoundException;

public interface InvitationService {

	public String getInvitationCode();
	
    /**
     * Lists invitation records for administrators, potentially filtered by region.
     * @param pageable Pagination and sorting information.
     * @return A page of InvitationResponse objects.
     */
	public Page<InvitationResponse> listInvitationsForAdmin(Pageable pageable);

    /**
     * Updates the reward payment status of a specific invitation record.
     * @param invitationId The ID of the invitation record.
     * @param newStatus The new reward payment status.
     * @return The updated InvitationResponse.
     * @throws EntityNotFoundException if the invitation record is not found.
     */
	public InvitationResponse updateInvitationStatus(Long invitationId, InvitationStatus newStatus) throws EntityNotFoundException;

    // Add other methods as needed, e.g., findById, createInvitationRecord
}