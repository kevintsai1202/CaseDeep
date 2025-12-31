package com.casemgr.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContractRequest implements Serializable {
	@NotEmpty(message = "Contract name not empty!")
	@Size(max = 45)
	private String name;
	@Size(max = 1000)
	private String description;
	@NotEmpty(message = "Contract type not empty!")
	private String type;
}