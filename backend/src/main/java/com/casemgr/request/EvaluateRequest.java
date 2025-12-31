package com.casemgr.request;

import java.io.Serializable;

import com.casemgr.enumtype.EvaluateItem;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EvaluateRequest implements Serializable {
	
	@NotNull(message = "Rate not null!")
	@Max(value = 10, message = "Can't more then 10")
	@Min(value = 1, message = "Can't less then 1")
	private Integer rate;
	
	@NotNull(message = "Type not null!")
	@Max(value = 2, message = "Can't more then 2")
	@Min(value = 0, message = "Can't less then 0")
	private Integer item;
	
	@Size(max = 2000)
	private String comment;	
}