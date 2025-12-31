package com.casemgr.request;

import com.casemgr.entity.SysListItem;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SysListItemRequest {
	@NotEmpty(message = "System list name not empty!")
	@Size(max = 45)
	private String name;
	private String description;
	private Integer sort;
	private String type;
	private String iconUrl;
}
