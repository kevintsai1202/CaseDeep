package com.casemgr.service;

import java.util.List;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.entity.Order;
import com.casemgr.enumtype.OrderStatus;
import com.casemgr.enumtype.PaymentStatus; // Added missing import
import com.casemgr.exception.BusinessException;
import com.casemgr.request.BlockSelectRequest;
import com.casemgr.request.BlockUpdateRequest;
import com.casemgr.request.ContractChangeRequest; // Added missing import
import com.casemgr.request.InputBlockRequest;
import com.casemgr.request.OrderCreateRequest;
import com.casemgr.request.OrderListRequest;
import com.casemgr.request.PaymentCardRequest; // Added missing import
import com.casemgr.response.BlockResponse;
import com.casemgr.response.ListItemResponse;
import com.casemgr.response.OrderCreateResponse;
// import com.casemgr.request.OrderTemplateRequest; // Removed unused import
import com.casemgr.response.OrderResponse;
// import com.casemgr.response.OrderTemplateResponse; // Removed unused import
import com.casemgr.response.PaymentCardResponse;

import jakarta.persistence.EntityNotFoundException;

public interface OrderService {

    // --- CRUD & Listing ---
	OrderCreateResponse createFromTemplate(OrderCreateRequest orderReq);
    OrderResponse getOrderById(Long oId) throws EntityNotFoundException; // get order detail
    OrderResponse getOrderByOrderNo(String orderNo) throws EntityNotFoundException; // get order detail by no
    List<OrderResponse> listOrders(); // Added method signature based on spec
    List<OrderResponse> listUserBOrders(); // Added method signature based on spec
    List<OrderResponse> listUserAOrdersByClient(Long clientId); // Added method signature based on spec
    OrderResponse update(Long oId, OrderCreateRequest orderRequest) throws EntityNotFoundException; // Keep existing update if needed, or remove
    void deleteOrder(Long oId) throws EntityNotFoundException; // Renamed from delete
    
    // --- Admin Specific Listing ---
    Page<OrderResponse> listOrdersForAdmin(Pageable pageable); // Added for admin listing with pagination
   
    // --- Specific Listing (Keep if needed) ---
    List<OrderResponse> listOrderByCategory(Long caId) throws EntityNotFoundException;
    List<OrderResponse> listOrderByProvider(Long providerId) throws EntityNotFoundException;

    // --- Order Status Update ---
    OrderResponse updateOrderStatus(Long oId, OrderStatus newStatus, String reason) throws EntityNotFoundException;

    // --- Confirmation Phase Methods ---
    OrderResponse updateConfirmation(Long oId, List<BlockUpdateRequest> blockUpdates) throws EntityNotFoundException;
    BlockResponse selectConfirmationBlockItem(Long bId, Long liId)throws EntityNotFoundException;
    
    Boolean checkAllConfirmationSelected(Long oId)throws EntityNotFoundException;
    
    OrderResponse sendQuote(Long oId, BigDecimal price) throws EntityNotFoundException;
    OrderResponse sendQuote(String orderNoBase62, BigDecimal price) throws EntityNotFoundException;
    OrderResponse requestQuote(Long oId) throws EntityNotFoundException, BusinessException;
	OrderResponse requestQuote(String orderNoBase62) throws EntityNotFoundException, BusinessException;

    OrderResponse rejectQuote(Long oId) throws EntityNotFoundException, BusinessException;
    OrderResponse rejectQuote(String orderNoBase62) throws EntityNotFoundException, BusinessException;
    
    OrderResponse acceptQuote(Long oId) throws EntityNotFoundException, BusinessException;
    OrderResponse acceptQuote(String orderNoBase62) throws EntityNotFoundException, BusinessException;
    
    OrderResponse completeOrder(String orderNoBase62) throws EntityNotFoundException, BusinessException ;
    OrderResponse cancelOrder(String orderNoBase62) throws EntityNotFoundException, BusinessException;
    OrderResponse completeOrder(Long oId) throws EntityNotFoundException,BusinessException;
    OrderResponse cancelOrder(Long oId) throws EntityNotFoundException,BusinessException;

    
    OrderResponse updateOrderPrice(Long oId, BigDecimal newPrice) throws EntityNotFoundException;

    // --- Contract Phase Methods ---
    OrderResponse requestContractChange(Long oId, ContractChangeRequest changeRequest) throws EntityNotFoundException; // Assuming ContractChangeRequest DTO
    OrderResponse approveContractChange(Long oId, Long cId) throws EntityNotFoundException; // cId might be contract history ID
    OrderResponse rejectContractChange(Long oId, Long cId) throws EntityNotFoundException; // cId might be contract history ID
    // Signing might be handled by updateOrderStatus

    // --- Payment Phase Methods ---
    // processPayment might involve external integration and return a status or redirect URL
    // String processPayment(Long oId, PaymentRequest paymentRequest) throws EntityNotFoundException; // Placeholder
    PaymentCardResponse updatePaymentStatus(Long oId, Long pId, PaymentStatus newStatus) throws EntityNotFoundException; // Assuming PaymentStatus enum and pId is PaymentCard ID
    List<PaymentCardResponse> addPaymentCard(String base62no, PaymentCardRequest cardRequest) throws EntityNotFoundException; // Assuming PaymentCardRequest DTO
	BlockResponse textConfirmationBlock(Long bId, String content) throws EntityNotFoundException;


    // --- Template related methods (Commented out as per original, keep if needed) ---
    //	OrderTemplateResponse createTemplate(String name);
    //	OrderTemplateResponse create(Long caId, OrderTemplateRequest orderTemplateRequest);
    //	OrderTemplateResponse uploadTemplateName(Long otId, String name) throws EntityNotFoundException;
    //	OrderTemplateResponse uploadTemplateImage(Long otId, MultipartFile file) throws EntityNotFoundException;
    //	OrderTemplateResponse templateDetail(Long otId) throws EntityNotFoundException;
    //	List<OrderTemplateResponse> listMyTemplate();
}