package com.casemgr.request;

import java.io.Serializable;
import java.util.List;

import com.casemgr.enumtype.OrderTemplateSelectType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderTemplateUpdateDeliveryRequest implements Serializable {
	
	@Schema(description = "The selection type for the Delivery", example ="SelectedByTheCustomer, BusinessDaysForDelivery")
	private OrderTemplateSelectType deliveryType;
	
	@Schema(description = "When the deliveryType is set to BusinessDaysForDelivery, the businessDays field needs to be filled.")
	private Integer businessDays;
}