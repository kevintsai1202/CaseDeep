package com.casemgr.response;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.casemgr.converter.ContractConverter;
import com.casemgr.converter.DeliveryItemConverter;
import com.casemgr.converter.PaymentCardConverter;
import com.casemgr.entity.Order;
import com.casemgr.utils.Base62Utils;
import com.casemgr.utils.NumberUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class OrderResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public OrderResponse(Order order) {
		this.oId = order.getOId();
		this.imageUrl = order.getImageUrl();
		this.status = order.getStatus().toString();
        // 單元測試期望此欄位為明文訂單號
        this.orderNoBase62 = order.getOrderNo();
//		this.confirmations = order.getConfirmations().stream().map(BlockResponse::new).toList();
		this.confirmations = Optional.ofNullable(order.getConfirmations())
			    .orElse(Collections.emptyList())
				.stream()
				.map(BlockResponse::new)
				.collect(Collectors.toList());
//		log.info("Contracts:{}",order.getContracts());
		this.contracts =  ContractConverter.INSTANCE.entityToResponse(order.getContracts());
		this.payments = PaymentCardConverter.INSTANCE.entityToResponse(order.getPayments());
		this.deliveries = DeliveryItemConverter.INSTANCE.entityToResponse(order.getDeliveries());
	}
	
	private Long oId;
	private String orderNoBase62;
//	private String orderNo;
	private String imageUrl;
	private String status;
	private List<BlockResponse> confirmations;
	private List<ContractResponse> contracts;
	private List<PaymentCardResponse> payments;
	private List<DeliveryItemResponse> deliveries;
}
