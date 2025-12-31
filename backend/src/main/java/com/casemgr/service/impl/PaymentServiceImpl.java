package com.casemgr.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode; // Added for potential rounding
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.converter.PaymentCardConverter; // Assuming this exists
import com.casemgr.entity.Account;
import com.casemgr.entity.Block;
import com.casemgr.entity.Contract;
import com.casemgr.entity.Order;
import com.casemgr.entity.PaymentCard;
import com.casemgr.enumtype.BlockType;
import com.casemgr.enumtype.ContractStatus;
import com.casemgr.enumtype.OrderStatus; // Added for status checks
import com.casemgr.enumtype.PaymentMethod; // Assuming PaymentMethod enum exists for parsing template methods
import com.casemgr.enumtype.PaymentStatus;
import com.casemgr.repository.ContractRepository;
import com.casemgr.repository.OrderRepository;
import com.casemgr.repository.PaymentCardRepository;
import com.casemgr.response.FiledataResponse;
import com.casemgr.response.PaymentCardResponse;
import com.casemgr.service.FileStorageService;
import com.casemgr.service.OrderService; // Added import
import com.casemgr.service.PaymentService;
import com.casemgr.service.RevenueShareService; // Added for Revenue Share integration

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor // Handles injection of final fields
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentCardRepository paymentCardRepository;
    private final OrderRepository orderRepository; // Keep for reading order
    private final OrderService orderService; // Added injection
    private final ContractRepository contractRepository;
    private final FileStorageService fileStorageService;
    private final RevenueShareService revenueShareService; // Added for Revenue Share integration

    @Override
    @Transactional
    public void initializePaymentCards(Long cId) {
        log.info("Initializing payment cards for Contract ID: {}", cId);
//        Order order = orderRepository.getReferenceById(oId);
        Contract contract = contractRepository.getReferenceById(cId);
        Order order = contract.getOrder();
        List<PaymentCard> paymentCards = new ArrayList<>();
        List<Block> blocks = contract.getBlocks();
        String paymentMethod = blocks.stream()
        		.filter(block -> BlockType.payment.equals(block.getType()))
        		.map(Block::getContext)
        	    .findFirst()
        	    .orElse(null);
        
        BigDecimal contractPrice = contract.getContractPrice();

        if (contractPrice == null || contractPrice.compareTo(BigDecimal.ZERO) <= 0) {
             log.warn("Cannot initialize payment cards for Order ID: {} because total price is not valid.", order.getOId());
             return;
        }


        try {
	            // Assuming PaymentMethod enum exists and matches the strings
	            if (PaymentMethod.FullPayment.name().equalsIgnoreCase(paymentMethod)) {
	                paymentCards.add(createPaymentCard(order, 1, contractPrice));
	            } else if (paymentMethod != null && paymentMethod.toUpperCase().startsWith("INSTALLMENT")) {
	                List<BigDecimal> ratios = getInstallmentRatios(paymentMethod);
	                int installments = ratios.size(); // Number of installments derived from ratios
	
	                if (installments > 0) {
	                    BigDecimal calculatedSum = BigDecimal.ZERO;
	                    for (int i = 0; i < installments; i++) {
	                        BigDecimal ratio = ratios.get(i);
	                        BigDecimal installmentAmount = contractPrice.multiply(ratio).setScale(2, RoundingMode.HALF_UP); // Scale and round
	
	                        // Adjust last installment to match total price exactly due to rounding
	                        if (i == installments - 1) {
	                            installmentAmount = contractPrice.subtract(calculatedSum);
	                        }
	
	                        paymentCards.add(createPaymentCard(order, i + 1, installmentAmount));
	                        calculatedSum = calculatedSum.add(installmentAmount);
	                    }
	                    // Final check if sum matches total price after adjustments
	                    if (calculatedSum.compareTo(contractPrice) != 0) {
	                        log.error("Installment sum ({}) does not match total price ({}) after calculation for Order ID: {}", calculatedSum, contractPrice, order.getOId());
	                        // Decide how to handle this error - maybe clear cards and throw?
	                        paymentCards.clear(); // Clear potentially incorrect cards
	                        throw new IllegalStateException("Installment calculation error for order " + order.getOId());
	                    }
	                } else {
	                     log.error("Could not determine installment count or ratios for method: {}", paymentMethod);
	                }
	            }
	        } catch (Exception e) {
	            log.error("Error parsing payment method '{}' for Order ID: {}", paymentMethod, order.getOId(), e);
	        }
            

            if (!paymentCards.isEmpty()) {
                paymentCards = paymentCardRepository.saveAll(paymentCards);
                order.getPayments().clear();
                order.getPayments().addAll(paymentCards);
                orderRepository.save(order);
                log.info("Initialized {} payment cards for Order ID: {}", paymentCards.size(), order.getOId());
            }
//        } else {
//            log.warn("No payment methods defined in template for Order ID: {}", oId);
//        }

        // TODO: Implement and use PaymentCardConverter
        // return PaymentCardConverter.INSTANCE.entityToResponse(paymentCards);
//        return new ArrayList<>(); // Placeholder
    }

    public PaymentCard createPaymentCard(Order order, int installmentNumber, BigDecimal amount) {
		
        Contract activeContract = order.getContracts().stream()
                .filter(contract -> ContractStatus.Active.equals(contract.getContractStatus()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Active contract not found for order ID: " + order.getOId()));
    	Account receivingAccount = activeContract.getReceivingAccount();
    	Account newAccount = new Account();
    	
    	BeanUtils.copyProperties(receivingAccount,newAccount,"AId");
    	
  
    	return PaymentCard.builder()
    	        .order(order)
    	        .installmentNumber(installmentNumber)
    	        .amount(amount)
    	        .status(PaymentStatus.Pending)
    	        .receivingAccount(newAccount)
    	        .build();
    }

    private List<BigDecimal> getInstallmentRatios(String method) {
        List<BigDecimal> ratios = new ArrayList<>();
        try {
            if (method == null) return ratios;
            String upperMethod = method.toUpperCase();

            if (upperMethod.startsWith("INSTALLMENT")) {
                if (upperMethod.equals("INSTALLMENT2_1")) {
                    ratios.add(new BigDecimal("0.10"));
                    ratios.add(new BigDecimal("0.90"));
                } else if (upperMethod.equals("INSTALLMENT2_2")) {
                    ratios.add(new BigDecimal("0.20"));
                    ratios.add(new BigDecimal("0.80"));
                } else if (upperMethod.equals("INSTALLMENT2_3")) {
                    ratios.add(new BigDecimal("0.30"));
                    ratios.add(new BigDecimal("0.70"));
                } else if (upperMethod.equals("INSTALLMENT2_4")) {
                    ratios.add(new BigDecimal("0.40"));
                    ratios.add(new BigDecimal("0.60"));
                } else if (upperMethod.equals("INSTALLMENT2_5")) {
                    ratios.add(new BigDecimal("0.50"));
                    ratios.add(new BigDecimal("0.50"));
                } else if (upperMethod.equals("INSTALLMENT2_6")) {
                    ratios.add(new BigDecimal("0.60"));
                    ratios.add(new BigDecimal("0.40"));
                } else if (upperMethod.equals("INSTALLMENT2_7")) {
                    ratios.add(new BigDecimal("0.70"));
                    ratios.add(new BigDecimal("0.30"));
                } else if (upperMethod.equals("INSTALLMENT2_8")) {
                    ratios.add(new BigDecimal("0.80"));
                    ratios.add(new BigDecimal("0.20"));
                } else if (upperMethod.equals("INSTALLMENT2_9")) {
                    ratios.add(new BigDecimal("0.90"));
                    ratios.add(new BigDecimal("0.10"));
                } else if (upperMethod.equals("INSTALLMENT3_1")) {
                    ratios.add(new BigDecimal("0.30"));
                    ratios.add(new BigDecimal("0.40"));
                    ratios.add(new BigDecimal("0.30"));
                } else if (upperMethod.equals("INSTALLMENT4_1")) {
                    ratios.add(new BigDecimal("0.25"));
                    ratios.add(new BigDecimal("0.25"));
                    ratios.add(new BigDecimal("0.25"));
                    ratios.add(new BigDecimal("0.25"));
                } else if (upperMethod.equals("INSTALLMENT5_1")) {
                    ratios.add(new BigDecimal("0.20"));
                    ratios.add(new BigDecimal("0.20"));
                    ratios.add(new BigDecimal("0.20"));
                    ratios.add(new BigDecimal("0.20"));
                    ratios.add(new BigDecimal("0.20"));
                } else {
                    log.warn("Unknown installment format: {}", method);
                }
            }
        } catch (Exception e) {
            log.error("Could not parse installment method: {}", method, e);
        }

        // Final validation
        if (!ratios.isEmpty()) {
            BigDecimal sum = ratios.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            // Allow for tiny precision differences
            if (sum.subtract(BigDecimal.ONE).abs().compareTo(new BigDecimal("0.00001")) > 0) {
                log.error("Installment ratios for method {} do not sum to 1 (sum={})", method, sum);
                return new ArrayList<>(); // Return empty if sum is incorrect
            }
        } else {
            log.warn("Could not determine ratios for method: {}", method);
        }

        return ratios;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentCardResponse> getPaymentCards(Long oId) throws EntityNotFoundException {
         Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

        List<PaymentCard> cards = order.getPayments();

        if (cards == null) {
            return new ArrayList<>();
        } else {
            return PaymentCardConverter.INSTANCE.entityToResponse(cards);
        }
    }

    @Override
    @Transactional
    public PaymentCardResponse updatePaymentStatus(Long pcId, PaymentStatus status) throws EntityNotFoundException {
        PaymentCard card = paymentCardRepository.findById(pcId)
             .orElseThrow(() -> new EntityNotFoundException("PaymentCard not found with id: " + pcId));
        
        log.info("Updating payment status for PaymentCard ID: {} to {}", pcId, status);
        card.setStatus(status);
        card.setPaymentDate(new Date());
        
        card = paymentCardRepository.save(card);

        // Check if all payments for the associated order are now complete
        Order order = card.getOrder();
        if (order != null) {
             // Re-fetch order to ensure status is current within this transaction
             Order currentOrder = orderRepository.findById(order.getOId())
                 .orElseThrow(() -> new EntityNotFoundException("Order not found when checking payment completion: " + order.getOId()));

            if (status == PaymentStatus.Paid && currentOrder.getStatus() == OrderStatus.awaiting_payment) {
                // If at least one payment is made, move to PartiallyPaid VIA OrderService
                log.info("First payment made for Order ID: {}. Updating status to PartiallyPaid via OrderService.", currentOrder.getOId());
                orderService.updateOrderStatus(currentOrder.getOId(), OrderStatus.in_progress, "First payment received");
                
                // Create Revenue Share record for first payment completion
                try {
                    log.info("Creating Revenue Share record for first payment completion of Order ID: {}", currentOrder.getOId());
                    revenueShareService.createRevenueShare(currentOrder);
                    log.info("Revenue Share record created successfully for Order ID: {}", currentOrder.getOId());
                } catch (Exception e) {
                    log.error("Failed to create Revenue Share record for Order ID: {}. Error: {}", currentOrder.getOId(), e.getMessage(), e);
                    // Don't throw exception to avoid breaking payment flow
                }
            }
        }
        return PaymentCardConverter.INSTANCE.entityToResponse(card); // Placeholder
    }
    
    @Override 
	@Transactional
	public PaymentCardResponse uploadReceipt(Long pcId, MultipartFile file) throws EntityNotFoundException{
		PaymentCard card = paymentCardRepository.findById(pcId)
	             .orElseThrow(() -> new EntityNotFoundException("PaymentCard not found with id: " + pcId));
		
		FiledataResponse fileData = new FiledataResponse(fileStorageService.save(file));
		
		card.setReceiptUrl(fileData.getUrl());
		card = paymentCardRepository.save(card);
		return PaymentCardConverter.INSTANCE.entityToResponse(card);
	}
    
	@Override 
	@Transactional
	public PaymentCardResponse uploadInvoice(Long pcId, MultipartFile file) throws EntityNotFoundException{
		PaymentCard card = paymentCardRepository.findById(pcId)
	             .orElseThrow(() -> new EntityNotFoundException("PaymentCard not found with id: " + pcId));
		
		FiledataResponse fileData = new FiledataResponse(fileStorageService.save(file));
		
		card.setInvoiceUrl(fileData.getUrl());
		card = paymentCardRepository.save(card);
		return PaymentCardConverter.INSTANCE.entityToResponse(card);
	}

    @Override // Make public if called from outside, keep private/rename if only internal
    @Transactional(readOnly = true)
    public boolean checkAllPaymentsCompleted(Long oId) throws EntityNotFoundException {
         Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));
        return checkAllPaymentsCompletedInternal(order);
    }

    // Internal helper to avoid re-fetching order if already available
    private boolean checkAllPaymentsCompletedInternal(Order order) {
        List<PaymentCard> cards = order.getPayments();

        if (cards == null || cards.isEmpty()) {
            log.info("No payment cards found for Order ID: {}. Considering payments complete.", order.getOId());
            return true; // Or false depending on business logic
        }

        boolean allPaid = cards.stream().allMatch(card -> PaymentStatus.Complete.equals(card.getStatus()));
        log.info("Checked payment completion for Order ID: {}. All cards paid: {}", order.getOId(), allPaid);
        return allPaid;
    }
}