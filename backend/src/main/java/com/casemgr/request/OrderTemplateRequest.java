package com.casemgr.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderTemplateRequest implements Serializable {
	@NotEmpty(message = "Order template name not empty!")
	@Size(max = 45)
	private String name;
}