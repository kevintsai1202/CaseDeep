package com.casemgr.request;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PricePackageCreateRequest implements Serializable {
	@NotEmpty(message = "Price package name not empty!")
	@Size(max = 45)
    private String packageName;
    
    private String packageDesc;
    
    private Double price;
    
    private List<PriceListItemRequest> listItems;

	private Long oId;
}