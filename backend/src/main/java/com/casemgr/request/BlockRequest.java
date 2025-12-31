package com.casemgr.request;

import java.io.Serializable;

import com.casemgr.enumtype.BlockType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlockRequest implements Serializable {
	@NotEmpty(message = "Block name not empty!")
	@Size(max = 45)
	private String name;
	
	@NotNull(message = "Sort not null!")
	private Integer sort;
	
	@Size(max = 2000)
	private String context;
	
	private Boolean multiple;
	
	private BlockType type;
	
	private Long fileId;
	
}