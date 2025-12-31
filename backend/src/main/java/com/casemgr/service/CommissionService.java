package com.casemgr.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.casemgr.enumtype.PaymentStatus;
import com.casemgr.response.CommissionResponse; // Assuming a response DTO will be created

import jakarta.persistence.EntityNotFoundException;

public interface CommissionService {

    /**
     * Lists commission records for administrators, potentially filtered by region.
     * @param pageable Pagination and sorting information.
     * @return A page of CommissionResponse objects.
     */
    Page<CommissionResponse> listCommissionsForAdmin(Pageable pageable);

    /**
     * Updates the payment status of a specific commission record.
     * @param commissionId The ID of the commission record.
     * @param newStatus The new payment status.
     * @return The updated CommissionResponse.
     * @throws EntityNotFoundException if the commission record is not found.
     */
    CommissionResponse updateCommissionStatus(Long commissionId, PaymentStatus newStatus) throws EntityNotFoundException;

    /**
     * Find commission by commissionIdStr
     * @param commissionIdStr The commission ID string (e.g., CO2501232305)
     * @return The CommissionResponse or null if not found
     */
    CommissionResponse findByCommissionIdStr(String commissionIdStr);

    /**
     * Creates a new commission record
     * @param orderId The order ID
     * @param amount The commission amount
     * @param rate The commission rate
     * @return The created CommissionResponse
     */
    CommissionResponse createCommission(Long orderId, Double amount, Double rate);

    // Add other methods as needed, e.g., findById, calculateCommission
}