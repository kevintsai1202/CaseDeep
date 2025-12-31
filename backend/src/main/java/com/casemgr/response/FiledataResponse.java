package com.casemgr.response;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.casemgr.entity.Filedata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiledataResponse {
	private Long fId;
	private String fileName;
	private String uuid;
	private Long size;
    private String url;  
    
    public FiledataResponse(Filedata filedata) {
    	String homeURL = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
    	this.fId = filedata.getFId();
    	this.fileName = filedata.getFileName();
    	this.size = filedata.getSize();
    	this.url = homeURL+"/api/files/"+filedata.getStorageUuid();
    	this.uuid = filedata.getStorageUuid();
    }

    // 測試相容：提供 setFileUrl 別名方法
    public void setFileUrl(String url) {
        this.url = url;
    }
}
