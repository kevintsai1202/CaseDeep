package com.casemgr.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.casemgr.entity.Discount;
import com.casemgr.request.DiscountRequest;
import com.casemgr.request.InputBlockRequest;
import com.casemgr.request.OptionBlockRequest;
import com.casemgr.request.OrderTemplateRequest;
import com.casemgr.request.OrderTemplateUpdateDeliveryRequest;
import com.casemgr.request.OrderTemplateUpdatePaymentRequest;
import com.casemgr.request.OrderTemplateUpdateStartingPriceRequest;
import com.casemgr.response.BlockResponse;
import com.casemgr.response.DiscountResponse;
import com.casemgr.response.OrderTemplateResponse;

import jakarta.persistence.EntityNotFoundException;

public interface OrderTemplateService {

//	OrderTemplateResponse create(Long caId, OrderTemplateRequest orderTemplateRequest);
//	OrderTemplateResponse update(Long otId, OrderTemplateRequest orderTemplateRequest) throws EntityNotFoundException;
//	OrderTemplateResponse detail(Long otId) throws EntityNotFoundException;
	List<OrderTemplateResponse> list();
//	List<OrderTemplateResponse> listIndustryOrders(Long iId)throws EntityNotFoundException;
	List<OrderTemplateResponse> listMyTemplate();
	List<OrderTemplateResponse> listProviderTemplate(Long uId);
	
	OrderTemplateResponse createTemplate(String name);
	OrderTemplateResponse updateTemplateName(Long otId, String name) throws EntityNotFoundException;
	OrderTemplateResponse uploadTemplateImage(Long otId, MultipartFile file) throws EntityNotFoundException;
	OrderTemplateResponse uploadTemplateImage(Long otId, String fileUrl) throws EntityNotFoundException;
	OrderTemplateResponse updateTemplatePayment(Long otId, OrderTemplateUpdatePaymentRequest paymentReq) throws EntityNotFoundException;
	OrderTemplateResponse updateTemplateDelivery(Long otId, OrderTemplateUpdateDeliveryRequest deliveryDateReq) throws EntityNotFoundException;
	OrderTemplateResponse updateTemplateDescRef(Long otId, Boolean hasDescRef) throws EntityNotFoundException;
	
	OrderTemplateResponse updateTemplateStartingPrice(Long otId,  OrderTemplateUpdateStartingPriceRequest startingPriceReq)throws EntityNotFoundException;
	

	OrderTemplateResponse addTemplateDiscount(Long otId,  DiscountRequest discountReq)throws EntityNotFoundException;
	
	DiscountResponse updateTemplateDiscount(Long otId, Long dId,  DiscountRequest discountReq)throws EntityNotFoundException;
	
	OrderTemplateResponse addTemplateInput(Long otId,  InputBlockRequest inputBlockReq)throws EntityNotFoundException;
	OrderTemplateResponse addTemplateOption(Long otId,  OptionBlockRequest optionBlockReq)throws EntityNotFoundException;
	
	BlockResponse updateTemplateInput(Long bId,  InputBlockRequest inputBlockReq)throws EntityNotFoundException;
	BlockResponse updateTemplateOption(Long bId,  OptionBlockRequest optionBlockReq)throws EntityNotFoundException;
	OrderTemplateResponse deleteTemplateBlockItem(Long bId) throws EntityNotFoundException;
	
	OrderTemplateResponse addTemplateContractItem(Long otId, InputBlockRequest otContractReq) throws EntityNotFoundException;
	BlockResponse updateTemplateContractItem(Long bId,  InputBlockRequest otContractReq) throws EntityNotFoundException;
	
	OrderTemplateResponse deleteTemplateContractItem(Long bId) throws EntityNotFoundException;
	
	OrderTemplateResponse templateDetail(Long otId) throws EntityNotFoundException;
	
	void setAllowSkipContract(Long otId, Boolean allowSkip)throws EntityNotFoundException;
	
	void deleteOrderTemplate(Long otId) throws EntityNotFoundException;
//	OrderTemplateResponse create(Long caId, OrderTemplateRequest orderTemplateRequest);
	
	/**
	 * 根據名稱模糊查詢訂單模板列表
	 * @param name 模板名稱（支援模糊查詢）
	 * @return 符合條件的訂單模板列表
	 */
	List<OrderTemplateResponse> findByName(String name);
}
