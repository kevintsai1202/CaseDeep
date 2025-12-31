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

import com.casemgr.request.LocationRequest;
import com.casemgr.response.LocationResponse;
import com.casemgr.service.LocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Admin Location Management", description = "Administrative APIs for managing geographical locations, regions, and location-based configurations in the system")
@RestController
@RequestMapping("/api/admin/locations")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class LocationController {
    
    private final LocationService locationService;

    /**
     * Retrieve all available locations in the system.
     * @return List of all locations with their details.
     */
    @GetMapping()
    @Operation(
        summary = "Get All Locations",
        description = "Retrieve a comprehensive list of all geographical locations configured in the system. " +
                      "This includes location names, codes, regional information, and administrative settings for location management."
    )
    public ResponseEntity<List<LocationResponse>> list() {
        return ResponseEntity.ok(locationService.list());
    }
    
    /**
     * Retrieve detailed information about a specific location by its ID.
     * @param id The unique identifier of the location.
     * @return Detailed location information including regional data and configuration.
     */
    @GetMapping(value = "/{id}")
    @Operation(
        summary = "Get Location Details by ID",
        description = "Retrieve comprehensive details about a specific location using its unique identifier. " +
                      "This includes location name, code, regional information, and administrative settings."
    )
    public ResponseEntity<LocationResponse> detail(
            @Parameter(description = "Unique identifier of the location to retrieve", required = true)
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(locationService.detail(id));
    }
    
    /**
     * Create a new location configuration in the system.
     * @param locationRequest Request body containing location information.
     * @return Details of the newly created location.
     */
    @PostMapping()
    @Operation(
        summary = "Create New Location",
        description = "Create a new geographical location configuration with specified details including location name, code, " +
                      "regional information, and administrative settings. This endpoint validates the location data and ensures uniqueness."
    )
    public ResponseEntity<LocationResponse> create(
        @Parameter(description = "Location creation request containing name, code, and regional information", required = true)
        @RequestBody @Validated LocationRequest locationRequest) {
        try {
            return ResponseEntity.ok(locationService.create(locationRequest));
        } catch (IllegalArgumentException e) {
            log.error("Error creating location: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing location configuration.
     * @param id The unique identifier of the location to update.
     * @param locationRequest Request body containing updated location information.
     * @return Updated location details.
     */
    @PutMapping(value = "/{id}")
    @Operation(
        summary = "Update Location Configuration",
        description = "Update an existing location's configuration including its name, code, regional information, and other settings. " +
                      "This endpoint validates the updated data and ensures location code uniqueness if modified."
    )
    public ResponseEntity<LocationResponse> update(
            @Parameter(description = "Unique identifier of the location to update", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Location update request containing modified location information", required = true)
            @RequestBody @Validated LocationRequest locationRequest) {
        try {
            return ResponseEntity.ok(locationService.update(id, locationRequest));
        } catch (EntityNotFoundException e) {
            log.error("Location not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Error updating location: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete a location configuration from the system.
     * @param id The unique identifier of the location to delete.
     */
    @SneakyThrows
    @DeleteMapping(value = "/{id}")
    @Operation(
        summary = "Delete Location Configuration",
        description = "Remove a location configuration from the system. This operation will permanently delete the location " +
                      "and may affect existing users, orders, or other entities that reference this location. Use with caution."
    )
    public ResponseEntity<Void> delete(
            @Parameter(description = "Unique identifier of the location to delete", required = true)
            @PathVariable("id") Long id) {
        try {
            locationService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.error("Location not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Search for a location by its name.
     * @param location The location name to search for.
     * @return Location information matching the specified name.
     */
    @GetMapping(value = "/search/{location}")
    @Operation(
        summary = "Find Location by Name",
        description = "Search for and retrieve location information using its name. This endpoint is useful for " +
                      "looking up specific locations by their display names or searching for locations in a specific region."
    )
    public ResponseEntity<LocationResponse> findByLocation(
            @Parameter(description = "Location name to search for (supports partial matching)", required = true)
            @PathVariable("location") String location) {
        try {
            return ResponseEntity.ok(locationService.findByLocation(location));
        } catch (EntityNotFoundException e) {
            log.error("Location not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}