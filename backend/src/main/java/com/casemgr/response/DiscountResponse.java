package com.casemgr.response;

import com.casemgr.enumtype.DiscountType;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class DiscountResponse{

	private Long dId;
	private Long spend;
	private Long discount;
	private DiscountType discountType;
	private String description;
}
