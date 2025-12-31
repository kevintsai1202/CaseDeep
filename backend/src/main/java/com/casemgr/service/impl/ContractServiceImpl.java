package com.casemgr.service.impl;

// Spring Framework imports
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casemgr.entity.Account;
// Project specific imports - Entities
import com.casemgr.entity.Block;
import com.casemgr.entity.Contract;
import com.casemgr.entity.ListItem;
import com.casemgr.entity.Order;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.entity.User;
import com.casemgr.enumtype.UserType; // Assuming UserRole is nested in User

// Project specific imports - Repositories
import com.casemgr.repository.BlockRepository;
import com.casemgr.repository.ContractRepository;
import com.casemgr.repository.OrderRepository;
// import com.casemgr.repository.UserRepository; // Needed for UserDetails lookup if principal is String
import com.casemgr.repository.OrderTemplateRepository;
// Project specific imports - Requests/Responses/Services/Enums/Converters
import com.casemgr.request.ContractChangeRequest;
import com.casemgr.request.InputBlockRequest;
import com.casemgr.response.BlockResponse;
import com.casemgr.response.ContractResponse;
import com.casemgr.service.ContractService;
import com.casemgr.service.DeliveryService;
import com.casemgr.service.OrderService; // Added import
import com.casemgr.service.PaymentService;
// import com.casemgr.service.NotificationService; // Optional
import com.casemgr.enumtype.OrderStatus;
import com.casemgr.converter.BlockConverter;
import com.casemgr.converter.ContractConverter; // Assuming this exists and works

// Jakarta Persistence
import jakarta.persistence.EntityNotFoundException;

// Lombok imports
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
// Java Util imports
import java.util.ArrayList;
import java.util.List;
import java.util.Date; // Explicit import for Date

@Service
@RequiredArgsConstructor // This will handle injecting all final fields
@Slf4j
public class ContractServiceImpl implements ContractService {

	private final ContractRepository contractRepository;
	private final OrderRepository orderRepository; // Keep for reading order if needed, but not for status update
	private final BlockRepository blockRepository;
	private final OrderTemplateRepository orderTemplateRepository;
	private PaymentService paymentService;
	private DeliveryService deliveryService;
	private OrderService orderService; // Added injection
	// private final UserRepository userRepository; // Inject if needed for username
	// lookup
	// private final NotificationService notificationService; // Inject if needed
	@Autowired
	public void setPaymentService(@Lazy PaymentService paymentService) {
		this.paymentService = paymentService;
	}
	
	@Autowired
	public void setDeliveryService(@Lazy DeliveryService deliveryService) {
		this.deliveryService = deliveryService;
	}
	
	@Autowired
	public void setOrderService(@Lazy OrderService orderService) {
		this.orderService = orderService;
	}

	@Override
	public ContractResponse addOrderTemplateItem(Long otId, InputBlockRequest inputBlock)
			throws EntityNotFoundException {
		OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + otId));

		Contract contract = orderTemplate.getContract();
		if (contract == null) {
			contract = new Contract();
			contract.setDescription("Template Contract");
			contract.setName("Template Contract");
			contract.setType("0");
			contract.setProvider(orderTemplate.getProvider());
			contract.setIndustry(orderTemplate.getIndustry());
			contract.setOrderTemplate(orderTemplate);
			contract = contractRepository.save(contract);
		}
		Block block = BlockConverter.INSTANCE.inputRequestToEntity(inputBlock);
//    	block.setContract(contract);
//    	block = blockRepository.save(block);
		List<Block> blocks = contract.getBlocks();
		blocks.add(block);
		contract.setBlocks(blocks);
		contract = contractRepository.save(contract);

		OrderTemplate finalOrderTemplate = orderTemplateRepository.getReferenceById(otId);
		return ContractConverter.INSTANCE.entityToResponse(finalOrderTemplate.getContract());
	}

	@Override
	public BlockResponse updateOrderTemplateItem(Long bId, InputBlockRequest inputBlock)
			throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContractResponse deleteOrderTemplateItem(Long otId, Long bId) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContractResponse addOrderItem(Long oId, InputBlockRequest inputBlock) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BlockResponse updateOrderItem(Long bId, InputBlockRequest inputBlock) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContractResponse deleteOrderItem(Long oId, Long bId) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public ContractResponse initializeContract(Order order, OrderTemplate template) {
		log.info("Initializing contract for Order ID: {}", order.getOId());
		Contract newContract = new Contract();
		Contract templateContract = template.getContract();
		Account newAccount = new Account();
		BeanUtils.copyProperties(order.getProvider().getReceivingAccount(), newAccount, "AId");

		newContract.setOrder(order);
		newContract.setProvider(order.getProvider());
		newContract.setClient(order.getClient());
		newContract.setType("1"); // Mark as active contract for the order
		newContract.setName(templateContract.getName());
		newContract.setDescription(templateContract.getDescription());
		newContract.setReceivingAccount(newAccount);

//		log.info("savedContract: {}", savedContract);

		List<Block> newBlocks = new ArrayList<>();
		if (templateContract.getBlocks() != null) {
			templateContract.getBlocks().forEach(block -> {
				Block newBlock = new Block();
				BeanUtils.copyProperties(block, newBlock, "bId", "orderTemplate", "order", "contract", "listItems");
				newBlock.setContract(newContract);

				List<ListItem> newListItems = new ArrayList<>();
				if (block.getListItems() != null) {
					block.getListItems().forEach(listItem -> {
						ListItem newListItem = new ListItem();
						BeanUtils.copyProperties(listItem, newListItem, "lId", "block", "blockSelected", "pricePackage",
								"settlement");
						newListItem.setBlock(newBlock);
						newListItem.setSelected(false);
						newListItems.add(newListItem);
					});
				}
				newBlock.setListItems(newListItems);
				blockRepository.save(newBlock);
//                newBlocks.add(newBlock);
			});
		}
//		newContract.setBlocks(newBlocks);
//		Contract savedContract = contractRepository.save(newContract);
//		log.info("Initialized Contract:{}", savedContract);

		return ContractConverter.INSTANCE.entityToResponse(newContract); // Use the saved instance
	}

	@Override
	@Transactional(readOnly = true)
	public ContractResponse getContractByOrderId(Long oId) throws EntityNotFoundException {
		// Find the order first to ensure it exists (still need OrderRepository for
		// this)
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		Contract contract = order.getContracts().stream().filter(c -> "1".equals(c.getType())).findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Active contract not found for order id: " + oId));

		// TODO: Implement and use ContractConverter
		// return ContractConverter.INSTANCE.entityToResponse(contract);
		return null;
	}

	@Override
	public ContractResponse generateContractPdf(Long contractId) throws EntityNotFoundException {
		log.warn("generateContractPdf not implemented yet for Contract ID: {}", contractId);
		// TODO: Implement PDF generation logic
		return null;
	}

	@Override
	@Transactional
	public ContractResponse signContract(Long contractId, UserType signerRole) throws EntityNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();

		Contract contract = contractRepository.findById(contractId)
				.orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + contractId));

		if ((UserType.CLIENT.equals(signerRole)) && contract.getClient().getUId().equals(user.getUId())) {
			contract.setClientSigned(true);
			contract.setClientSignatureTime(Instant.now());
			contract.setClientSignatureUrl(user.getSignatureUrl());
			contract.setRevisionDate(new Date());
		} else if ((signerRole == UserType.PROVIDER) && contract.getProvider().getUId().equals(user.getUId())) {
			contract.setProviderSigned(true);
			contract.setProviderSignatureTime(Instant.now());
			contract.setProviderSignatureUrl(user.getSignatureUrl());
			contract.setRevisionDate(new Date());
		} else {
			throw new SecurityException("User is not authorized to sign this contract as " + signerRole);
		}
		log.info("Signing contract ID: {} by Role: {}", contractId, signerRole);

		contract = contractRepository.save(contract);

		// Check if both signed and update Order status VIA OrderService
		if (Boolean.TRUE.equals(contract.getClientSigned()) && Boolean.TRUE.equals(contract.getProviderSigned())) {
			log.info("Both parties have signed Contract ID: {}. Updating Order ID: {} status via OrderService.",
					contractId, contract.getOrder().getOId());
			
			paymentService.initializePaymentCards(contractId);
			deliveryService.initializeDeliveryItems(contractId);
			
			orderService.updateOrderStatus(contract.getOrder().getOId(), OrderStatus.awaiting_payment,
					"Contract signed by both parties");
			// No need to save order here, OrderService should handle it
		}

		return ContractConverter.INSTANCE.entityToResponse(contract);
//		return null;
	}

	@Override
	@Transactional
	public ContractResponse requestContractChange(Long contractId, ContractChangeRequest changeRequest)
			throws EntityNotFoundException {
		Contract contract = contractRepository.findById(contractId)
				.orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + contractId));
		Order order = contract.getOrder();
		if (order == null) {
			throw new IllegalStateException("Contract with id " + contractId + " is not associated with an order.");
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long currentUserId = getCurrentUserId(auth);

		UserType requesterRole;
		if (order.getClient() != null && order.getClient().getUId().equals(currentUserId)) {
			requesterRole = UserType.CLIENT;
		} else if (order.getProvider() != null && order.getProvider().getUId().equals(currentUserId)) {
			requesterRole = UserType.PROVIDER;
		} else {
			throw new SecurityException("User is not authorized to request changes for this contract.");
		}

		if (!OrderStatus.inquiry.equals(order.getStatus()) && !OrderStatus.awaiting_payment.equals(order.getStatus())) {
			throw new IllegalStateException(
					"Cannot request contract change in the current order status: " + order.getStatus());
		}

		log.info("Requesting contract change for Contract ID: {} by Role: {}", contractId, requesterRole);

		contract.setChangeReason(changeRequest.getChangeReason());
		contract.setProposedChangesText(changeRequest.getProposedChangesText());
		contract.setChangeRequesterRole(requesterRole);
		contract.setRevisionDate(new Date());

		// Save contract changes first
		contractRepository.save(contract);

		// Update Order Status VIA OrderService
		orderService.updateOrderStatus(order.getOId(), OrderStatus.inquiry,
				"Contract change requested by " + requesterRole);
		// No need to save order here

		// Optional Notification Logic
		// ...

		return ContractConverter.INSTANCE.entityToResponse(contract);
//		return null;
	}

	@Override
	@Transactional
	public ContractResponse approveContractChange(Long contractId) throws EntityNotFoundException {
		Contract contract = contractRepository.findById(contractId)
				.orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + contractId));
		Order order = contract.getOrder();
		if (order == null) {
			throw new IllegalStateException("Contract with id " + contractId + " is not associated with an order.");
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long currentUserId = getCurrentUserId(auth);
		UserType requesterRole = contract.getChangeRequesterRole();

		if (requesterRole == null) {
			throw new IllegalStateException("Cannot approve change as no change was formally requested.");
		}

		boolean authorized = false;
		if (requesterRole == UserType.CLIENT && order.getProvider() != null
				&& order.getProvider().getUId().equals(currentUserId)) {
			authorized = true;
		} else if (requesterRole == UserType.PROVIDER && order.getClient() != null
				&& order.getClient().getUId().equals(currentUserId)) {
			authorized = true;
		}
		if (!authorized) {
			throw new SecurityException("User is not authorized to approve changes for this contract.");
		}

		if (!OrderStatus.inquiry.equals(order.getStatus())) {
			throw new IllegalStateException(
					"Cannot approve contract change in the current status: " + order.getStatus());
		}

		log.info("Approving contract change for Contract ID: {}", contractId);

		// Clear Change Request Fields & Save Contract
		contract.setChangeReason(null);
		contract.setProposedChangesText(null);
		contract.setChangeRequesterRole(null);
		contract.setRevisionDate(new Date());
		contractRepository.save(contract);

		// Update Order Status VIA OrderService
		orderService.updateOrderStatus(order.getOId(), OrderStatus.awaiting_payment, "Contract change approved");
		// No need to save order here

		// Optional Notification Logic
		// ...
		return ContractConverter.INSTANCE.entityToResponse(contract);
//		return null;
	}

	@Override
	@Transactional
	public ContractResponse rejectContractChange(Long contractId) throws EntityNotFoundException {
		Contract contract = contractRepository.findById(contractId)
				.orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + contractId));
		Order order = contract.getOrder();
		if (order == null) {
			throw new IllegalStateException("Contract with id " + contractId + " is not associated with an order.");
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Long currentUserId = getCurrentUserId(auth);
		UserType requesterRole = contract.getChangeRequesterRole();

		if (requesterRole == null) {
			throw new IllegalStateException("Cannot reject change as no change was formally requested.");
		}

		boolean authorized = false;
		if (requesterRole == UserType.CLIENT && order.getProvider() != null
				&& order.getProvider().getUId().equals(currentUserId)) {
			authorized = true;
		} else if (requesterRole == UserType.PROVIDER && order.getClient() != null
				&& order.getClient().getUId().equals(currentUserId)) {
			authorized = true;
		}
		if (!authorized) {
			throw new SecurityException("User is not authorized to reject changes for this contract.");
		}

		if (!OrderStatus.inquiry.equals(order.getStatus())) {
			throw new IllegalStateException(
					"Cannot reject contract change in the current status: " + order.getStatus());
		}

		log.info("Rejecting contract change for Contract ID: {}", contractId);

		// Clear Change Request Fields & Save Contract
		contract.setChangeReason(null);
		contract.setProposedChangesText(null);
		contract.setChangeRequesterRole(null);
		contract.setRevisionDate(new Date());
		contractRepository.save(contract);

		// Update Order Status VIA OrderService
		// Revert to the status before the change request (likely ContractSigned)
		orderService.updateOrderStatus(order.getOId(), OrderStatus.inquiry, "Contract change rejected");
		// No need to save order here

		// Optional Notification Logic
		// ...
		return ContractConverter.INSTANCE.entityToResponse(contract);
//		return null;
	}

	@Override
	@Transactional
	public void skipContract(Long oId) throws EntityNotFoundException {
		// Find the order first to ensure it exists
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		// TODO: Add authorization checks

		if (!OrderStatus.inquiry.equals(order.getStatus())) {
			throw new IllegalStateException("Cannot skip contract in status: " + order.getStatus());
		}

		// Update order status VIA OrderService
		orderService.updateOrderStatus(oId, OrderStatus.awaiting_payment, "Contract skipped");
		// No need to save order here

		log.info("Skipped contract phase for Order ID: {}", oId);
	}

	// Helper method to get User ID from Authentication principal
	private Long getCurrentUserId(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new SecurityException("User is not authenticated.");
		}
		Object principal = authentication.getPrincipal();

		if (principal instanceof User) {
			return ((User) principal).getUId();
		} else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
			log.warn("Principal is UserDetails but not the expected User entity type. Cannot extract ID directly.");
			// TODO: Implement user lookup by username if needed, requires UserRepository
			// injection
			// String username =
			// ((org.springframework.security.core.userdetails.UserDetails)
			// principal).getUsername();
			// User user = userRepository.findByUsername(username).orElseThrow(() -> new
			// SecurityException("User not found for principal"));
			// return user.getId();
			throw new SecurityException("Cannot determine user ID from UserDetails principal without lookup.");
		} else if (principal instanceof String) {
			log.warn("Principal is a String (username?). Cannot extract ID directly without user lookup.");
			throw new SecurityException("Cannot determine user ID from String principal.");
		} else {
			log.error("Unexpected principal type: {}", principal.getClass().getName());
			throw new SecurityException("Could not determine current user ID from principal.");
		}
	}



}