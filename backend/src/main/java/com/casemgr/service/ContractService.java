package com.casemgr.service;

import com.casemgr.entity.Order;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.enumtype.UserType;
import com.casemgr.request.ContractChangeRequest; // Placeholder DTO
import com.casemgr.request.InputBlockRequest;
import com.casemgr.response.BlockResponse;
import com.casemgr.response.ContractResponse; // Placeholder DTO

import jakarta.persistence.EntityNotFoundException;

public interface ContractService {

    /**
     * Initializes a contract based on an order and its template.
     * Called by OrderService during order creation.
     * @param order The newly created order.
     * @param template The template the order was based on.
     * @return The initialized contract details.
     */
    ContractResponse initializeContract(Order order, OrderTemplate template);

    ContractResponse addOrderItem(Long oId, InputBlockRequest inputBlock) throws EntityNotFoundException;
    
    BlockResponse updateOrderItem(Long bId, InputBlockRequest inputBlock) throws EntityNotFoundException;
    
    ContractResponse deleteOrderItem(Long oId, Long bId) throws EntityNotFoundException;
    
    ContractResponse addOrderTemplateItem(Long otId, InputBlockRequest inputBlock) throws EntityNotFoundException;
    
    BlockResponse updateOrderTemplateItem(Long bId, InputBlockRequest inputBlock) throws EntityNotFoundException;
    
    ContractResponse deleteOrderTemplateItem(Long otId, Long bId) throws EntityNotFoundException;
    
    /**
     * Retrieves contract details for a specific order.
     * @param oId The order ID.
     * @return The contract details.
     * @throws EntityNotFoundException If the order or contract is not found.
     */
    ContractResponse getContractByOrderId(Long oId) throws EntityNotFoundException;

    /**
     * Generates a PDF representation of the contract. (Optional)
     * @param contractId The contract ID.
     * @return ContractResponse potentially including a link to the PDF or PDF data.
     * @throws EntityNotFoundException If the contract is not found.
     */
    ContractResponse generateContractPdf(Long contractId) throws EntityNotFoundException; // (Optional)

    /**
     * Records a signature on the contract.
     * @param contractId The contract ID.
     * @param signerRole The role of the signer (e.g., CLIENT, PROVIDER).
     * @param signatureUrl The URL or identifier for the signature image.
     * @return Updated contract details.
     * @throws EntityNotFoundException If the contract is not found.
     */
    ContractResponse signContract(Long contractId, UserType signerRole) throws EntityNotFoundException;

    /**
     * Initiates a request to change the contract terms.
     * @param contractId The contract ID.
     * @param changeRequest Details of the requested changes.
     * @return Updated contract details reflecting the change request.
     * @throws EntityNotFoundException If the contract is not found.
     */
    ContractResponse requestContractChange(Long contractId, ContractChangeRequest changeRequest) throws EntityNotFoundException;

    /**
     * Approves a previously requested contract change.
     * @param contractId The contract ID.
     * @return Updated contract details with approved changes.
     * @throws EntityNotFoundException If the contract is not found.
     */
    ContractResponse approveContractChange(Long contractId) throws EntityNotFoundException;

    /**
     * Rejects a previously requested contract change.
     * @param contractId The contract ID.
     * @return Updated contract details reflecting the rejection.
     * @throws EntityNotFoundException If the contract is not found.
     */
    ContractResponse rejectContractChange(Long contractId) throws EntityNotFoundException;

    /**
     * Handles the logic when the contract step is skipped for an order.
     * Primarily updates the associated Order's status.
     * @param oId The order ID.
     * @throws EntityNotFoundException If the order is not found.
     */
    void skipContract(Long oId) throws EntityNotFoundException; // Updates Order status via OrderService or directly

}