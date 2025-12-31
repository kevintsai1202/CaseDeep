package com.casemgr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.request.CurrencyRequest;
import com.casemgr.response.CurrencyResponse;
import com.casemgr.service.CurrencyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Admin Currency Management", description = "Administrative APIs for managing currency configurations, exchange rates, and currency-related settings in the system")
@RestController
@RequestMapping("/api/admin/currencies")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class CurrencyController {
    
    private final CurrencyService currencyService;

    /**
     * Retrieve all available currencies in the system.
     * @return List of all currencies with their details.
     */
    @GetMapping()
    @Operation(
        summary = "Get All Currencies",
        description = "Retrieve a comprehensive list of all currencies configured in the system. " +
                      "This endpoint provides currency codes, names, symbols, and exchange rate information for administrative management."
    )
    public ResponseEntity<List<CurrencyResponse>> list() {
        return ResponseEntity.ok(currencyService.list());
    }
    
    /**
     * Retrieve detailed information about a specific currency by its ID.
     * @param id The unique identifier of the currency.
     * @return Detailed currency information including exchange rates and configuration.
     */
    @GetMapping(value = "/{id}")
    @Operation(
        summary = "Get Currency Details by ID",
        description = "Retrieve comprehensive details about a specific currency using its unique identifier. " +
                      "This includes currency code, name, symbol, exchange rates, and administrative settings."
    )
    public ResponseEntity<CurrencyResponse> detail(
            @Parameter(description = "Unique identifier of the currency to retrieve", required = true)
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(currencyService.detail(id));
    }
    
    /**
     * Create a new currency configuration in the system.
     * @param currencyRequest Request body containing currency information.
     * @return Details of the newly created currency.
     */
    @PostMapping()
    @Operation(
        summary = "Create New Currency",
        description = "Create a new currency configuration with specified details including currency code, name, symbol, and exchange rates. " +
                      "This endpoint validates the currency data and ensures no duplicate currency codes exist in the system."
    )
    public ResponseEntity<CurrencyResponse> create(
        @Parameter(description = "Currency creation request containing code, name, symbol, and exchange rate information", required = true)
        @RequestBody @Validated CurrencyRequest currencyRequest) {
        try {
            return ResponseEntity.ok(currencyService.create(currencyRequest));
        } catch (IllegalArgumentException e) {
            log.error("Error creating currency: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing currency configuration.
     * @param id The unique identifier of the currency to update.
     * @param currencyRequest Request body containing updated currency information.
     * @return Updated currency details.
     */
    @PutMapping(value = "/{id}")
    @Operation(
        summary = "Update Currency Configuration",
        description = "Update an existing currency's configuration including its name, symbol, exchange rates, and other settings. " +
                      "This endpoint validates the updated data and ensures currency code uniqueness if modified."
    )
    public ResponseEntity<CurrencyResponse> update(
            @Parameter(description = "Unique identifier of the currency to update", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Currency update request containing modified currency information", required = true)
            @RequestBody @Validated CurrencyRequest currencyRequest) {
        try {
            return ResponseEntity.ok(currencyService.update(id, currencyRequest));
        } catch (EntityNotFoundException e) {
            log.error("Currency not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Error updating currency: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete a currency configuration from the system.
     * @param id The unique identifier of the currency to delete.
     */
    @SneakyThrows
    @DeleteMapping(value = "/{id}")
    @Operation(
        summary = "Delete Currency Configuration",
        description = "Remove a currency configuration from the system. This operation will permanently delete the currency " +
                      "and may affect existing transactions or orders that reference this currency. Use with caution."
    )
    public ResponseEntity<Void> delete(
            @Parameter(description = "Unique identifier of the currency to delete", required = true)
            @PathVariable("id") Long id) {
        try {
            currencyService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.error("Currency not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search for a currency by its currency code.
     * @param currency The currency code to search for (e.g., USD, EUR, TWD).
     * @return Currency information matching the specified code.
     */
    @GetMapping(value = "/search/{currency}")
    @Operation(
        summary = "Find Currency by Code",
        description = "Search for and retrieve currency information using its standard currency code (ISO 4217 format). " +
                      "This endpoint is useful for looking up specific currencies by their three-letter codes like USD, EUR, or TWD."
    )
    public ResponseEntity<CurrencyResponse> findByCurrency(
            @Parameter(description = "Three-letter currency code to search for (e.g., USD, EUR, TWD)", required = true)
            @PathVariable("currency") String currency) {
        try {
            return ResponseEntity.ok(currencyService.findByCurrency(currency));
        } catch (EntityNotFoundException e) {
            log.error("Currency not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}