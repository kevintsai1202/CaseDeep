package com.casemgr.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.casemgr.response.UpgradeResponse; // Assuming a response DTO will be created

public interface UpgradeService {

    /**
     * Lists upgrade records for administrators, potentially filtered by region.
     * @param pageable Pagination and sorting information.
     * @return A page of UpgradeResponse objects.
     */
    Page<UpgradeResponse> listUpgradesForAdmin(Pageable pageable);

    // Add other methods as needed, e.g., findById, createUpgrade, updateStatus
}