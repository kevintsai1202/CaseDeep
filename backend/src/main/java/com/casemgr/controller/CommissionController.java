package com.casemgr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.response.CommissionResponse;
import com.casemgr.service.CommissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Commission Management", description = "APIs for managing commission records, payments, and commission tracking")
@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/api/commissions"})
public class CommissionController {

    private final CommissionService commissionService;

    @Operation(
        summary = "Get Commission by ID String",
        description = "Retrieve detailed information about a specific commission using its string identifier. " +
                      "This endpoint provides comprehensive commission data including payment status, amounts, and related transaction information."
    )
    @GetMapping("/{commissionIdStr}")
    public ResponseEntity<CommissionResponse> getCommissionByIdStr(
        @Parameter(description = "String identifier of the commission to retrieve", required = true)
        @PathVariable String commissionIdStr) {
        log.info("Fetching commission with ID: {}", commissionIdStr);
        CommissionResponse commission = commissionService.findByCommissionIdStr(commissionIdStr);
        if (commission == null) {
            log.warn("Commission not found with ID: {}", commissionIdStr);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(commission);
    }
}