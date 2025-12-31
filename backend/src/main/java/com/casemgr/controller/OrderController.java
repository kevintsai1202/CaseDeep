package com.casemgr.controller;

import com.casemgr.request.*;
import com.casemgr.enumtype.DeliveryStatus;
import com.casemgr.enumtype.PaymentStatus;
import com.casemgr.enumtype.UserType;
import com.casemgr.exception.BusinessException;
import com.casemgr.request.ContractChangeRequest;
import com.casemgr.request.PaymentCardRequest;
import com.casemgr.request.UpdatePaymentStatusRequest; // Assuming a DTO for payment status update
import com.casemgr.response.BlockResponse;
import com.casemgr.response.ContractResponse;
import com.casemgr.response.DeliveryItemResponse;
import com.casemgr.response.IntroResponse;
import com.casemgr.response.ListItemResponse;
import com.casemgr.response.OrderCreateResponse;
import com.casemgr.response.OrderResponse;
import com.casemgr.response.PaymentCardResponse;
import com.casemgr.service.ContractService;
import com.casemgr.service.DeliveryService;
import com.casemgr.service.OrderService;
import com.casemgr.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Order Management", description = "APIs for managing orders, contracts, payments, and delivery items throughout the order lifecycle")
@RestController
@RequestMapping("/api/orders")
@CrossOrigin
@RequiredArgsConstructor // 使用 Lombok 自動生成建構子注入
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final ContractService contractService;
    private final DeliveryService deliveryService;

    @PostMapping
    @Operation(
        summary = "Create Order from Template",
        description = "Create a new order based on an existing order template. " +
                      "The template provides the basic structure and configuration for the order, " +
                      "which can then be customized with specific requirements and details."
    )
    public ResponseEntity<OrderCreateResponse> createOrderFromTemplate(
        @Parameter(description = "Order creation request containing template ID and customization details", required = true)
        @Valid @RequestBody OrderCreateRequest orderReq) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createFromTemplate(orderReq));
    }
    
    @GetMapping("/myclientorders")
    @Operation(
        summary = "Get My Client Orders",
        description = "Retrieve all orders where the current user is the client (buyer). " +
                      "Returns a list of orders with their current status, details, and progress information."
    )
    public ResponseEntity<List<OrderResponse>> getMyClientOrders() {
        List<OrderResponse> orders = orderService.listUserBOrders();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/myproviderorders/all")
    @Operation(
        summary = "Get My Provider Orders",
        description = "Retrieve all orders where the current user is the provider (seller). " +
                      "Returns a comprehensive list of orders with client information, status, and service details."
    )
    public ResponseEntity<List<OrderResponse>> getMyProviderOrders() {
        List<OrderResponse> orders = orderService.listUserBOrders();
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/myproviderorders/{clientId}")
    @Operation(
        summary = "Get Provider Orders by Client",
        description = "Retrieve all orders where the current user is the provider and filtered by a specific client. " +
                      "Useful for providers to view their order history with a particular client."
    )
    public ResponseEntity<List<OrderResponse>> getMyProviderOrdersByClient(
        @Parameter(description = "Client user ID to filter orders", required = true)
        @PathVariable Long clientId) {
        List<OrderResponse> orders = orderService.listUserAOrdersByClient(clientId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{base62no}")
    @Operation(
        summary = "Get Order by Order Number",
        description = "Retrieve detailed information about a specific order using its unique order number. " +
                      "Returns complete order details including status, participants, contract, payment, and delivery information."
    )
    public ResponseEntity<OrderResponse> getOrderByOrderNo(
        @Parameter(description = "Base62 encoded order number", required = true)
        @PathVariable String base62no) {
        OrderResponse order = orderService.getOrderByOrderNo(base62no);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @Operation(
        summary = "List All Orders",
        description = "Retrieve a list of all orders accessible to the current user. " +
                      "The results may be filtered based on user permissions and roles. " +
                      "Supports pagination and filtering through query parameters."
    )
    public ResponseEntity<List<OrderResponse>> listOrders() {
        // 注意：OrderListRequest 通常作為 @ModelAttribute 或直接參數接收查詢參數
        // Service 層需要實現分頁和篩選邏輯
        List<OrderResponse> orders = orderService.listOrders();
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{oId}/status")
    @Operation(
        summary = "Update Order Status",
        description = "Update the status of an order with an optional reason. " +
                      "Status changes trigger workflow transitions and may affect order processing, " +
                      "notifications, and available actions for participants."
    )
    public ResponseEntity<OrderResponse> updateOrderStatus(
        @Parameter(description = "Order ID to update", required = true)
        @PathVariable Long oId,
        @Parameter(description = "Status update request containing new status and optional reason", required = true)
        @Valid @RequestBody UpdateStatusRequest statusRequest) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(oId, statusRequest.getNewStatus(), statusRequest.getReason());
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/confirmation/{bid}/select/{iid}")
    @Operation(
        summary = "Select Confirmation Block Item",
        description = "Select a specific item within a confirmation block during the order confirmation phase. " +
                      "This is used when orders have multiple options or configurations that need to be confirmed."
    )
    public ResponseEntity<BlockResponse> selectConfirmationBlockItem(
        @Parameter(description = "Block ID containing the selectable items", required = true)
        @PathVariable Long bid,
        @Parameter(description = "Item ID to select within the block", required = true)
        @PathVariable Long iid) {
    	BlockResponse blockResponse = orderService.selectConfirmationBlockItem(bid, iid);
        return ResponseEntity.ok(blockResponse);
    }
    
    @PatchMapping("/confirmation/{bid}/text")
    @Operation(
        summary = "Update Confirmation Block Text",
        description = "Update the text content of a confirmation block during the order confirmation phase. " +
                      "This allows users to provide additional details, specifications, or comments for the order."
    )
    public ResponseEntity<BlockResponse> updateConfirmationBlockText(
        @Parameter(description = "Block ID to update", required = true)
        @PathVariable Long bid,
        @Parameter(description = "Text update request containing the new content", required = true)
        @RequestBody UpdateTextBlockRequest updateText) {
    	BlockResponse blockResponse = orderService.textConfirmationBlock(bid, updateText.getContext());
        return ResponseEntity.ok(blockResponse);
    }
    
    @PatchMapping("/{oid}/confirmation/check")
    @Operation(
        summary = "Check Confirmation Completion",
        description = "Verify if all required confirmation items have been selected for an order. " +
                      "Returns true if all mandatory confirmations are complete, false otherwise. " +
                      "This is used to validate order readiness before proceeding to the next phase."
    )
    public ResponseEntity<Boolean> checkConfirmation(
        @Parameter(description = "Order ID to check confirmation status", required = true)
        @PathVariable Long oid) {
    	return ResponseEntity.ok(orderService.checkAllConfirmationSelected(oid));
    }
    
    @PostMapping("/{base62no}/sendquote")
    @Operation(
        summary = "Send Quote",
        description = "Provider sends a price quote to the client for the order. " +
                      "This initiates the quotation phase where the provider proposes a price for the requested services. " +
                      "The client can then accept, reject, or negotiate the quote."
    )
    public ResponseEntity<OrderResponse> sendQuote(
        @Parameter(description = "Base62 encoded order number", required = true)
        @PathVariable String base62no,
        @Parameter(description = "Quote request containing the proposed price", required = true)
        @Valid @RequestBody SendQuoteRequest quoteRequest) {
        OrderResponse updatedOrder = orderService.sendQuote(base62no, quoteRequest.getPrice());
        return ResponseEntity.ok(updatedOrder);
    }
    
    @PostMapping("/{base62no}/requestquote")
    @Operation(
        summary = "Request Quote",
        description = "Client requests a price quote from the provider for the order. " +
                      "This action notifies the provider that the client is ready to receive pricing information " +
                      "and moves the order into the quotation phase."
    )
    public ResponseEntity<OrderResponse> requestQuote(
        @Parameter(description = "Base62 encoded order number", required = true)
        @PathVariable String base62no) throws EntityNotFoundException, BusinessException {
        OrderResponse updatedOrder = orderService.requestQuote(base62no);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{base62no}/rejectquote")
    @Operation(
        summary = "Reject Quote",
        description = "Client rejects the price quote provided by the provider. " +
                      "This action can lead to renegotiation, order cancellation, or request for a revised quote. " +
                      "The order status will be updated to reflect the rejection."
    )
    public ResponseEntity<OrderResponse> rejectQuote(
        @Parameter(description = "Base62 encoded order number", required = true)
        @PathVariable String base62no) throws EntityNotFoundException, BusinessException {
        OrderResponse updatedOrder = orderService.rejectQuote(base62no);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @PostMapping("/{base62no}/acceptquote")
    @Operation(
        summary = "Accept Quote",
        description = "Client accepts the price quote provided by the provider. " +
                      "This action confirms the agreed price and moves the order to the contract phase. " +
                      "Both parties can then proceed with contract signing and payment processing."
    )
    public ResponseEntity<OrderResponse> acceptQuote(
        @Parameter(description = "Base62 encoded order number", required = true)
        @PathVariable String base62no) throws EntityNotFoundException, BusinessException {
        OrderResponse updatedOrder = orderService.acceptQuote(base62no);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @PatchMapping("/{base62no}/complete")
    @Operation(
        summary = "Complete Order",
        description = "Mark the order as completed by the client. " +
                      "This indicates that all deliverables have been received and approved, " +
                      "and the order can be finalized. Triggers final payment processing and order closure."
    )
    public ResponseEntity<OrderResponse> completeOrder(
        @Parameter(description = "Base62 encoded order number", required = true)
        @PathVariable String base62no) throws EntityNotFoundException, BusinessException {
        OrderResponse updatedOrder = orderService.completeOrder(base62no);
        return ResponseEntity.ok(updatedOrder);
    }
    
    @PatchMapping("/{base62no}/cancel")
    @Operation(
        summary = "Cancel Order",
        description = "Cancel an active order. This action can be performed by either party under certain conditions. " +
                      "Cancellation may trigger refund processes, penalty calculations, and notification workflows. " +
                      "The order status will be updated to cancelled."
    )
    public ResponseEntity<OrderResponse> cancelOrder(
        @Parameter(description = "Base62 encoded order number", required = true)
        @PathVariable String base62no) throws EntityNotFoundException, BusinessException {
        OrderResponse updatedOrder = orderService.cancelOrder(base62no);
        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{oId}/price")
    @Operation(
        summary = "Update Order Price",
        description = "Update the price of an existing order. This may be used for price adjustments, " +
                      "additional services, or corrections. Price changes may require approval from both parties " +
                      "and can affect payment schedules and contract terms."
    )
    public ResponseEntity<OrderResponse> updateOrderPrice(
        @Parameter(description = "Order ID to update", required = true)
        @PathVariable Long oId,
        @Parameter(description = "Price update request containing the new price amount", required = true)
        @Valid @RequestBody UpdatePriceRequest priceRequest) {
        OrderResponse updatedOrder = orderService.updateOrderPrice(oId, priceRequest.getNewPrice());
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{oId}")
    @Operation(
        summary = "Delete Order",
        description = "Permanently delete an order from the system. This is an administrative action " +
                      "that requires special permissions. Deleted orders cannot be recovered. " +
                      "Only available to administrators and order managers."
    )
    @Secured(value = {"ROLE_ADMIN", "ROLE_MANAGER_ORDER"})
    public ResponseEntity<Void> deleteOrder(
        @Parameter(description = "Order ID to delete", required = true)
        @PathVariable Long oId) {
        orderService.deleteOrder(oId);
        return ResponseEntity.noContent().build();
    }

    // --- Contract Phase Endpoints ---
    @PostMapping("/contract/{cId}/clientsign")
    @Operation(
        summary = "Client Sign Contract",
        description = "Client digitally signs the contract for the order. " +
                      "This action indicates the client's agreement to the contract terms and conditions. " +
                      "Both parties must sign before the order can proceed to the payment phase."
    )
    public ResponseEntity<ContractResponse> clientSign(
        @Parameter(description = "Contract ID to sign", required = true)
        @PathVariable Long cId) {
        return ResponseEntity.ok(contractService.signContract(cId, UserType.CLIENT));
    }
    
    @PostMapping("/contract/{cId}/providersign")
    @Operation(
        summary = "Provider Sign Contract",
        description = "Provider digitally signs the contract for the order. " +
                      "This action indicates the provider's agreement to deliver the services as specified. " +
                      "Both parties must sign before the order can proceed to the payment phase."
    )
    public ResponseEntity<ContractResponse> providerSign(
        @Parameter(description = "Contract ID to sign", required = true)
        @PathVariable Long cId) {
        return ResponseEntity.ok(contractService.signContract(cId, UserType.PROVIDER));
    }

    @PostMapping("/{oId}/contract/request-change")
    @Operation(
        summary = "Request Contract Change",
        description = "Request modifications to an existing contract. Either party can request changes " +
                      "to contract terms, conditions, or specifications. The request must be approved " +
                      "by the other party before taking effect."
    )
    public ResponseEntity<OrderResponse> requestContractChange(
        @Parameter(description = "Order ID for the contract to be changed", required = true)
        @PathVariable Long oId,
        @Parameter(description = "Contract change request containing proposed modifications", required = true)
        @Valid @RequestBody ContractChangeRequest changeRequest) {
        OrderResponse updatedOrder = orderService.requestContractChange(oId, changeRequest);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{oId}/contract/{cId}/approve")
    @Operation(
        summary = "Approve Contract Change",
        description = "Approve a requested contract change. This action confirms acceptance of the proposed " +
                      "modifications and updates the contract accordingly. Both parties must approve " +
                      "changes for them to take effect."
    )
    public ResponseEntity<OrderResponse> approveContractChange(
        @Parameter(description = "Order ID for the contract", required = true)
        @PathVariable Long oId,
        @Parameter(description = "Contract change ID or Contract History ID to approve", required = true)
        @PathVariable Long cId) {
        OrderResponse updatedOrder = orderService.approveContractChange(oId, cId);
        return ResponseEntity.ok(updatedOrder);
    }

    @PostMapping("/{oId}/contract/{cId}/reject")
    @Operation(
        summary = "Reject Contract Change",
        description = "Reject a requested contract change. This action declines the proposed modifications " +
                      "and maintains the current contract terms. The requesting party may submit " +
                      "revised change requests or proceed with the existing contract."
    )
    public ResponseEntity<OrderResponse> rejectContractChange(
        @Parameter(description = "Order ID for the contract", required = true)
        @PathVariable Long oId,
        @Parameter(description = "Contract change ID or Contract History ID to reject", required = true)
        @PathVariable Long cId) {
        OrderResponse updatedOrder = orderService.rejectContractChange(oId, cId);
        return ResponseEntity.ok(updatedOrder);
    }

    // --- Payment Phase Endpoints ---

    @PatchMapping("/payment/{pId}/pay")
    @Operation(
        summary = "Process Payment",
        description = "Process payment for a specific payment card. Updates the payment status to 'Paid' " +
                      "and triggers payment processing workflows. This action is typically performed " +
                      "by the client to fulfill their payment obligations."
    )
    public ResponseEntity<PaymentCardResponse> payment(
        @Parameter(description = "Payment Card ID to process payment for", required = true)
        @PathVariable Long pId) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(pId, PaymentStatus.Paid));
    }
    
    @PatchMapping("/payment/{pId}/complete")
    @Operation(
        summary = "Complete Payment",
        description = "Mark a payment as completed. This indicates that the payment has been fully processed, " +
                      "verified, and funds have been transferred. Updates the payment status to 'Complete' " +
                      "and may trigger order progression to the next phase."
    )
    public ResponseEntity<PaymentCardResponse> completePayment(
        @Parameter(description = "Payment Card ID to mark as complete", required = true)
        @PathVariable Long pId) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(pId, PaymentStatus.Complete));
    }
    
	@PostMapping(value = "/payment/{pId}/receipt" , consumes = "multipart/form-data")
	@Operation(
		summary = "Upload Payment Receipt",
		description = "Upload a payment receipt file as proof of payment. This document serves as " +
					  "verification that payment has been made and can be used for record-keeping " +
					  "and dispute resolution. Supports common image and PDF formats."
	)
	public ResponseEntity<PaymentCardResponse> uploadReceipt(
		@Parameter(description = "Payment Card ID to attach receipt to", required = true)
		@PathVariable Long pId,
		@Parameter(description = "Receipt file to upload (image or PDF)", required = true)
		@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(paymentService.uploadReceipt(pId, file));
	}
	
	@PostMapping(value = "/payment/{pId}/invoice" , consumes = "multipart/form-data")
	@Operation(
		summary = "Upload Payment Invoice",
		description = "Upload an invoice file for the payment. This document provides detailed breakdown " +
					  "of charges, services, and payment terms. Used for accounting, tax purposes, " +
					  "and financial record-keeping. Supports common document formats."
	)
	public ResponseEntity<PaymentCardResponse> uploadInvoice(
		@Parameter(description = "Payment Card ID to attach invoice to", required = true)
		@PathVariable Long pId,
		@Parameter(description = "Invoice file to upload (PDF or image)", required = true)
		@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(paymentService.uploadInvoice(pId, file));
	}

    @PostMapping("/{base62no}/payment")
    @Operation(
        summary = "Add Payment Card",
        description = "Add an additional payment card to an order. This allows for multiple payment methods, " +
                      "installment payments, or additional charges. The provider can add payment cards " +
                      "for different services or payment milestones."
    )
    public ResponseEntity<List<PaymentCardResponse>> addPaymentCard(
        @Parameter(description = "Base62 encoded order number", required = true)
        @PathVariable String base62no,
        @Parameter(description = "Payment card request containing card details and amount", required = true)
        @Valid @RequestBody PaymentCardRequest cardRequest) {
        return ResponseEntity.ok(orderService.addPaymentCard(base62no, cardRequest));
    }
    

    // --- Delivery Item Endpoints ---

    @GetMapping("/{base62no}/deliveries")
    @Operation(
        summary = "Get All Delivery Items",
        description = "Retrieve all delivery items associated with a specific order. " +
                      "Delivery items represent individual deliverables, files, or services " +
                      "that need to be provided as part of the order fulfillment."
    )
    public ResponseEntity<List<DeliveryItemResponse>> getDeliveryItems(
        @Parameter(description = "Base62 encoded order number", required = true)
        @PathVariable String base62no) {
        try {
            List<DeliveryItemResponse> items = deliveryService.getDeliveryItems(base62no);
            return ResponseEntity.ok(items);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/deliveries/{diId}")
    @Operation(
        summary = "Get Delivery Item Details",
        description = "Retrieve detailed information about a specific delivery item including " +
                      "its status, files, comments, and delivery history. Used for tracking " +
                      "individual deliverable progress and managing delivery workflows."
    )
    public ResponseEntity<DeliveryItemResponse> getDeliveryItem(
        @Parameter(description = "Delivery Item ID to retrieve", required = true)
        @PathVariable Long diId) {
            return ResponseEntity.ok(deliveryService.getDeliveryItem(diId)); 
    }

    @PostMapping("/{base62no}/deliveries")
    @Operation(
        summary = "Add New Delivery Item",
        description = "Add a new delivery item to an existing order. This creates a new deliverable " +
                      "that the provider needs to fulfill. Each delivery item can have its own " +
                      "timeline, requirements, and approval workflow."
    )
    public ResponseEntity<DeliveryItemResponse> addDeliveryItem(
            @Parameter(description = "Base62 encoded order number", required = true)
            @PathVariable String base62no,
            @Parameter(description = "Delivery item request containing item details and requirements", required = true)
            @Valid @RequestBody DeliveryItemRequest itemRequest) {
        try {
            DeliveryItemResponse newItem = deliveryService.addDeliveryItem(base62no, itemRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/deliveries/{diId}/upload")
    @Operation(
        summary = "Upload Delivery File",
        description = "Upload a file for a specific delivery item. This could be the actual deliverable, " +
                      "supporting documentation, or work-in-progress files. Multiple files can be " +
                      "uploaded for each delivery item as needed."
    )
    public ResponseEntity<DeliveryItemResponse> uploadDeliveryFile(
            @Parameter(description = "Delivery Item ID to upload file for", required = true)
            @PathVariable Long diId,
            @Parameter(description = "File to upload as part of the delivery", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            DeliveryItemResponse updatedItem = deliveryService.uploadDeliveryFile(diId, file);
            return ResponseEntity.ok(updatedItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } 
    }
    
    @DeleteMapping("/deliveries/files/{uuid}")
    @Operation(
        summary = "Delete Delivery File",
        description = "Delete a specific delivery file using its unique identifier. " +
                      "This permanently removes the file from the delivery item. " +
                      "Only the file owner or authorized users can delete files."
    )
    public ResponseEntity deleteDeliveryFile(
        @Parameter(description = "Unique identifier (UUID) of the file to delete", required = true)
        @PathVariable String uuid) {
        	deliveryService.deleteDeliveryFile(uuid);
            return ResponseEntity.noContent().build(); 
    }

    @PatchMapping("/deliveries/{diId}/status/modificationrequested")
    @Operation(
        summary = "Request Delivery Modification",
        description = "Update delivery item status to 'Modification Requested'. This indicates that " +
                      "the client has reviewed the delivery and requests changes or improvements. " +
                      "Comments can be provided to specify what modifications are needed."
    )
    public ResponseEntity<DeliveryItemResponse> updateDeliveryStatusToMR(
            @Parameter(description = "Delivery Item ID to update", required = true)
            @PathVariable Long diId,
            @Parameter(description = "Modification request containing comments and change details", required = true)
            @RequestBody ModificationRequested request) {
        try {
            DeliveryItemResponse updatedItem = deliveryService.updateStatusToModificationRequested(diId, request.getComments());
            return ResponseEntity.ok(updatedItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/deliveries/{diId}/status/deliveried")
    @Operation(
        summary = "Mark as Delivered",
        description = "Update delivery item status to 'Delivered'. This indicates that the provider " +
                      "has completed the deliverable and submitted it for client review. " +
                      "The client can then accept or request modifications."
    )
    public ResponseEntity<DeliveryItemResponse> updateDeliveryStatusToDeliveied(
            @Parameter(description = "Delivery Item ID to mark as delivered", required = true)
            @PathVariable Long diId) {
        try {
            DeliveryItemResponse updatedItem = deliveryService.updateStatusToDelivered(diId);
            return ResponseEntity.ok(updatedItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/deliveries/{diId}/status/accepted")
    @Operation(
        summary = "Accept Delivery",
        description = "Update delivery item status to 'Accepted'. This indicates that the client " +
                      "has reviewed and approved the delivered item. This action may mark the " +
                      "delivery as final and trigger completion workflows."
    )
    public ResponseEntity<DeliveryItemResponse> updateDeliveryStatusToAccepted(
            @Parameter(description = "Delivery Item ID to accept", required = true)
            @PathVariable Long diId,
            @Parameter(description = "Acceptance request containing final delivery confirmation", required = true)
            @RequestBody DeliveryAcceptedRequested request) {
        try {
            DeliveryItemResponse updatedItem = deliveryService.updateStatusToAccepted(diId, request.getFinalDelivery());
            return ResponseEntity.ok(updatedItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}