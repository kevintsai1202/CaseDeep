package com.casemgr.response;

import com.casemgr.entity.ListItem;
import com.casemgr.entity.SysListItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceListItemResponse {
	private Long lId;
	private String name;
	private Boolean selected;
	private Integer sort;
}
