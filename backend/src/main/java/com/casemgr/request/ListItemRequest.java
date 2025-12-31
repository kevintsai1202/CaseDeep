package com.casemgr.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ListItemRequest {
	@NotEmpty(message = "list item name not empty!")
	@Size(max = 45)
	private String name;
	private BigDecimal quantity;
	private BigDecimal unitPrice;
	private String unit;
	private Boolean selected;
	private Integer blockSort;
//	private Integer settlementSort;
}
