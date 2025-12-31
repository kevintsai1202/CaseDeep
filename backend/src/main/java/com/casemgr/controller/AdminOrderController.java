package com.casemgr.controller;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody; // Added for potential future use
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // Added for potential status update
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.enumtype.OrderStatus; // Added import
import com.casemgr.response.OrderResponse;
import com.casemgr.service.OrderService; // Use interface

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException; // Added import
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Admin Order Management", description = "Administrative APIs for managing orders, status updates, and order lifecycle operations")
@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
@RequestMapping({"/api/admin/orders"}) // Base path for admin order operations
public class AdminOrderController {

    private final OrderService orderService;

    @Secured("ROLE_ORDER_MANAGE")
    @Operation(
        summary = "List Orders for Admin Management",
        description = "Retrieve a paginated list of orders filtered by the administrator's region. " +
                      "This endpoint provides comprehensive order data for administrative oversight, status tracking, and order management operations."
    )
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> listOrdersForAdmin(
        @Parameter(description = "Pagination parameters including page number, size, and sorting options")
        Pageable pageable) {
        // Service method already implements region filtering based on logged-in admin
        Page<OrderResponse> orderPage = orderService.listOrdersForAdmin(pageable);
        return ResponseEntity.ok(orderPage);
    }

    @Secured("ROLE_ORDER_MANAGE")
    @Operation(
        summary = "Get Order Details by ID",
        description = "Retrieve comprehensive details for a specific order including status, items, pricing, " +
                      "customer information, and transaction history. This endpoint provides full order visibility for administrative review."
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
        @Parameter(description = "Unique identifier of the order to retrieve", required = true)
        @PathVariable Long orderId) {
        try {
            OrderResponse orderResponse = orderService.getOrderById(orderId);
            return ResponseEntity.ok(orderResponse);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Secured("ROLE_ORDER_MANAGE")
    @Operation(
        summary = "Update Order Status",
        description = "Update the status of a specific order with optional reason for the change. " +
                      "This endpoint allows administrators to manage order lifecycle, handle cancellations, approvals, and status transitions."
    )
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
        @Parameter(description = "Unique identifier of the order to update", required = true)
        @PathVariable Long orderId,
        @Parameter(description = "New status to set for the order", required = true)
        @RequestParam OrderStatus newStatus,
        @Parameter(description = "Optional reason or comment for the status change")
        @RequestParam(required = false) String reason) {
        try {
            OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, newStatus, reason);
            return ResponseEntity.ok(updatedOrder);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) { // Catch invalid status transitions
            log.warn("Invalid status update for order {}: {}", orderId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating status for order {}", orderId, e);
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred."));
        }
    }

    // Additional admin-specific order endpoints can be added here (e.g., update pricing, manage order items, bulk operations)

}