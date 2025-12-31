package com.casemgr.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.casemgr.entity.Order;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.entity.PaymentCard;
import com.casemgr.enumtype.PaymentStatus; // Assuming this exists
import com.casemgr.request.PaymentCardRequest; // Placeholder DTO
import com.casemgr.response.PaymentCardResponse; // Placeholder DTO

import jakarta.persistence.EntityNotFoundException;

public interface PaymentService {

    /**
     * Initializes payment cards for an order based on the template's payment methods.
     * Called by OrderService during order creation.
     * @param order The newly created order.
     * @param template The template the order was based on.
     * @return A list of initialized payment card details.
     */
    void initializePaymentCards(Long oId);

    /**
     * Retrieves the list of payment cards associated with an order.
     * @param oId The order ID.
     * @return A list of payment card details.
     * @throws EntityNotFoundException If the order is not found.
     */
    List<PaymentCardResponse> getPaymentCards(Long oId) throws EntityNotFoundException;

    /**
     * Updates the status of a specific payment card.
     * This might be called by a payment gateway callback or manually.
     * @param pcId The payment card ID.
     * @param status The new payment status.
     * @param transactionId Optional transaction identifier from the payment gateway.
     * @return Updated payment card details.
     * @throws EntityNotFoundException If the payment card is not found.
     */
    PaymentCardResponse updatePaymentStatus(Long pcId, PaymentStatus status) throws EntityNotFoundException;
	PaymentCardResponse uploadReceipt(Long pcId, MultipartFile file) throws EntityNotFoundException;
	PaymentCardResponse uploadInvoice(Long pcId, MultipartFile file) throws EntityNotFoundException;
    /**
     * Allows adding an extra payment card to an order (e.g., by User A).
     * @param oId The order ID to add the card to.
     * @param cardRequest Details of the payment card to add.
     * @return Details of the newly added payment card.
     * @throws EntityNotFoundException If the order is not found.
     */
//    PaymentCardResponse createPaymentCard(Long oId, PaymentCardRequest cardRequest) throws EntityNotFoundException;

    PaymentCard createPaymentCard(Order order, int installmentNumber, BigDecimal amount) throws EntityNotFoundException;
    /**
     * Checks if all payment cards associated with an order have been successfully paid.
     * @param oId The order ID.
     * @return true if all payments are completed, false otherwise.
     * @throws EntityNotFoundException If the order is not found.
     */
    boolean checkAllPaymentsCompleted(Long oId) throws EntityNotFoundException;

}