package com.casemgr.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.entity.Filedata;
import com.casemgr.response.FiledataResponse;

public interface FileStorageService {

	void init();

	Filedata save(MultipartFile multipartFile);

    // 測試相容：提供別名方法（回傳 FiledataResponse）
    FiledataResponse storeFile(MultipartFile multipartFile);

	Resource load(String filename);
	
	void delete(String uuid);

	Stream<Path> load();

	void clear();

}
