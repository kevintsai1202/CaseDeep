package com.casemgr.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.converter.ListItemConverter;
import com.casemgr.entity.ListItem;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.entity.PricePackage;
import com.casemgr.entity.User;
import com.casemgr.repository.ListItemRepository;
import com.casemgr.repository.OrderTemplateRepository;
import com.casemgr.repository.PricePackageRepository;
import com.casemgr.request.PriceListItemRequest;
import com.casemgr.request.PricePackageCreateRequest;
import com.casemgr.response.FiledataResponse;
import com.casemgr.response.PriceListItemResponse;
import com.casemgr.response.PricePackageResponse;
import com.casemgr.service.FileStorageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PricePackageServiceImpl {
	private final PricePackageRepository pricePackageRepository;
	private final UserServiceImpl userService;
	private final OrderTemplateRepository orderTemplateRepository;
	private final FileStorageService fileStorageService;
	private final ListItemRepository listItemRepository;
	
	
	public List<PricePackageResponse> getMyPricies(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) userService.loadUserByUsername(auth.getName());
		
		List<PricePackage> pricePackages = pricePackageRepository.findByProvider(user);
		List<PricePackageResponse> pricePackageResponses = pricePackages.stream().map(PricePackageResponse::new).toList();
		return pricePackageResponses;
	}
	
	public List<PricePackageResponse> getProviderPricies(Long uId){
		User user = userService.getUser(uId);
		List<PricePackage> pricePackages = pricePackageRepository.findByProvider(user);
		List<PricePackageResponse> pricePackageResponses = pricePackages.stream().map(PricePackageResponse::new).toList();
		return pricePackageResponses;
	}
	
	@Transactional
	public PricePackageResponse createPricePackage(PricePackageCreateRequest req) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println(auth.getName());
		User user = (User) userService.loadUserByUsername(auth.getName());
		
		PricePackage pricePackage = new PricePackage();
		pricePackage.setPackageName(req.getPackageName());
		pricePackage.setPackageDesc(req.getPackageDesc());
		pricePackage.setPrice(req.getPrice());
		pricePackage.setProvider(user);
		if (req.getOId() != null) {
			OrderTemplate order = orderTemplateRepository.getReferenceById(req.getOId());
			pricePackage.setOrderTemplate(order);
		}
		
//		pricePackage = pricePackageRepository.save(pricePackage);
		
		if ((req.getListItems() != null) && req.getListItems().size() > 0) {
			List<ListItem> listItems = ListItemConverter.INSTANCE.priceItemRequestToEntity(req.getListItems());
			for (ListItem item : listItems) {
				item.setPricePackage(pricePackage);
			}
//			listItemRepository.saveAll(listItems);
			pricePackage.setListItems(listItems);
		}
		
		pricePackage = pricePackageRepository.save(pricePackage);
//		pricePackage = pricePackageRepository.getReferenceById(pricePackage.getPId());
		log.info("pricePackage:{}", pricePackage);
		return new PricePackageResponse(pricePackage);
	}
	
	@Transactional
	public PricePackageResponse uploadFile(Long id, MultipartFile file) {
		PricePackage price = pricePackageRepository.getReferenceById(id);
		FiledataResponse fileData= new FiledataResponse(fileStorageService.save(file));
		price.setFileUrl(fileData.getUrl());
		price = pricePackageRepository.save(price);
		return new PricePackageResponse(price);	
	}

	@Transactional
	public PriceListItemResponse addListItem(Long pId, PriceListItemRequest itemReq) {
		PricePackage pricePackage = pricePackageRepository.getReferenceById(pId);
		ListItem listItem = ListItemConverter.INSTANCE.priceItemRequestToEntity(itemReq);
		listItem.setPricePackage(pricePackage);
		listItem = listItemRepository.save(listItem);
		return ListItemConverter.INSTANCE.entityToPriceItemResponse(listItem);
	}
	
	@Transactional
	public PriceListItemResponse updateListItem(Long iId, PriceListItemRequest itemReq) {
		ListItem listItem = listItemRepository.getReferenceById(iId);
		listItem.setName(itemReq.getName());
		listItem.setSelected(itemReq.getSelected());
		listItem.setBlockSort(itemReq.getSort());
		return ListItemConverter.INSTANCE.entityToPriceItemResponse(listItemRepository.save(listItem));
	}
	
	@Transactional
	public void deleteListItem(Long iId) {
		listItemRepository.deleteById(iId);
	}
	
	@Transactional
	public void deletePricePackage(Long pId) {
		PricePackage price = pricePackageRepository.getReferenceById(pId);
		log.info("Delete PricePackage:{}",price);
		pricePackageRepository.delete(price);
	}
	
	@Transactional
	public PricePackageResponse addOrderTemplate(Long pId, Long oId) {
		PricePackage pricePackage = pricePackageRepository.getReferenceById(pId);
		OrderTemplate order = orderTemplateRepository.getReferenceById(oId);
		pricePackage.setOrderTemplate(order);
		pricePackage = pricePackageRepository.save(pricePackage);
		return new PricePackageResponse(pricePackage);
	}
}
