package com.casemgr.service.impl;

import java.util.ArrayList;
import java.util.Date; // Added for setting delivery date
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication; // Added for authorization
import org.springframework.security.core.context.SecurityContextHolder; // Added for authorization
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.entity.Block;
import com.casemgr.entity.Contract;
import com.casemgr.entity.DeliveryItem;
import com.casemgr.entity.Filedata;
import com.casemgr.entity.Order;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.entity.PaymentCard;
import com.casemgr.entity.User; // Added for authorization
import com.casemgr.enumtype.BlockType;
import com.casemgr.enumtype.DeliveryStatus;
import com.casemgr.enumtype.OrderStatus; // Added for status checks
import com.casemgr.repository.ContractRepository;
import com.casemgr.repository.DeliveryItemRepository;
import com.casemgr.repository.OrderRepository;
import com.casemgr.request.DeliveryItemRequest;
import com.casemgr.response.DeliveryItemResponse;
import com.casemgr.response.FiledataResponse;
import com.casemgr.service.DeliveryService;
import com.casemgr.service.FileStorageService;
import com.casemgr.service.OrderService; // Added import
import com.casemgr.utils.Base62Utils;
import com.casemgr.converter.DeliveryItemConverter; // Assuming this exists
// May need FileStorageService if handling file uploads here
// import com.casemgr.service.FileStorageService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor // Handles injection of final fields
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

	private final DeliveryItemRepository deliveryItemRepository;
	private final OrderRepository orderRepository; // Keep for reading order
	private final OrderService orderService; // Added injection
	private final ContractRepository contractRepository;
	private final FileStorageService fileStorageService;
	// private final FileStorageService fileStorageService; // Inject if needed

	@Override
	@Transactional
	public void initializeDeliveryItems(Long cId) {
		log.info("Initializing payment cards for Contract ID: {}", cId);
//      Order order = orderRepository.getReferenceById(oId);
		Contract contract = contractRepository.getReferenceById(cId);
		Order order = contract.getOrder();
		List<DeliveryItem> deliveryItems = new ArrayList<>();
		List<Block> blocks = contract.getBlocks();
		Block deliveryBlock = blocks.stream().filter(block -> BlockType.delivery.equals(block.getType())).findFirst()
				.orElse(null);

		DeliveryItem newItem = new DeliveryItem();
		newItem.setOrder(order);
		newItem.setDescription(deliveryBlock.getContext());
//      newItem.setFileUrl(itemRequest.getFileUrl()); // TODO: Handle file upload via FileStorageService?
		newItem.setStatus(DeliveryStatus.Pending);

		deliveryItems.add(newItem);

		order.getDeliveries().addAll(deliveryItems);

		orderRepository.save(order);
//      return DeliveryItemConverter.INSTANCE.entityToResponse(deliveryItems);
	}

	@Override
	public DeliveryItemResponse getDeliveryItem(Long diId) throws EntityNotFoundException {
		DeliveryItem deliveryItem = deliveryItemRepository.getReferenceById(diId);

		return DeliveryItemConverter.INSTANCE.entityToResponse(deliveryItem);
	}

	@Override
	public List<DeliveryItemResponse> getDeliveryItems(String base62no) throws EntityNotFoundException {
		String orderNo = Base62Utils.decode(base62no);
		log.info("orderNo:{}", orderNo);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		log.info("order:{}", order);
		return getDeliveryItems(order.getOId());
	}

	@Override
	@Transactional(readOnly = true)
	public List<DeliveryItemResponse> getDeliveryItems(Long oId) throws EntityNotFoundException {
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		List<DeliveryItem> items = order.getDeliveries();
		log.info("delivery items:{}", items);

		if (items == null) {
			return new ArrayList<>();
		}

		// TODO: Implement and use DeliveryItemConverter
		// return DeliveryItemConverter.INSTANCE.entityToResponse(items);
		return DeliveryItemConverter.INSTANCE.entityToResponse(items); // Placeholder
	}

	@Override
	@Transactional
	public DeliveryItemResponse addDeliveryItem(String base62no, DeliveryItemRequest itemRequest)
			throws EntityNotFoundException {
		String orderNo = Base62Utils.decode(base62no);
		log.info("orderNo:{}", orderNo);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		log.info("order:{}", order);
		return addDeliveryItem(order.getOId(), itemRequest);
	}

	@Override
	@Transactional
	public DeliveryItemResponse addDeliveryItem(Long oId, DeliveryItemRequest itemRequest)
			throws EntityNotFoundException {
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		// --- 1. Authorization Check ---
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) auth.getPrincipal(); // Assuming User implements UserDetails
		if (order.getProvider() == null || !order.getProvider().getUId().equals(currentUser.getUId())) {
			throw new SecurityException("User is not authorized to add delivery items to this order.");
		}
		// TODO: Implement proper authorization check

		// --- 2. Status Check (Optional) ---
		if (!OrderStatus.awaiting_payment.equals(order.getStatus())
				&& !OrderStatus.in_progress.equals(order.getStatus())
				&& !OrderStatus.in_revision.equals(order.getStatus())) {
			throw new IllegalStateException(
					"Cannot add delivery item in the current order status: " + order.getStatus());
		}

		// --- 3. Create DeliveryItem from Request ---
		DeliveryItem newItem = new DeliveryItem();
		newItem.setOrder(order);
		newItem.setDescription(itemRequest.getDescription());
//        newItem.setFileUrl(itemRequest.getFileUrl()); // TODO: Handle file upload via FileStorageService?
		newItem.setStatus(DeliveryStatus.Pending);
		newItem.setDeliveryDate(new Date());

		// --- 4. Save ---
		newItem = deliveryItemRepository.save(newItem);
		log.info("Added new DeliveryItem ID: {} to Order ID: {}", newItem.getDiId(), oId);

		// --- 5. Update Order Status VIA OrderService (if applicable) ---
		// Re-fetch order to check current status within transaction
//        Order currentOrder = orderRepository.findById(oId)
//             .orElseThrow(() -> new EntityNotFoundException("Order not found after adding delivery item: " + oId));
//        if (currentOrder.getStatus() == OrderStatus.in_progress) {
//            log.info("First delivery item added for Order ID: {}. Updating status to PartiallyDelivered via OrderService.", oId);
//            orderService.updateOrderStatus(oId, OrderStatus.delivered, "First delivery item added");
//        }

		// --- 6. Return Response ---
		// TODO: Implement and use DeliveryItemConverter
		return DeliveryItemConverter.INSTANCE.entityToResponse(newItem);
//        return null; // Placeholder
	}

	@Override
	@Transactional
	public DeliveryItemResponse uploadDeliveryFile(Long diId, MultipartFile file) throws EntityNotFoundException {
		DeliveryItem di = deliveryItemRepository.findById(diId)
				.orElseThrow(() -> new EntityNotFoundException("DeliveryItem not found with id: " + diId));

		Filedata fileData = fileStorageService.save(file);
//		FiledataResponse fileResponse = new FiledataResponse(fileData);

//		Block block = new Block();
//		block.setContext(fileData.getFileName());
//		block.setFileData(fileData);
//		block.setDeliveryItem(di);
		fileData.setDeliveryItem(di);
		di.getDeliveries().add(fileData);

		di = deliveryItemRepository.save(di);

		return DeliveryItemConverter.INSTANCE.entityToResponse(di);
	}

//    @Override
//    @Transactional
//    public DeliveryItemResponse updateDeliveryStatus(Long diId, DeliveryStatus status, String comment) throws EntityNotFoundException {
//        DeliveryItem item = deliveryItemRepository.findById(diId)
//             .orElseThrow(() -> new EntityNotFoundException("DeliveryItem not found with id: " + diId));
//        
//        // --- 1. Authorization Check ---
//        // TODO: Implement proper authorization (likely client accepts/requests modification)
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        // User currentUser = (User) auth.getPrincipal();
//        Order order = item.getOrder();
//        // if (order == null || order.getClient() == null || !order.getClient().getId().equals(currentUser.getId())) {
//        //      throw new SecurityException("User is not authorized to update the status of this delivery item.");
//        // }
//
//        // --- 2. Status Transition Validation (Optional) ---
//        // ...
//
//        log.info("Updating status for DeliveryItem ID: {} to {}", diId, status);
//        item.setStatus(status);
//
//        if (status == DeliveryStatus.ModificationRequested) {
//            if (comment == null || comment.isBlank()) {
//                 throw new IllegalArgumentException("A comment is required when requesting modification.");
//            }
//            item.setModificationRequestComment(comment);
//        } else {
//            item.setModificationRequestComment(null);
//        }
//
//        // --- 3. Save Item ---
//        item = deliveryItemRepository.save(item);
//
//        // --- 4. Check if all deliveries are accepted and update Order status VIA OrderService ---
//        if (order != null && status == DeliveryStatus.Accepted) {
//             // Re-fetch order to ensure status is current
//             Order currentOrder = orderRepository.findById(order.getOId())
//                 .orElseThrow(() -> new EntityNotFoundException("Order not found when checking delivery completion: " + order.getOId()));
//
//            if (checkAllDeliveriesAcceptedInternal(currentOrder)) { // Use internal check
//                 log.info("All delivery items accepted for Order ID: {}. Updating order status to RatingPending via OrderService.", currentOrder.getOId());
//                 orderService.updateOrderStatus(currentOrder.getOId(), OrderStatus.completed, "All deliveries accepted");
//            }
//            // No explicit status change needed if only partially accepted, PartiallyDelivered is handled by addDeliveryItem
//        }
//
//        // --- 5. Return Response ---
//        // TODO: Implement and use DeliveryItemConverter
//         return DeliveryItemConverter.INSTANCE.entityToResponse(item);
////        return null; // Placeholder
//    }

	@Override
	@Transactional(readOnly = true)
	public boolean checkAllDeliveriesAccepted(Long oId) throws EntityNotFoundException {
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));
		return checkAllDeliveriesAcceptedInternal(order);
	}

	// Internal helper to avoid re-fetching order
	private boolean checkAllDeliveriesAcceptedInternal(Order order) {
		List<DeliveryItem> items = order.getDeliveries();
		if (items == null || items.isEmpty()) {
			log.info("No delivery items found for Order ID: {}. Considering deliveries accepted.", order.getOId());
			return true; // Or false depending on business logic
		}
		boolean allAccepted = items.stream().allMatch(item -> DeliveryStatus.Accepted.equals(item.getStatus()));
		log.info("Checked delivery acceptance for Order ID: {}. All items accepted: {}", order.getOId(), allAccepted);
		return allAccepted;
	}

	@Override
	@Transactional
	public void deleteDeliveryFile(String uuid) {
		fileStorageService.delete(uuid);
	}

	@Override
	public DeliveryItemResponse updateStatusToDelivered(Long diId) throws EntityNotFoundException {
		DeliveryItem deliveryItem = deliveryItemRepository.findById(diId)
				.orElseThrow(() -> new EntityNotFoundException("DeliveryItem not found with id: " + diId));
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currUser = (User) auth.getPrincipal();
		Order order = deliveryItem.getOrder();
		if (order == null || order.getProvider() == null || !order.getProvider().getUId().equals(currUser.getUId())) {
			throw new SecurityException("User is not authorized to update the status of this delivery item.");
		}

		// --- 2. Status Transition Validation (Optional) ---
		// ...

		log.info("Updating status for DeliveryItem ID: {} to {}", diId, DeliveryStatus.Delivered);
		deliveryItem.setStatus(DeliveryStatus.Delivered);

		// --- 3. Save Item ---
		deliveryItem = deliveryItemRepository.save(deliveryItem);
		return DeliveryItemConverter.INSTANCE.entityToResponse(deliveryItem);
	}

	@Override
	public DeliveryItemResponse updateStatusToModificationRequested(Long diId, String comment)
			throws EntityNotFoundException {
		DeliveryItem deliveryItem = deliveryItemRepository.findById(diId)
				.orElseThrow(() -> new EntityNotFoundException("DeliveryItem not found with id: " + diId));
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currUser = (User) auth.getPrincipal();
		Order order = deliveryItem.getOrder();
		if (order == null || order.getClient() == null || !order.getClient().getUId().equals(currUser.getUId())) {
			throw new SecurityException("User is not authorized to update the status of this delivery item.");
		}

		// --- 2. Status Transition Validation (Optional) ---
		// ...

		log.info("Updating status for DeliveryItem ID: {} to {}", diId, DeliveryStatus.ModificationRequested);
		deliveryItem.setStatus(DeliveryStatus.ModificationRequested);
		deliveryItem.setModificationRequestComment(comment);

		// --- 3. Save Item ---
		deliveryItem = deliveryItemRepository.save(deliveryItem);
		return DeliveryItemConverter.INSTANCE.entityToResponse(deliveryItem);
	}

	@Override
	public DeliveryItemResponse updateStatusToAccepted(Long diId, Boolean isFinal) throws EntityNotFoundException {
		DeliveryItem deliveryItem = deliveryItemRepository.findById(diId)
				.orElseThrow(() -> new EntityNotFoundException("DeliveryItem not found with id: " + diId));
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currUser = (User) auth.getPrincipal();
		Order order = deliveryItem.getOrder();
		if (order == null || order.getClient() == null || !order.getClient().getUId().equals(currUser.getUId())) {
			throw new SecurityException("User is not authorized to update the status of this delivery item.");
		}

		// --- 2. Status Transition Validation (Optional) ---
		// ...

		log.info("Updating status for DeliveryItem ID: {} to {}", diId, DeliveryStatus.Accepted);
		deliveryItem.setStatus(DeliveryStatus.Accepted);
	
	

		// --- 3. Save Item ---
		deliveryItem = deliveryItemRepository.save(deliveryItem);
		
		if (deliveryItem.getIsFinal()) {
			order.setStatus(OrderStatus.delivered);
			orderRepository.save(order);
		}
		
		return DeliveryItemConverter.INSTANCE.entityToResponse(deliveryItem);
	}

}