package com.casemgr.response;

import java.util.List;
import java.util.stream.Collectors;

import com.casemgr.converter.ContractConverter;
import com.casemgr.converter.DiscountConverter;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.enumtype.OrderTemplateSelectType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTemplateResponse {
	private Long otId;
	private String name;
	private String imageUrl;
	private Boolean hasDescRef;
	private List<String> paymentMethods;
	private Integer businessDays;
	private OrderTemplateSelectType deliveryType;
	private Integer startingPrice;
	private List<DiscountResponse> discounts;
	private List<BlockResponse> blocks;
	private ContractResponse contract;
	
	
	public OrderTemplateResponse(OrderTemplate orderTemplate) {
		this.otId = orderTemplate.getOtId();
		this.name = orderTemplate.getName();
		this.imageUrl = orderTemplate.getImageUrl();
		this.hasDescRef = orderTemplate.getHasDescRef();
		this.paymentMethods = orderTemplate.getPaymentMethods();
		this.deliveryType = orderTemplate.getDeliveryType();
		this.businessDays = orderTemplate.getBusinessDays();
		this.startingPrice = orderTemplate.getStartingPrice();
		this.discounts = DiscountConverter.INSTANCE.entityToResponse(orderTemplate.getDiscounts());
		this.blocks = orderTemplate.getBlocks().stream().map(BlockResponse::new).collect(Collectors.toList());
		this.contract = ContractConverter.INSTANCE.entityToResponse(orderTemplate.getContract());
		}
}