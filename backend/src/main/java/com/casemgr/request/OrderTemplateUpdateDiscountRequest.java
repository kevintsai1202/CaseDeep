package com.casemgr.request;

import java.io.Serializable;
import java.util.List;

import com.casemgr.entity.Discount;
import com.casemgr.entity.ListItem;
import com.casemgr.enumtype.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderTemplateUpdateDiscountRequest implements Serializable {
	
	@Schema(description = "Discount type", allowableValues = {"Percentage", "Fixed"}, example = "Percentage/Fixed")
	private List<Discount> discounts;
}