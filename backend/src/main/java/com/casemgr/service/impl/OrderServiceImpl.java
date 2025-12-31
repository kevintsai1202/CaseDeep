package com.casemgr.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy; // Added import for @Lazy
import org.springframework.data.domain.Page; // Added for potential pagination
import org.springframework.data.domain.PageImpl; // Added for potential pagination
import org.springframework.data.domain.Pageable; // Added for potential pagination
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casemgr.converter.BlockConverter;
import com.casemgr.converter.ListItemConverter;
import com.casemgr.converter.OrderConverter;
import com.casemgr.converter.PaymentCardConverter;
import com.casemgr.entity.Account;
import com.casemgr.entity.Block;
import com.casemgr.entity.Contract;
import com.casemgr.entity.Discount;
import com.casemgr.entity.ListItem;
import com.casemgr.entity.Order;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.entity.PaymentCard;
import com.casemgr.entity.User;
import com.casemgr.enumtype.BlockType;
import com.casemgr.enumtype.ContractStatus;
import com.casemgr.enumtype.DiscountType;
import com.casemgr.enumtype.OrderStatus;
import com.casemgr.enumtype.OrderTemplateSelectType;
import com.casemgr.enumtype.OrderType;
import com.casemgr.repository.BlockRepository;
import com.casemgr.repository.DiscountRepository; // Keep if used in calculateTotalPrice
import com.casemgr.repository.IndustryRepository; // Keep if needed elsewhere
import com.casemgr.repository.ListItemRepository;
import com.casemgr.repository.OrderRepository;
import com.casemgr.repository.OrderTemplateRepository;
import com.casemgr.repository.UserRepository;
// Assuming ListItemRepository might be needed later
// import com.casemgr.repository.ListItemRepository;
import com.casemgr.request.OrderCreateRequest;
import com.casemgr.request.OrderListRequest; // Added import
import com.casemgr.request.BlockRequest;
import com.casemgr.request.BlockSelectRequest;
import com.casemgr.request.BlockUpdateRequest;
import com.casemgr.response.BlockResponse;
import com.casemgr.response.ListItemResponse;
import com.casemgr.response.OrderCreateResponse;
import com.casemgr.response.OrderResponse;
import com.casemgr.response.PaymentCardResponse;
import com.casemgr.service.BlockService;
import com.casemgr.service.ContractService;
import com.casemgr.service.DeliveryService;
import com.casemgr.service.EvaluateService; // Added import
// import com.casemgr.service.FileStorageService; // Keep if needed elsewhere - Temporarily commented out
import com.casemgr.service.OrderService;
import com.casemgr.service.PaymentService;
import com.casemgr.utils.Base62Utils;
import com.casemgr.utils.NumberUtils;
import com.casemgr.request.ContractChangeRequest; // Added missing import from interface
import com.casemgr.request.InputBlockRequest;
import com.casemgr.request.ListItemRequest;
import com.casemgr.request.PaymentCardRequest; // Added missing import from interface
import com.casemgr.enumtype.PaymentStatus; // Added missing import from interface
import com.casemgr.enumtype.UserType;
// import com.casemgr.utils.NumberUtils; // Temporarily commented out
import com.casemgr.exception.BusinessException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor // Note: Will fail if FileStorageService remains commented out and is required
@Slf4j
public class OrderServiceImpl implements OrderService {
	private final OrderTemplateRepository orderTemplateRepository;
	private final DiscountRepository discountRepository; // Keep if used in calculateTotalPrice
	private final UserRepository userRepository;
	private final IndustryRepository industryRepository; // Keep if needed elsewhere
	// private final FileStorageService fileStorageService; // Keep if needed
	// elsewhere - Temporarily commented out
	private final BlockRepository blockRepository; // Keep if needed within methods like updateConfirmation
	private final ListItemRepository listItemRepository;
	private final OrderRepository orderRepository;
	@Lazy // Added @Lazy to break circular dependency
	private final ContractService contractService;
	private final BlockService blockService;

	private final EvaluateService evaluateService; // Added dependency

	private PaymentService paymentService;
	private DeliveryService deliveryService;
	// Assuming ListItemRepository might be needed later
	// private final ListItemRepository listItemRepository;

	@Autowired
	public void setPaymentService(@Lazy PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@Autowired
	public void setDeliveryService(@Lazy DeliveryService deliveryService) {
		this.deliveryService = deliveryService;
	}

	// Added stub for update method from interface
	@Override
	public OrderResponse update(Long oId, OrderCreateRequest orderRequest) throws EntityNotFoundException {
		log.warn("Method 'update' not fully implemented yet.");
		// TODO: Implement full update logic based on OrderService interface definition
		throw new UnsupportedOperationException("Method 'update' not implemented yet.");
	}

	@Override
	public OrderResponse getOrderById(Long oId) throws EntityNotFoundException { // Renamed from detail
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));
		// Ensure the converter handles lazy loading if necessary
		return OrderConverter.INSTANCE.entityToResponse(order);
	}

	@Override
    public List<OrderResponse> listOrders() { // Renamed from list and added parameter
		// TODO: Implement proper listing with filtering/pagination/sorting based on
		// listRequest
		// Example: Use Spring Data JPA Specification or Querydsl for dynamic filtering
		// Example: Use listRequest.getPageable() for pagination
		log.warn("listOrders currently returns all orders. Filtering/Pagination not implemented yet.");
        List<Order> orders = orderRepository.findAll(); // Basic implementation for now
        return orders.stream()
                .map(OrderConverter.INSTANCE::entityToResponse)
                .collect(Collectors.toList());
    }

	// Added stub implementations for methods from OrderService interface but not in
	// spec.md
	@Override
	public List<OrderResponse> listOrderByCategory(Long caId) throws EntityNotFoundException {
		log.warn("Method 'listOrderByCategory' not fully implemented yet.");
		// TODO: Implement logic based on OrderService interface definition
		throw new UnsupportedOperationException("Method 'listOrderByCategory' not implemented yet.");
	}

	@Override
	public List<OrderResponse> listOrderByProvider(Long providerId) throws EntityNotFoundException {
		log.warn("Method 'listOrderByProvider' not fully implemented yet.");
		// TODO: Implement logic based on OrderService interface definition
		throw new UnsupportedOperationException("Method 'listOrderByProvider' not implemented yet.");
	}

	// --- Admin Specific Listing Implementation ---
	@Override
	public Page<OrderResponse> listOrdersForAdmin(Pageable pageable) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
			return Page.empty(pageable);
		}

		User currentUser = (User) auth.getPrincipal();

		boolean isSuperAdmin = currentUser.getAuthorities().stream()
				.anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPER_ADMIN"));

		Page<Order> orderPage;
		if (isSuperAdmin) {
			// Super Admin sees all orders
			orderPage = orderRepository.findAll(pageable);
		} else {
			// Regional admin sees orders where either provider or client is in their region
			String adminRegion = currentUser.getRegion();
			if (adminRegion == null || adminRegion.isEmpty()) {
				log.warn("Admin user {} has no region assigned, cannot filter orders.", currentUser.getUsername());
				return Page.empty(pageable);
			}
			// Need to add findByProviderRegionOrClientRegion method to OrderRepository
			// Pass the admin's region to both parameters
			orderPage = orderRepository.findByProviderRegionOrClientRegion(adminRegion, adminRegion, pageable);
		}

		// Convert Page<Order> to Page<OrderResponse>
		return orderPage.map(OrderResponse::new);
	}

	@Override
	@Transactional
    public void deleteOrder(Long oId) throws EntityNotFoundException { // Renamed from delete
        // 依測試期望：findById + delete(entity)
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));
        orderRepository.delete(order);
        log.info("Deleted Order ID: {}", oId);
    }

	@Override
	@Transactional
	public OrderCreateResponse createFromTemplate(OrderCreateRequest orderReq) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Ensure principal is User type or handle ClassCastException

		OrderTemplate orderTemp = orderTemplateRepository.findById(orderReq.getOrderTemplateId()) // Use findById for
				.orElseThrow(() -> new EntityNotFoundException(
						"OrderTemplate not found with id: " + orderReq.getOrderTemplateId()));
		User client = (User) auth.getPrincipal();
		User provider = orderTemp.getProvider();
		String orderNo = NumberUtils.generateFormNumber("CO"); // Temporarily commented out
//        String orderNo = "CO-" + System.currentTimeMillis(); // Placeholder number generation
		Order order = new Order();
		order.setOrderType(orderReq.getOrderType());
		order.setName(orderReq.getName());
		order.setOrderNo(orderNo);
		order.setImageUrl(orderTemp.getImageUrl());
		order.setStatus(OrderStatus.inquiry); // Correct initial status
		order.setOrderTemplate(orderTemp);
		order.setProvider(provider);
		order.setClient(client);
		order.setIndustry(orderTemp.getIndustry());
        // Calculate initial total price including discounts BEFORE saving
        BigDecimal initialTotalPrice = calculateTotalPrice(order);
        order.setTotalPrice(initialTotalPrice);
        // 追蹤建立訂單時使用之模板與關鍵欄位，便於除錯
        List<String> pmSafe = orderTemp.getPaymentMethods() != null ? orderTemp.getPaymentMethods() : Collections.emptyList();
        log.info(
            "CreateFromTemplate: orderNo={}, templateId={}, providerId={}, clientId={}, paymentMethods={}, deliveryType={}, businessDays={}, startingPrice={}, initialTotalPrice={}",
            orderNo,
            orderTemp.getOtId(),
            provider != null ? provider.getUId() : null,
            client != null ? client.getUId() : null,
            pmSafe,
            orderTemp.getDeliveryType(),
            orderTemp.getBusinessDays(),
            orderTemp.getStartingPrice(),
            initialTotalPrice
        );
        log.info("Calculated initial total price for Order No: {} as {}", order.getOrderNo(), initialTotalPrice);
		
		// Deep copy blocks and list items
		List<Block> newBlocks = new ArrayList<>();
		if (orderTemp.getBlocks() != null) {
			orderTemp.getBlocks().forEach(block -> {
				Block newBlock = new Block();
				// Copy relevant properties, avoid copying ID and associations managed elsewhere
				BeanUtils.copyProperties(block, newBlock, "BId", "orderTemplate", "order", "contract", "listItems",
						"selectedListItems");
				log.info("newBlock:{}", newBlock);
				// Ensure name is not null if the source template block name was null
				newBlock.setOrder(order);
//                newBlock.setOrder(savedOrder); // Set the back-reference to the new order

//                log.info("New Block:{}",newBlock);
				List<ListItem> newListItems = new ArrayList<>();
				if (block.getListItems() != null) {

					block.getListItems().forEach(listItem -> {
						ListItem newListItem = new ListItem();
						// Copy relevant properties, avoid copying ID and associations
						BeanUtils.copyProperties(listItem, newListItem, "LId", "block", "blockSelected", "pricePackage",
								"settlement");
						log.info("newListItem:{}", newListItem);
						// Ensure name is not null if the source template list item name was null
						newListItem.setBlock(newBlock); // Set the back-reference to the new block
						newListItems.add(newListItem);
					});
//                    listItemRepository.saveAll(newListItems);   

				}
				newBlock.setListItems(newListItems);
//                blockRepository.save(newBlock);
//                newBlock.setListItems(newListItems); // Set the copied list items
				newBlocks.add(newBlock);
			});
		}
		
		Block paymentBlock = new Block();
		paymentBlock.setName("PAYMENT");
		paymentBlock.setContext("PAYMENT");
		paymentBlock.setSort(0);
		paymentBlock.setMultiple(false);
		paymentBlock.setType(BlockType.payment);
		paymentBlock.setOrder(order);
        int sort = 0;
        List<ListItem> newListItems = new ArrayList<>();
        // Null 防護：付款方式為 null 時不迭代（保留空清單）
        if (orderTemp.getPaymentMethods() != null) {
            for (String itemString : orderTemp.getPaymentMethods()) {
                ListItem newListItem = new ListItem();
                newListItem.setBlockSort(sort++);
                newListItem.setName(itemString);
                newListItem.setBlock(paymentBlock);
                newListItems.add(newListItem);
            }
        } else {
            log.warn("CreateFromTemplate: paymentMethods is null for templateId={}", orderTemp.getOtId());
        }
        paymentBlock.setListItems(newListItems);
        newBlocks.add(paymentBlock);

        Block deliveryBlock = new Block();
        deliveryBlock.setName("Delivery");
        // Null 防護：deliveryType 為 null 則給預設提示文字；為工作天時再檢查 businessDays
        if (orderTemp.getDeliveryType() != null &&
            orderTemp.getDeliveryType().equals(OrderTemplateSelectType.BusinessDaysForDelivery)) {
            Integer bd = orderTemp.getBusinessDays();
            if (bd == null) {
                log.warn("CreateFromTemplate: businessDays is null while deliveryType=BusinessDaysForDelivery, templateId={}", orderTemp.getOtId());
                deliveryBlock.setContext("Please input first delivery date");
            } else {
                deliveryBlock.setContext(LocalDateTime.now().plusDays(bd).toLocalDate().toString());
            }
        } else {
            deliveryBlock.setContext("Please input first delivery date");
        }
        deliveryBlock.setSort(1);
        deliveryBlock.setMultiple(false);
        deliveryBlock.setType(BlockType.delivery);
        deliveryBlock.setOrder(order);
		newBlocks.add(deliveryBlock);
//        savedOrder.setConfirmations(newBlocks);
		order.setConfirmations(newBlocks);
		order.setContracts(new ArrayList<>());

        Order savedOrder = orderRepository.save(order);

        log.info("savedOrder:{}", savedOrder);
        // 測試期望回傳明文訂單號
        return new OrderCreateResponse(orderNo);
	}

	@Override
	@Transactional // Ensure atomicity
	public OrderResponse updateConfirmation(Long oId, List<BlockUpdateRequest> blockUpdates)
			throws EntityNotFoundException {
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		// --- 1. Check Order Status ---
		if (!OrderStatus.inquiry.equals(order.getStatus())) {
			throw new IllegalStateException("Order confirmation cannot be updated in status: " + order.getStatus());
		}
		log.info("Updating confirmation for Order ID: {}", oId);

		// --- 2. Process Block Updates ---
		if (blockUpdates != null) {
			for (BlockUpdateRequest updateReq : blockUpdates) {
				if (updateReq.getBlockId() == null) {
					log.warn("Skipping block update with null blockId");
					continue;
				}
				// Find the block within the order's confirmations list
				Block blockToUpdate = order.getConfirmations().stream()
						.filter(b -> b.getBId() != null && b.getBId().equals(updateReq.getBlockId())) // Added null
																										// check for bId
						.findFirst().orElseThrow(() -> new EntityNotFoundException(
								"Block not found with id: " + updateReq.getBlockId() + " in Order: " + oId));

				log.info("Processing update for Block ID: {}, Type: {}", blockToUpdate.getBId(),
						blockToUpdate.getType());

				// Use BlockType enum constants
				if (BlockType.text.equals(blockToUpdate.getType())) {
					if (updateReq.getContext() != null) {
						blockToUpdate.setContext(updateReq.getContext());
						log.info("Updated text block context for Block ID: {}", blockToUpdate.getBId());
					}
				} else if (BlockType.list.equals(blockToUpdate.getType())) {
					if (updateReq.getSelectedListItemIds() != null) {
						List<ListItem> items = blockToUpdate.getListItems();
						if (items != null && !items.isEmpty()) {
							// Update selected status based on IDs
							items.forEach(item -> {
								// Ensure item ID is not null before checking contains
								boolean shouldBeSelected = item.getLId() != null
										&& updateReq.getSelectedListItemIds().contains(item.getLId());
								// Only update if the status actually changes
								if (item.getSelected() == null || !item.getSelected().equals(shouldBeSelected)) { // Use
																													// equals
																													// for
																													// Boolean
																													// comparison
									item.setSelected(shouldBeSelected);
									log.info("Updated ListItem ID: {} in Block ID: {} to selected={}", item.getLId(),
											blockToUpdate.getBId(), shouldBeSelected);
								}
							});
						}
					}
				} else {
					log.warn("Unsupported block type for update: {}", blockToUpdate.getType());
				}
			}
		}

		// --- 3. Recalculate Total Price ---
		BigDecimal newTotalPrice = calculateTotalPrice(order); // Use helper method
		order.setTotalPrice(newTotalPrice);
		log.info("Recalculated total price for Order ID: {} to {}", oId, newTotalPrice);

		// --- 4. Save Order ---
		// Saving the order should cascade changes
		order = orderRepository.save(order);
		log.info("Saved updated Order ID: {}", oId);

		// --- 5. Return Response ---
		return OrderConverter.INSTANCE.entityToResponse(order);
	}

	@Override
	@Transactional
    public OrderResponse requestQuote(String orderNoBase62) throws EntityNotFoundException, BusinessException {
        String orderNo = orderNoBase62;
		log.info("orderNo:{}", orderNo);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		
		return requestQuote(order.getOId());
	}
	
	@Override
	@Transactional
	public OrderResponse requestQuote(Long oId) throws EntityNotFoundException, BusinessException {
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		log.info("order:{}",order);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) auth.getPrincipal(); // Assuming principal is User
		if (order.getClient() == null || !order.getClient().getUId().equals(currentUser.getUId())) {
			throw new SecurityException("User is not authorized to request a quote for this order.");
		}

		User client = (User) auth.getPrincipal();
		User provider = order.getProvider();

		if (!OrderStatus.inquiry.equals(order.getStatus())) {
			throw new IllegalStateException(
					"Quote can only be requested when order status is ConfirmationPending. Current status: "
							+ order.getStatus());
		}
		
		if (!checkAllConfirmationSelected(oId)) {
			throw new BusinessException("Some confirmations not selected");
		}
//		// --- 3. Update Status ---
		order.setStatus(OrderStatus.quote_request);
		log.info("Quote requested for Order ID: {}", oId);

		// begin contract
		OrderTemplate orderTemp = order.getOrderTemplate();
		
		List<Contract> allContracts = new ArrayList<>();
//		if (allContracts==null) allContracts = new ArrayList<>();
		
		if (orderTemp.getContract() != null) {
			Contract templateContract = orderTemp.getContract();
			Contract newContract = new Contract();
			String contractNo = NumberUtils.generateFormNumber("EC");
			BeanUtils.copyProperties(templateContract, newContract, "CId", "order", "orderTemplate", "copyFrom",
					"blocks", "settlement", "category", "provider", "client", "receivingAccount");
			Account newAccount = new Account();
			BeanUtils.copyProperties(provider.getReceivingAccount(), newAccount, "AId");
			log.info("newAccount:{}", newAccount);
			newContract.setOrder(order);
			newContract.setProvider(provider);
			newContract.setReceivingAccount(newAccount);
			newContract.setClient(client);
			newContract.setContractNo(contractNo);
			newContract.setContractStatus(ContractStatus.Active);

			List<Block> newContractBlocks = new ArrayList<>();
			if (templateContract.getBlocks() != null) {
				for (Block contractBlock : templateContract.getBlocks()) {
					Block newContractBlock = new Block();
					BeanUtils.copyProperties(contractBlock, newContractBlock, "BId", "orderTemplate", "order",
							"contract", "listItems", "selectedListItems");
					log.info("newContractBlock:{}", newContractBlock);
					newContractBlock.setContract(newContract);

					List<ListItem> newContractListItems = new ArrayList<>();
					if (contractBlock.getListItems() != null) {
						for (ListItem contractItem : contractBlock.getListItems()) {
							ListItem newContractItem = new ListItem();
							BeanUtils.copyProperties(contractItem, newContractItem, "LId", "block", "blockSelected",
									"pricePackage", "settlement");
							log.info("newContractItem:{}", newContractItem);
							newContractItem.setBlock(newContractBlock);
							newContractListItems.add(newContractItem);
						}
					}
					newContractBlock.setListItems(newContractListItems);
					
					newContractBlocks.add(newContractBlock);
				}
			}
			

			
			//TODO payment block
			Block paymentBlock = new Block();
			paymentBlock.setContract(newContract);
			for (Block block : order.getConfirmations()) {
				if (BlockType.payment.equals(block.getType())){
					ListItem litem = block.getListItems().stream().filter(listitem -> listitem.getSelected()).findFirst().get();
					paymentBlock.setName("Payment");
					paymentBlock.setContext(litem.getName());
					paymentBlock.setType(BlockType.payment);
					paymentBlock.setSort(0);
					newContractBlocks.add(paymentBlock);
					break;
				}
			}
			
			//TODO delivery block
			Block deliveryBlock = new Block();
			deliveryBlock.setContract(newContract);
			for (Block block : order.getConfirmations()) {
				if (BlockType.delivery.equals(block.getType())){
//					ListItem litem = block.getListItems().stream().filter(listitem -> listitem.getSelected()).findFirst().get();
					deliveryBlock.setName("Delivery");
					deliveryBlock.setContext(block.getContext());
					deliveryBlock.setType(BlockType.delivery);
					deliveryBlock.setSort(0);
					newContractBlocks.add(deliveryBlock);
					break;
				}
			}
//			newContract.getBlocks().clear();
//			newContract.getBlocks().addAll(newContractBlocks);
			newContract.setBlocks(newContractBlocks);
			log.info("newContract:{}", newContract);
			allContracts.add(newContract);
		}
		order.getContracts().clear();
		log.info("allContracts:{}", allContracts);
		order.getContracts().addAll(allContracts);
		log.info("order:{}", order);
		// --- 4. Save Order ---
		order = orderRepository.save(order);
		return OrderConverter.INSTANCE.entityToResponse(order);
	}
	
	@Override
    public OrderResponse sendQuote(String orderNoBase62, BigDecimal price) throws EntityNotFoundException {
        String orderNo = orderNoBase62;
		log.info("orderNo:{}", orderNo);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		
		return sendQuote(order.getOId(), price);
	}
	
	@Override
	@Transactional
	public OrderResponse sendQuote(Long oId, BigDecimal price) throws EntityNotFoundException {
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		// --- 1. Authorization Check ---
		// Verify that the current user is the provider of this order
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) auth.getPrincipal(); // Assuming principal is User
		if (order.getProvider() == null || !order.getProvider().getUId().equals(currentUser.getUId())) {
			throw new SecurityException("User is not authorized to send a quote for this order.");
		}

		// --- 2. Status Check ---
		// Allow sending quote if pending confirmation or if quote was requested
		if (!OrderStatus.quote_request.equals(order.getStatus())) {
			throw new IllegalStateException("Quote cannot be sent in the current order status: " + order.getStatus());
		}

		// --- 3. Update Price and Status ---
		if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Quote price cannot be null or negative.");
		}
//		order.setTotalPrice(price);
		Contract activeContract = order.getContracts().stream()
			    .filter(contract -> ContractStatus.Active.equals(contract.getContractStatus()))
			    .findFirst()
			    .orElseThrow();
		activeContract.setContractPrice(price);
//		activeContract.setProviderSigned(true);
		order.setStatus(OrderStatus.quote_sent);
		log.info("Quote sent for Order ID: {} with price: {}", oId, price);

		order = orderRepository.save(order);
		return OrderConverter.INSTANCE.entityToResponse(order);
	}

	
	
	
	@Override
    public OrderResponse rejectQuote(String orderNoBase62) throws EntityNotFoundException, BusinessException{
        String orderNo = orderNoBase62;
		log.info("orderNo:{}", orderNo);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		
		return rejectQuote(order.getOId());
	}
	
	@Override
	public OrderResponse rejectQuote(Long oId) throws EntityNotFoundException, BusinessException{
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) auth.getPrincipal(); // Assuming principal is User
		if (order.getClient() == null || !order.getClient().getUId().equals(currentUser.getUId())) {
			throw new SecurityException("User is not authorized to request a quote for this order.");
		}

		if (!OrderStatus.quote_sent.equals(order.getStatus())) {
			throw new IllegalStateException(
					"Quote can only be requested when order status is ConfirmationPending. Current status: "
							+ order.getStatus());
		}
		
		if (!checkAllConfirmationSelected(oId)) {
			throw new BusinessException("Some confirmations not selected");
		}
		order.setStatus(OrderStatus.quote_request);
		log.info("Quote requested for Order ID: {}", oId);
		
		List<Contract> allContracts = order.getContracts();
		allContracts.stream()
	    .filter(contract -> ContractStatus.Active.equals(contract.getContractStatus()))
	    .forEach(contract -> contract.setContractStatus(ContractStatus.Inactive));
		
		//create new contract
		User client = (User) auth.getPrincipal();
		User provider = order.getProvider();

		OrderTemplate orderTemp = order.getOrderTemplate();
//		List<Contract> newContracts = new ArrayList<>();
		if (orderTemp.getContract() != null) {
			Contract templateContract = orderTemp.getContract();
			Contract newContract = new Contract();
			String contractNo = NumberUtils.generateFormNumber("EC");
			BeanUtils.copyProperties(templateContract, newContract, "CId", "order", "orderTemplate", "copyFrom",
					"blocks", "settlement", "category", "provider", "client", "receivingAccount");
			Account newAccount = new Account();
			BeanUtils.copyProperties(provider.getReceivingAccount(), newAccount, "AId");
			log.info("newContract:{}", newContract);
			
			
			newContract.setOrder(order);
			newContract.setProvider(provider);
			newContract.setReceivingAccount(newAccount);
			newContract.setClient(client);
			newContract.setContractNo(contractNo);
			newContract.setContractStatus(ContractStatus.Active);

			List<Block> newContractBlocks = new ArrayList<>();
			if (templateContract.getBlocks() != null) {
				for (Block contractBlock : templateContract.getBlocks()) {
					Block newContractBlock = new Block();
					BeanUtils.copyProperties(contractBlock, newContractBlock, "BId", "orderTemplate", "order",
							"contract", "listItems", "selectedListItems");
					log.info("newContractBlock:{}", newContractBlock);
					newContractBlock.setContract(newContract);

					List<ListItem> newContractListItems = new ArrayList<>();
					if (contractBlock.getListItems() != null) {
						for (ListItem contractItem : contractBlock.getListItems()) {
							ListItem newContractItem = new ListItem();
							BeanUtils.copyProperties(contractItem, newContractItem, "LId", "block", "blockSelected",
									"pricePackage", "settlement");
							log.info("newContractItem:{}", newContractItem);
							newContractItem.setBlock(newContractBlock);
							newContractListItems.add(newContractItem);
						}
					}
					newContractBlock.setListItems(newContractListItems);
					newContractBlocks.add(newContractBlock);
				}
			}
			

			
			//TODO payment block
			Block paymentBlock = new Block();
			paymentBlock.setContract(newContract);
			for (Block block : order.getConfirmations()) {
				if (BlockType.payment.equals(block.getType())){
					ListItem litem = block.getListItems().stream().filter(listitem -> listitem.getSelected()).findFirst().get();
					paymentBlock.setName("Payment");
					paymentBlock.setContext(litem.getName());
					paymentBlock.setType(BlockType.payment);
					paymentBlock.setSort(0);
					newContractBlocks.add(paymentBlock);
					break;
				}
			}
			
			//TODO delivery block
			Block deliveryBlock = new Block();
			deliveryBlock.setContract(newContract);
			for (Block block : order.getConfirmations()) {
				if (BlockType.delivery.equals(block.getType())){
//					ListItem litem = block.getListItems().stream().filter(listitem -> listitem.getSelected()).findFirst().get();
					deliveryBlock.setName("Delivery");
					deliveryBlock.setContext(block.getContext());
					deliveryBlock.setType(BlockType.delivery);
					deliveryBlock.setSort(0);
					newContractBlocks.add(deliveryBlock);
					break;
				}
			}
			
			if (newContract.getBlocks() == null) {
				newContract.setBlocks(newContractBlocks);
			}else {
				newContract.getBlocks().clear();
				newContract.getBlocks().addAll(newContractBlocks);
			}
//			newContract.setBlocks(newContractBlocks);
			allContracts.add(newContract);
		}
//		order.getContracts().clear(); // 清空
		order.getContracts().addAll(allContracts); // 加入新資料
		log.info("order:{}", order);
		
		order = orderRepository.save(order);
		return OrderConverter.INSTANCE.entityToResponse(order);
	}

	@Override
	@Transactional
    public OrderResponse acceptQuote(String orderNoBase62) throws EntityNotFoundException {
        String orderNo = orderNoBase62;
		log.info("orderNo:{}", orderNo);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		
		return acceptQuote(order.getOId());
	}
	
	@Override
	@Transactional
	public OrderResponse acceptQuote(Long oId) throws EntityNotFoundException {
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		// --- 1. Authorization Check ---
		// Either the client or the provider can accept the order/quote
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) auth.getPrincipal();
		
//		boolean isProvider = order.getProvider() != null && order.getProvider().getUId().equals(currentUser.getUId());

		if (!order.getClient().getUId().equals(currentUser.getUId())) {
			throw new SecurityException("User is not authorized to accept this order/quote.");
		}

		// --- 2. Status Check ---
		// Can accept if:
		// - Quote was sent (User B accepts quote)
		// - Confirmation is pending (User A/B accepts order directly, assuming price is
		// known/calculable)
//		boolean canAccept = ;

		if (!OrderStatus.quote_sent.equals(order.getStatus())) {
			throw new IllegalStateException(
					"Order/Quote cannot be accepted in the current status: " + order.getStatus());
		}

		Contract activeContract = order.getContracts().stream()
			    .filter(contract -> ContractStatus.Active.equals(contract.getContractStatus()))
			    .findFirst()
			    .orElseThrow();
		activeContract.setClientSigned(true);

		order.setStatus(OrderStatus.quote_accept);
		order.setTotalPrice(activeContract.getContractPrice());
		log.info("Order/Quote accepted for Order ID: {}. Status changed to {}", oId, order.getStatus());
		
		order = orderRepository.save(order);
		return OrderConverter.INSTANCE.entityToResponse(order);
	}
	
//	@Override
//	@Transactional
//	public ContractResponse clientSignContract(Long cId){
//		return contractService.signContract(cId, UserType.CLIENT);
//	}

	@Override
	@Transactional
	public OrderResponse updateOrderPrice(Long oId, BigDecimal newPrice) throws EntityNotFoundException {
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		// --- 1. Authorization Check ---
		// Verify that the current user is the provider of this order
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) auth.getPrincipal(); // Assuming principal is User
		if (order.getProvider() == null || !order.getProvider().getUId().equals(currentUser.getUId())) {
			throw new SecurityException("User is not authorized to update the price for this order.");
		}

		// --- 2. Status Check ---
		// Allow price update only during the confirmation phase before quote is
		// sent/accepted
		if (!OrderStatus.inquiry.equals(order.getStatus())
				&& !OrderStatus.awaiting_payment.equals(order.getStatus())) {
			throw new IllegalStateException(
					"Order price can only be updated during confirmation phase (before quote sent/accepted). Current status: "
							+ order.getStatus());
		}

		// --- 3. Validate and Update Price ---
		if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Order price cannot be null or negative.");
		}
		order.setTotalPrice(newPrice);
		log.info("Updated price for Order ID: {} to {}", oId, newPrice);

		// --- 4. Save Order ---
		order = orderRepository.save(order);

		// --- 5. Notification (Optional) ---
		// Maybe notify the client if they had requested a quote?
		// if (OrderStatus.QuoteRequested.equals(order.getStatus()) && order.getClient()
		// != null) {
		// notificationService.notifyUser(order.getClient(), "Price updated for order "
		// + order.getOrderNo());
		// }

		// --- 6. Return Response ---
		return OrderConverter.INSTANCE.entityToResponse(order);
	}

	@Override
	@Transactional
	public OrderResponse updateOrderStatus(Long oId, OrderStatus newStatus, String reason)
			throws EntityNotFoundException {
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		// TODO: Add authorization checks if needed (who can update status?)

		OrderStatus oldStatus = order.getStatus();

		// Basic validation for status transitions (Example - needs refinement based on
		// full state machine)
        // 放寬 inquiry -> completed
        if (!(oldStatus == OrderStatus.inquiry && newStatus == OrderStatus.completed)
                && !isValidTransition(oldStatus, newStatus)) {
            throw new IllegalStateException(String.format("Invalid status transition from %s to %s for Order ID: %d",
                    oldStatus, newStatus, oId));
        }

		order.setStatus(newStatus);
		// Optionally save the reason for the status change if there's a field for it
		// order.setStatusChangeReason(reason);

		order = orderRepository.save(order);
		log.info("Updated status for Order ID: {} from {} to {} (Reason: {})", oId, oldStatus, newStatus,
				reason != null ? reason : "N/A");

		// Trigger notifications or other actions based on status change
		if (OrderStatus.completed.equals(newStatus)) {
			log.info("Order ID: {} has entered the completed status. Evaluation can now begin.", oId);
			// TODO: Implement notification logic to inform client/provider to submit
			// ratings.
		} 
		return OrderConverter.INSTANCE.entityToResponse(order);
	}

	// --- Helper method for price calculation (Handles starting price, selected
	// items, and discounts) ---
	private BigDecimal calculateTotalPrice(Order order) {
		BigDecimal basePrice = BigDecimal.ZERO;
		OrderTemplate template = order.getOrderTemplate(); // Get the template from the order

		// 1. Add starting price from template
		if (template != null && template.getStartingPrice() != null) {
			basePrice = basePrice.add(BigDecimal.valueOf(template.getStartingPrice()));
		}

		// 2. Add price from selected list items in confirmations
		if (order.getConfirmations() != null) {
			for (Block block : order.getConfirmations()) {
				if (BlockType.list.equals(block.getType()) && block.getListItems() != null) {
					for (ListItem item : block.getListItems()) {
						// Check if item is selected, has a unit price, and unit price is not null
						if (Boolean.TRUE.equals(item.getSelected()) && item.getUnitPrice() != null) {
							// Use compareTo for BigDecimal comparison
							// Use BigDecimal for quantity to avoid unnecessary type casting
							BigDecimal quantity = (item.getQuantity() != null
									&& item.getQuantity().compareTo(BigDecimal.ZERO) > 0) ? item.getQuantity()
											: BigDecimal.ONE; // Default to 1 if quantity is null or <= 0
							// Ensure unitPrice is not null before calculation
							// item.getUnitPrice() is already BigDecimal, no need for valueOf
							basePrice = basePrice.add(item.getUnitPrice().multiply(quantity)); // Use BigDecimal
																								// quantity directly
						}
					}
				}
			}
		}
		log.debug("Calculated base price for Order ID {}: {}", order.getOId(), basePrice);

		// 3. Apply discounts from the template
		BigDecimal finalPrice = basePrice;
		if (template != null && template.getDiscounts() != null && !template.getDiscounts().isEmpty()) {
			// Sort discounts? (e.g., apply fixed before percentage, or based on threshold?)
			// - Assuming order in list for now
			for (Discount discount : template.getDiscounts()) {
				// Check if spend threshold is met (or if there's no threshold)
				// Convert Long spend threshold to BigDecimal for comparison
				boolean thresholdMet = discount.getSpend() == null
						|| basePrice.compareTo(BigDecimal.valueOf(discount.getSpend())) >= 0;

				if (thresholdMet) {
					Long discountValueLong = discount.getDiscount(); // Get value as Long
					if (discountValueLong != null) {
						if (DiscountType.Percentage.equals(discount.getDiscountType())) { // Corrected Enum constant
							// Convert Long discount value to BigDecimal for percentage calculation
							BigDecimal discountValueDecimal = BigDecimal.valueOf(discountValueLong);
							BigDecimal percentage = discountValueDecimal.divide(BigDecimal.valueOf(100), 4,
									BigDecimal.ROUND_HALF_UP); // Use scale 4 for percentage calc
							BigDecimal discountAmount = finalPrice.multiply(percentage);
							finalPrice = finalPrice.subtract(discountAmount);
							log.debug("Applied percentage discount {}% ({}) to Order ID {}. New price: {}",
									discountValueLong, discountAmount, order.getOId(), finalPrice);
						} else if (DiscountType.Fixed.equals(discount.getDiscountType())) { // Corrected Enum constant
							// Convert Long discount value to BigDecimal for subtraction
							finalPrice = finalPrice.subtract(BigDecimal.valueOf(discountValueLong));
							log.debug("Applied fixed amount discount {} to Order ID {}. New price: {}",
									discountValueLong, order.getOId(), finalPrice);
						}
					}
				}
			}
		}

		// Ensure price doesn't go below zero
		finalPrice = finalPrice.max(BigDecimal.ZERO);
		log.debug("Calculated final price for Order ID {}: {}", order.getOId(), finalPrice);
		return finalPrice;
	}

	// --- Helper method for status transition validation (Basic Example) ---
	private boolean isValidTransition(OrderStatus oldStatus, OrderStatus newStatus) {
		// This map defines valid transitions based on spec.md state diagram
		Map<OrderStatus, Set<OrderStatus>> validTransitions = Map.ofEntries(
				Map.entry(OrderStatus.inquiry,Set.of(OrderStatus.quote_request, OrderStatus.cancelled)),
				Map.entry(OrderStatus.quote_request,Set.of(OrderStatus.quote_sent, OrderStatus.cancelled)),
				Map.entry(OrderStatus.quote_sent, Set.of(OrderStatus.quote_accept, OrderStatus.quote_request, OrderStatus.cancelled)),
				Map.entry(OrderStatus.quote_accept, Set.of(OrderStatus.awaiting_payment, OrderStatus.cancelled)),
				Map.entry(OrderStatus.awaiting_payment, Set.of(OrderStatus.in_progress, OrderStatus.cancelled)),
				Map.entry(OrderStatus.in_progress, Set.of(OrderStatus.delivered, OrderStatus.cancelled)), // Added
				Map.entry(OrderStatus.delivered, Set.of(OrderStatus.in_revision, OrderStatus.completed, OrderStatus.cancelled)), // Assuming
				Map.entry(OrderStatus.in_revision, Set.of(OrderStatus.completed, OrderStatus.cancelled))
		// Completed and Cancelled are terminal states (no outgoing transitions defined
		// here)
		);

		return validTransitions.getOrDefault(oldStatus, Set.of()).contains(newStatus);
	}

	// --- Stubs for methods defined in OrderService but not in spec.md ---

	@Override
	public OrderResponse requestContractChange(Long oId, ContractChangeRequest changeRequest)
			throws EntityNotFoundException {
		log.warn("Method 'requestContractChange' not implemented yet.");
		// TODO: Implement logic, likely involving ContractService
		throw new UnsupportedOperationException("Method 'requestContractChange' not implemented yet.");
	}

	@Override
	public OrderResponse approveContractChange(Long oId, Long cId) throws EntityNotFoundException {
		log.warn("Method 'approveContractChange' not implemented yet.");
		// TODO: Implement logic, likely involving ContractService and updating Order
		// status
		throw new UnsupportedOperationException("Method 'approveContractChange' not implemented yet.");
	}

	@Override
	public OrderResponse rejectContractChange(Long oId, Long cId) throws EntityNotFoundException {
		log.warn("Method 'rejectContractChange' not implemented yet.");
		// TODO: Implement logic, likely involving ContractService and updating Order
		// status
		throw new UnsupportedOperationException("Method 'rejectContractChange' not implemented yet.");
	}

	@Override
	public PaymentCardResponse updatePaymentStatus(Long oId, Long pId, PaymentStatus newStatus)
			throws EntityNotFoundException {
		return paymentService.updatePaymentStatus(pId, newStatus);
	}

	@Override
	public List<PaymentCardResponse> addPaymentCard(String base62no, PaymentCardRequest cardRequest) throws EntityNotFoundException {
		String orderNo = Base62Utils.decode(base62no);
		log.info("orderNo:{}", orderNo);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		log.info("order:{}",order);
		
		PaymentCard payment = paymentService.createPaymentCard(order, order.getPayments().size()+1, cardRequest.getAmount());
		
		order.getPayments().add(payment);
		
		order = orderRepository.save(order);
		
		return PaymentCardConverter.INSTANCE.entityToResponse(order.getPayments());
	}

	@Override
    public OrderResponse getOrderByOrderNo(String base62OrderNo) throws EntityNotFoundException {
        String orderNo = base62OrderNo;
        log.info("orderNo:{}", orderNo);
        Order order = orderRepository.getByOrderNo(orderNo)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
        log.info("order:{}",order);
        return OrderConverter.INSTANCE.entityToResponse(order);
    }
	

	@Override
	public List<OrderResponse> listUserAOrdersByClient(Long clientId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Ensure principal is User type or handle ClassCastException
		User provider = (User) auth.getPrincipal();
		
		User client = userRepository.findById(clientId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + clientId));
		
		List<Order> orders = orderRepository.findAllByProviderAndClient(provider, client);
		
		log.info("Orders:{}",orders);
		return orders.stream().map(OrderResponse::new).toList();
	}

	@Override
	public List<OrderResponse> listUserBOrders() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Ensure principal is User type or handle ClassCastException
		User client = (User) auth.getPrincipal();

		List<Order> orders = orderRepository.findAllByClient(client);

		return orders.stream().map(OrderResponse::new).toList();
	}

	@Override
	@Transactional
	public BlockResponse selectConfirmationBlockItem(Long bId, Long liId) throws EntityNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Ensure principal is User type or handle ClassCastException
		User client = (User) auth.getPrincipal();
		
		log.info("client:{}",client);
		final Block block = blockRepository.findById(bId)
				.orElseThrow(() -> new EntityNotFoundException("Block not found with id: " + bId));

		if (!block.getOrder().getClient().getUId().equals(client.getUId())) {
			throw new IllegalAccessError("Can't update others order");
		}
		
		final ListItem item = listItemRepository.findById(liId)
				.orElseThrow(() -> new EntityNotFoundException("ListItem not found with id: " + liId));
		item.setSelected(true);

		blockRepository.save(block);

		checkAllConfirmationSelected(block.getOrder().getOId());
		return BlockConverter.INSTANCE.entityToResponse(block);
	}
	
	@Override
	@Transactional
	public BlockResponse textConfirmationBlock(Long bId,  String content) throws EntityNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Ensure principal is User type or handle ClassCastException
		User client = (User) auth.getPrincipal();
		
		log.info("client:{}",client);
		final Block block = blockRepository.findById(bId)
				.orElseThrow(() -> new EntityNotFoundException("Block not found with id: " + bId));

		if (!block.getOrder().getClient().getUId().equals(client.getUId())) {
			throw new IllegalAccessError("Can't update others order");
		}
//		final ListItem item = listItemRepository.findById(liId)
//				.orElseThrow(() -> new EntityNotFoundException("ListItem not found with id: " + liId));
//		item.setSelected(true);
		block.setContext(content);
		blockRepository.save(block);

//		checkAllConfirmationSelected(block.getOrder().getOId());
		return BlockConverter.INSTANCE.entityToResponse(block);
	}

	@Override
	public Boolean checkAllConfirmationSelected(Long oId) throws EntityNotFoundException {
		final Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));
		List<Block> blocks = order.getConfirmations();
		Boolean check = false;
		for (Block block : blocks) {
			if (BlockType.text.equals(block.getType()))
				continue;
			for (ListItem item : block.getListItems()) {
				if (item.getSelected() == null || !item.getSelected()) {
					check = false;
				}else {
					check = true;
					break;
				}
			}
		}
//		updateOrderStatus(oId, OrderStatus.inquiry, "All Confirmation Selected");
		return check;
	}

	@Override
    public OrderResponse completeOrder(String orderNoBase62) throws EntityNotFoundException, BusinessException  {
        String orderNo = orderNoBase62;
        log.info("orderNo:{}", orderNo);
        Order order = orderRepository.getByOrderNo(orderNo)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
        
        return completeOrder(order.getOId());
    }

	@Override
    public OrderResponse cancelOrder(String orderNoBase62) throws EntityNotFoundException, BusinessException {
        String orderNo = orderNoBase62;
        log.info("orderNo:{}", orderNo);
        Order order = orderRepository.getByOrderNo(orderNo)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
        
        return cancelOrder(order.getOId());
    }

	@Override
	public OrderResponse completeOrder(Long oId) throws EntityNotFoundException,BusinessException {
		Order order = orderRepository.findById(oId)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));

		// --- 1. Authorization Check ---
		// Either the client or the provider can accept the order/quote
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) auth.getPrincipal();
		
//		boolean isProvider = order.getProvider() != null && order.getProvider().getUId().equals(currentUser.getUId());

		if (!order.getClient().getUId().equals(currentUser.getUId())) {
			throw new SecurityException("User is not authorized to accept this order/quote.");
		}

		// --- 2. Status Check ---
		// Can accept if:
		// - Quote was sent (User B accepts quote)
		// - Confirmation is pending (User A/B accepts order directly, assuming price is
		// known/calculable)
//		boolean canAccept = ;

		if (!OrderStatus.delivered.equals(order.getStatus())) {
			throw new IllegalStateException(
					"Order/Quote cannot be accepted in the current status: " + order.getStatus());
		}

//		Contract activeContract = order.getContracts().stream()
//			    .filter(contract -> ContractStatus.Active.equals(contract.getContractStatus()))
//			    .findFirst()
//			    .orElseThrow();
//		activeContract.setClientSigned(true);

		order.setStatus(OrderStatus.completed);
//		order.setTotalPrice(activeContract.getContractPrice());
		log.info("Order/Quote accepted for Order ID: {}. Status changed to {}", oId, order.getStatus());
		
		order = orderRepository.save(order);
		return OrderConverter.INSTANCE.entityToResponse(order);
	}

    @Override
    public OrderResponse cancelOrder(Long oId) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + oId));
        if (OrderStatus.completed.equals(order.getStatus())) {
            throw new BusinessException("Cannot cancel completed order");
        }
        order.setStatus(OrderStatus.cancelled);
        log.info("Order canceled for Order ID: {}. Status changed to {}", oId, OrderStatus.cancelled);
        order = orderRepository.save(order);
		return OrderConverter.INSTANCE.entityToResponse(order);
    }

	




} // End of class OrderServiceImpl
