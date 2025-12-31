package com.casemgr.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.casemgr.entity.Order;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.enumtype.DeliveryStatus; // Assuming this exists
import com.casemgr.request.DeliveryItemRequest; // Placeholder DTO
import com.casemgr.response.DeliveryItemResponse; // Placeholder DTO

import jakarta.mail.Multipart;
import jakarta.persistence.EntityNotFoundException;

public interface DeliveryService {

    /**
     * Initializes delivery items for an order, potentially based on the template.
     * Called by OrderService during order creation.
     * @param order The newly created order.
     * @param template The template the order was based on.
     * @return A list of initialized delivery item details (might be empty).
     */
    void initializeDeliveryItems(Long cId);

    /**
     * Retrieves the list of delivery items associated with an order.
     * @param oId The order ID.
     * @return A list of delivery item details.
     * @throws EntityNotFoundException If the order is not found.
     */
    List<DeliveryItemResponse> getDeliveryItems(Long oId) throws EntityNotFoundException;
    
    List<DeliveryItemResponse> getDeliveryItems(String base62no) throws EntityNotFoundException;
    
    DeliveryItemResponse getDeliveryItem(Long oId) throws EntityNotFoundException;

    /**
     * Adds a new delivery item to an order (e.g., User A delivers something).
     * @param oId The order ID.
     * @param itemRequest Details of the delivery item to add.
     * @return Details of the newly added delivery item.
     * @throws EntityNotFoundException If the order is not found.
     */
    DeliveryItemResponse addDeliveryItem(Long oId, DeliveryItemRequest itemRequest) throws EntityNotFoundException;
    DeliveryItemResponse addDeliveryItem(String base62no, DeliveryItemRequest itemRequest) throws EntityNotFoundException;

    /**
     * Updates the status of a specific delivery item.
     * @param diId The delivery item ID.
     * @param status The new delivery status (e.g., Delivered, ModificationRequested, Accepted).
     * @param comment Optional comment (e.g., reason for modification request).
     * @return Updated delivery item details.
     * @throws EntityNotFoundException If the delivery item is not found.
     */
//    DeliveryItemResponse updateDeliveryStatus(Long diId, DeliveryStatus status) throws EntityNotFoundException;
    DeliveryItemResponse updateStatusToDelivered(Long diId) throws EntityNotFoundException;
    DeliveryItemResponse updateStatusToModificationRequested(Long diId,String comment) throws EntityNotFoundException;
    DeliveryItemResponse updateStatusToAccepted(Long diId, Boolean isFinal) throws EntityNotFoundException;
    
    
    DeliveryItemResponse uploadDeliveryFile(Long diId, MultipartFile file) throws EntityNotFoundException;

    void deleteDeliveryFile(String uuid) throws EntityNotFoundException;

    /**
     * Checks if all delivery items for an order have been accepted by the client.
     * @param oId The order ID.
     * @return true if all deliveries are accepted, false otherwise.
     * @throws EntityNotFoundException If the order is not found.
     */
    boolean checkAllDeliveriesAccepted(Long oId) throws EntityNotFoundException;

}