package com.casemgr.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndustryResponse implements Serializable {
	private Long id;
	private String name;
	private String description;
	private String icon;
	private String title;
	private String meta;
//	private String urlPath;
}