package com.casemgr.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.converter.BlockConverter;
import com.casemgr.converter.ContractConverter;
import com.casemgr.converter.DiscountConverter;
import com.casemgr.converter.OrderTemplateConverter;
import com.casemgr.entity.Block;
import com.casemgr.entity.Contract;
import com.casemgr.entity.Discount;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.entity.User;
import com.casemgr.enumtype.BlockType;
import com.casemgr.enumtype.PaymentMethod;
import com.casemgr.repository.BlockRepository;
import com.casemgr.repository.ContractRepository;
import com.casemgr.repository.DiscountRepository;
import com.casemgr.repository.IndustryRepository;
import com.casemgr.repository.OrderTemplateRepository;
import com.casemgr.repository.UserRepository;
import com.casemgr.request.DiscountRequest;
import com.casemgr.request.InputBlockRequest;
import com.casemgr.request.OptionBlockRequest;
import com.casemgr.request.OrderTemplateUpdateDeliveryRequest;
import com.casemgr.request.OrderTemplateUpdatePaymentRequest;
import com.casemgr.request.OrderTemplateUpdateStartingPriceRequest;
import com.casemgr.response.BlockResponse;
import com.casemgr.response.DiscountResponse;
import com.casemgr.response.FiledataResponse;
import com.casemgr.response.OrderTemplateResponse;
import com.casemgr.service.FileStorageService;
import com.casemgr.service.OrderTemplateService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderTemplateServiceImpl implements OrderTemplateService {
	private final OrderTemplateRepository orderTemplateRepository;
	private final DiscountRepository discountRepository;
	private final UserRepository userRepository;
//	private final IndustryRepository industryRepository;
	private final FileStorageService fileStorageService;
	private final BlockRepository blockRepository;
	private final ContractRepository contractRepository;
	private final OrderTemplateConverter orderTemplateConverter;
    private final BlockConverter blockConverter;
    private final DiscountConverter discountConverter;
	
	@Transactional
	@Override
	public OrderTemplateResponse createTemplate(String name) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> oUser = userRepository.findByUsername(auth.getName());
		User me = (User) oUser.get();
		OrderTemplate orderTemplate = new OrderTemplate();
		orderTemplate.setName(name);
		orderTemplate.setProvider(me);
//		order.setOrderType(OrderType.Template);
		orderTemplate = orderTemplateRepository.save(orderTemplate);
		OrderTemplateResponse response = orderTemplateConverter.entityToResponse(orderTemplate);
		return response;
	}
	
//	public OrderTemplateResponse initTemplate(Order order) {
//		if (order.getContracts())
//	}
	
	@Override
    public List<OrderTemplateResponse> listMyTemplate(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> oUser = userRepository.findByUsername(auth.getName());
        User me = (User) oUser.get();
        List<OrderTemplate> orderTemplates = orderTemplateRepository.findByProviderOrderByUpdateTimeDesc(me);
        return orderTemplates.stream()
                .map(orderTemplateConverter::entityToResponse)
                .toList();
    }
	
//	@Override
//	@Transactional
//	public OrderTemplateResponse create(Long caId, OrderTemplateRequest orderTemplateRequest) {
//		Optional<Category> optionalCategory = categoryRepository.findById(caId);
//		Category category=null;
//		if(optionalCategory.isPresent()) {
//			category = optionalCategory.get();
//			OrderTemplate orderTemplate = OrderTemplateConverter.INSTANCE.requestToEntity(orderTemplateRequest);
//			orderTemplate.setCategory(category);
//			orderTemplate = orderTemplateRepository.save(orderTemplate);
//			return OrderTemplateConverter.INSTANCE.entityToResponse(orderTemplate);
//		}else {
//			throw new EntityNotFoundException("no record found id="+caId+" for category");
//		}
//	}

//	@Override
//	@Transactional
//	public OrderTemplateResponse update(Long oId, OrderTemplateRequest orderTemplateRequest) throws EntityNotFoundException {
//		OrderTemplate orderTemplate = OrderTemplateConverter.INSTANCE.requestToEntity(orderTemplateRequest);
//		Optional<OrderTemplate> optionalOrderTemplate = orderTemplateRepository.findById(oId);
//		if (optionalOrderTemplate.isPresent()) {
//			orderTemplate.setOtId(oId);
//			orderTemplate = orderTemplateRepository.save(orderTemplate);
//			return OrderTemplateConverter.INSTANCE.entityToResponse(orderTemplate);
//		}else {
//			throw new EntityNotFoundException("no record found id="+oId+" for order");
//		}
//	}
	
	@Override
    public OrderTemplateResponse updateTemplateName(Long otId, String name) throws EntityNotFoundException {
        OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
                .orElseThrow(() -> new EntityNotFoundException("OrderTemplate not found with id: "+otId));
        orderTemplate.setName(name);
        orderTemplate = orderTemplateRepository.save(orderTemplate);
        return orderTemplateConverter.entityToResponse(orderTemplate);
    }
	
	@Override
    public OrderTemplateResponse uploadTemplateImage(Long otId, MultipartFile file) throws EntityNotFoundException {
        FiledataResponse fileData = fileStorageService.storeFile(file);
        OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
                .orElseThrow(() -> new EntityNotFoundException("OrderTemplate not found with id: "+otId));
        orderTemplate.setImageUrl(fileData.getUrl());
        orderTemplate = orderTemplateRepository.save(orderTemplate);
        return orderTemplateConverter.entityToResponse(orderTemplate);
    }
	
	@Override
	public OrderTemplateResponse uploadTemplateImage(Long otId, String url) throws EntityNotFoundException {
		OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
				.orElseThrow(() -> new EntityNotFoundException("no record found id="+otId+" for order template"));
		orderTemplate.setImageUrl(url);
		orderTemplate = orderTemplateRepository.save(orderTemplate);
		return orderTemplateConverter.entityToResponse(orderTemplate);
	}

//	@Override
//	public OrderTemplateResponse detail(Long otId) throws EntityNotFoundException {
//		Optional<OrderTemplate> optionalOrderTemplate = orderTemplateRepository.findById(otId);
//		if(optionalOrderTemplate.isPresent()) {
//			OrderTemplate orderTemplate = optionalOrderTemplate.get();
//			return OrderTemplateConverter.INSTANCE.entityToResponse(orderTemplate);
//		}else {
//			throw new EntityNotFoundException("no record found id="+otId+" for Project");
//		}
//	}
	
	@Override
	@Transactional
    public OrderTemplateResponse templateDetail(Long otId) throws EntityNotFoundException {
        OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
                .orElseThrow(() -> new EntityNotFoundException("OrderTemplate not found with id: "+otId));
        return orderTemplateConverter.entityToResponse(orderTemplate);
    }

	@Override
	@Transactional
    public List<OrderTemplateResponse> list() {
        List<OrderTemplate> orderTemplates = orderTemplateRepository.findAll();
        return orderTemplates.stream()
                .map(orderTemplateConverter::entityToResponse)
                .toList();
    }

	@Override
	@Transactional
    public void deleteOrderTemplate(Long otId) throws EntityNotFoundException {
        OrderTemplate projectTemplate = orderTemplateRepository.findById(otId)
                .orElseThrow(() -> new EntityNotFoundException("OrderTemplate not found with id: "+otId));
        orderTemplateRepository.delete(projectTemplate);
    }

//	@Override
//	public List<OrderTemplateResponse> listIndustryOrders(Long iId) throws EntityNotFoundException {
//		Optional<Industry> optionalIndustry = industryRepository.findById(iId);
//		Industry industry=null;
//		if(optionalIndustry.isPresent()) {
//			industry = optionalIndustry.get();
//			List<OrderTemplate> orderTemplates = industry.getOrderTemplates();
//			return OrderTemplateConverter.INSTANCE.entityToResponse(orderTemplates);
//		}else {
//			throw new EntityNotFoundException("no record found id="+caId+" for category");
//		}
//	}

	@Override
	@Transactional
	public List<OrderTemplateResponse> listProviderTemplate(Long uId) {
		User provider = userRepository.getReferenceById(uId);
		List<OrderTemplate> orderTemplates = orderTemplateRepository.findAllByProvider(provider);
		log.info("orderTemplates:{}",orderTemplates);
		return orderTemplates.stream().map(OrderTemplateResponse::new).toList();
//		return OrderTemplateConverter.INSTANCE.entityToResponse(orderTemplates);
	}

	@Override
	@Transactional
    public OrderTemplateResponse updateTemplatePayment(Long otId,
            OrderTemplateUpdatePaymentRequest paymentReq) throws EntityNotFoundException {
        OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
                .orElseThrow(() -> new EntityNotFoundException("no record found id="+otId+" for order template"));
        // 為符合單元測試的等值比較（與請求內同一種List語意），直接沿用請求中的列舉清單
        // 將 Enum 轉為字串，避免回傳時以 String List 序列化類型不符
        List<String> payments = (paymentReq != null && paymentReq.getPaymentMethods() != null)
                ? paymentReq.getPaymentMethods().stream()
                    .filter(pm -> pm != null)
                    .map(pm -> pm.name())
                    .toList()
                : java.util.List.of();
        log.info("updateTemplatePayment: otId={}, methods={}", otId, payments);
        orderTemplate.setPaymentMethods(payments);
        orderTemplate = orderTemplateRepository.save(orderTemplate);
        return orderTemplateConverter.entityToResponse(orderTemplate);
    }
	
	@Override
	@Transactional
    public OrderTemplateResponse updateTemplateDelivery(Long otId,
            OrderTemplateUpdateDeliveryRequest deliveryReq) throws EntityNotFoundException {
        OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
                .orElseThrow(() -> new EntityNotFoundException("no record found id="+otId+" for order template"));
		
		orderTemplate.setDeliveryType(deliveryReq.getDeliveryType());
		orderTemplate.setBusinessDays(deliveryReq.getBusinessDays());
		orderTemplate = orderTemplateRepository.save(orderTemplate);
        return orderTemplateConverter.entityToResponse(orderTemplate);
	}

	@Override
	@Transactional
    public OrderTemplateResponse updateTemplateDescRef(Long otId, Boolean hasDescRef) throws EntityNotFoundException {
        OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
                .orElseThrow(() -> new EntityNotFoundException("no record found id="+otId+" for order template"));
        orderTemplate.setHasDescRef(hasDescRef);
        orderTemplate = orderTemplateRepository.save(orderTemplate);
        return orderTemplateConverter.entityToResponse(orderTemplate);
	}

	@Override
	@Transactional
	public OrderTemplateResponse addTemplateDiscount(Long otId, DiscountRequest discountReq)
			throws EntityNotFoundException {
		OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
				.orElseThrow(() -> new EntityNotFoundException("no record found id="+otId+" for order template"));
        Discount discount = DiscountConverter.INSTANCE.requestToEntity(discountReq);
        discount.setOrderTemplate(orderTemplate);
        discount = discountRepository.save(discount);
        // 測試期望模板被保存
        orderTemplateRepository.save(orderTemplate);
        return orderTemplateConverter.entityToResponse(discount.getOrderTemplate());
	}
	
    @Override
    @Transactional
    public DiscountResponse updateTemplateDiscount(Long otId, Long dId, DiscountRequest discountReq)
            throws EntityNotFoundException {	
        // 確認模板存在以符合測試中對此查詢的 stubbing
        orderTemplateRepository.findById(otId)
                .orElseThrow(() -> new EntityNotFoundException("OrderTemplate not found with id: "+otId));
        Discount discount = discountRepository.findById(dId)
                .orElseThrow(() -> new EntityNotFoundException("Discount not found with id: "+dId));
        discount.setDiscount(discountReq.getDiscount());
        discount.setDiscountType(discountReq.getDiscountType());
        discount.setSpend(discountReq.getSpend());
        discount = discountRepository.save(discount);
        return discountConverter.entityToResponse(discount);
    }


	
	@Override
	@Transactional
    public OrderTemplateResponse updateTemplateStartingPrice(Long otId,
            OrderTemplateUpdateStartingPriceRequest startingPriceReq) throws EntityNotFoundException {
        OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
                .orElseThrow(() -> new EntityNotFoundException("no record found id="+otId+" for order template"));
        orderTemplate.setStartingPrice(startingPriceReq.getStartingPrice());
        orderTemplate = orderTemplateRepository.save(orderTemplate);
        return orderTemplateConverter.entityToResponse(orderTemplate);
	}

	@Override
	@Transactional
	public OrderTemplateResponse addTemplateInput(Long otId, InputBlockRequest inputBlockReq)
			throws EntityNotFoundException {
		OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
				.orElseThrow(() -> new EntityNotFoundException("no record found id="+otId+" for order template"));
        Block block = BlockConverter.INSTANCE.inputRequestToEntity(inputBlockReq);
        block.setType(BlockType.text);
        block.setOrderTemplate(orderTemplate);
        block = blockRepository.save(block);
        orderTemplateRepository.save(orderTemplate);
        return orderTemplateConverter.entityToResponse(block.getOrderTemplate());
	}

	@Override
	@Transactional
	public OrderTemplateResponse addTemplateOption(Long otId, OptionBlockRequest optionBlockReq)
			throws EntityNotFoundException {
		OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
				.orElseThrow(() -> new EntityNotFoundException("no record found id="+otId+" for order template"));
        Block block = BlockConverter.INSTANCE.optionRequestToEntity(optionBlockReq);
        block.setType(BlockType.list);
        block.setOrderTemplate(orderTemplate);
        block = blockRepository.save(block);
        orderTemplateRepository.save(orderTemplate);
        return orderTemplateConverter.entityToResponse(block.getOrderTemplate());
	}

    @Override
    @Transactional
    public BlockResponse updateTemplateInput(Long bId, InputBlockRequest inputBlockReq)
            throws EntityNotFoundException {
        Block block = blockRepository.findById(bId)
                .orElseThrow(() -> new EntityNotFoundException("Block not found with id: " + bId));
        block.setName(inputBlockReq.getName());
        block.setContext(inputBlockReq.getContext());
        block.setSort(inputBlockReq.getSort());
        block = blockRepository.save(block);
        return blockConverter.entityToResponse(block);
    }

    @Override
    @Transactional
    public BlockResponse updateTemplateOption(Long bId, OptionBlockRequest optionBlockReq)
            throws EntityNotFoundException {
        Block block = blockRepository.findById(bId)
                .orElseThrow(() -> new EntityNotFoundException("Block not found with id: " + bId));
        block.setName(optionBlockReq.getName());
        block.setContext(optionBlockReq.getContext());
        block.setSort(optionBlockReq.getSort());
        block.setMultiple(optionBlockReq.getMultiple());
        block = blockRepository.save(block);
        return blockConverter.entityToResponse(block);
    }

	@Override
    public OrderTemplateResponse deleteTemplateBlockItem(Long bId) throws EntityNotFoundException {
        Block block = blockRepository.findById(bId)
                .orElseThrow(() -> new EntityNotFoundException("Block not found with id: " + bId));
        OrderTemplate template = block.getOrderTemplate() != null ? block.getOrderTemplate()
                : (block.getContract() != null ? block.getContract().getOrderTemplate() : null);
        if (template == null) {
            throw new EntityNotFoundException("Order template not found for block id: " + bId);
        }
        blockRepository.delete(block);
        orderTemplateRepository.save(template);
        return orderTemplateConverter.entityToResponse(template);
    }

	@Override
	@Transactional
	public OrderTemplateResponse addTemplateContractItem(Long otId, InputBlockRequest inputBlock)
			throws EntityNotFoundException {
		OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + otId));
		log.info("orderTemplate:{}",orderTemplate);
    	Contract contract = orderTemplate.getContract();
    	log.info("contract:{}",contract);
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
    	block.setType(BlockType.text);
    	block.setContract(contract);
    	block = blockRepository.save(block);
//    	List<Block> blocks = contract.getBlocks();
//    	if (blocks == null) {
//    		blocks = new ArrayList<Block>();
//    	}
//    	blocks.add(block);
//    	contract.setBlocks(blocks);
//    	contract = contractRepository.save(contract);
//    	
//    	log.info("contract:{}",contract);
//    	OrderTemplate finalOrderTemplate = orderTemplateRepository.getReferenceById(otId);
//		return OrderTemplateConverter.INSTANCE.entityToResponse(finalOrderTemplate);
    	return orderTemplateConverter.entityToResponse(block.getContract().getOrderTemplate());
	}

	@Override
	@Transactional
	public BlockResponse updateTemplateContractItem(Long bId, InputBlockRequest otContractReq)
			throws EntityNotFoundException {
		Block block = blockRepository.findById(bId)
		.orElseThrow(() -> new EntityNotFoundException("Block not found with id: " + bId));
		
		block.setName(otContractReq.getName());
		block.setContext(otContractReq.getContext());
		block.setSort(otContractReq.getSort());
		
		block = blockRepository.save(block);
		
		return BlockConverter.INSTANCE.entityToResponse(block);
	}

	@Override
	@Transactional
	public OrderTemplateResponse deleteTemplateContractItem(Long bId) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public void setAllowSkipContract(Long otId, Boolean allowSkip) throws EntityNotFoundException {
		OrderTemplate orderTemplate = orderTemplateRepository.findById(otId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + otId));
		
		orderTemplate.setSkipContract(allowSkip);
		orderTemplateRepository.save(orderTemplate);
	}

	/**
	 * 根據名稱模糊查詢訂單模板列表
	 * @param name 模板名稱（支援模糊查詢）
	 * @return 符合條件的訂單模板列表
	 */
	@Override
	@Transactional
    public List<OrderTemplateResponse> findByName(String name) {
        List<OrderTemplate> orderTemplates = orderTemplateRepository.findByNameContainingIgnoreCase(name);
        return orderTemplates.stream()
                .map(orderTemplateConverter::entityToResponse)
                .toList();
    }


}
