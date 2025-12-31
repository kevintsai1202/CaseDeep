package com.casemgr.response;

import com.casemgr.entity.SysListItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysListItemResponse {
	private Long slId;
	private String name;
	private String description;
	private Integer sort;
	private String type;
	private String iconUrl;
}
