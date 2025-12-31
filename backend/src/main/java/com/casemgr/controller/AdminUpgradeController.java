package com.casemgr.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.response.UpgradeResponse;
import com.casemgr.service.UpgradeService; // Use interface

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Admin Upgrade Management", description = "Administrative APIs for managing user account upgrades, subscription changes, and upgrade processing")
@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/api/admin/upgrades"}) // Base path for admin upgrade operations
public class AdminUpgradeController {

    private final UpgradeService upgradeService;

    @Secured("ROLE_UPGRADE_MANAGE")
    @Operation(
        summary = "List User Upgrades for Admin Review",
        description = "Retrieve a paginated list of user upgrade records filtered by the administrator's region. " +
                      "This endpoint provides upgrade data for processing subscription changes, account tier modifications, and upgrade approval workflows."
    )
    @GetMapping
    public ResponseEntity<Page<UpgradeResponse>> listUpgradesForAdmin(
        @Parameter(description = "Pagination parameters including page number, size, and sorting options")
        Pageable pageable) {
        // Service method already implements region filtering based on logged-in admin
        Page<UpgradeResponse> upgradePage = upgradeService.listUpgradesForAdmin(pageable);
        return ResponseEntity.ok(upgradePage);
    }

    // Additional admin-specific upgrade endpoints can be added here (e.g., upgrade details, status updates, approval workflows)

}