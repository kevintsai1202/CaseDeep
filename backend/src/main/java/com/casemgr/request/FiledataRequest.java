package com.casemgr.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FiledataRequest implements Serializable {
	@NotEmpty(message = "File name not empty!")
	@Size(max = 256)
	private String fileName;
	
	@NotEmpty(message = "UUID not empty!")
	@Size(max = 100)
	private String uuid;
	
	@NotEmpty(message = "Folder not empty!")
	@Size(max = 10)
	private String folder;
	
	@NotNull(message = "File size not empty!")
	private Long size;
}