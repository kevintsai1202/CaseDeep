package com.casemgr.request;

import java.io.Serializable;
import java.time.Instant;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BidRequest implements Serializable {
	@NotNull(message = "Category not empty!")
	private Long categoryId;
	@NotEmpty(message = "Description not empty!")
	@Size(max = 1000)
	private String requirement;
	@NotNull(message = "Inquiry Date not empty!")
	private Instant inquiryDate;
	
	private Long minBudget;
	
	private Long maxBudget;
}