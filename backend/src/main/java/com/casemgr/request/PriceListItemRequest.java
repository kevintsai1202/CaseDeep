package com.casemgr.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PriceListItemRequest {
	@NotEmpty(message = "list item name not empty!")
	@Size(max = 45)
	private String name;
	private Boolean selected;
	private Integer sort;
}
