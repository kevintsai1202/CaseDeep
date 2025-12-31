package com.casemgr.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.entity.Filedata;
import com.casemgr.entity.Showcase;
import com.casemgr.entity.User;
import com.casemgr.exception.BusinessException;
import com.casemgr.repository.FiledataRepository;
import com.casemgr.response.FiledataResponse;
import com.casemgr.service.FileStorageService;
import com.casemgr.utils.UUIDTools;

import jakarta.transaction.Transactional;
import lombok.SneakyThrows;

@Service("FileStorageService")
public class FileStorageServiceImpl implements FileStorageService {

	private final Path path = Paths.get("fileStorage");

	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private FiledataRepository filedataRepository;
	
	
	@SneakyThrows
	private void verifyFile(MultipartFile file) {
		Long fileSize = file.getSize();
//		userRepository
		
		if (file.isEmpty()) {
			throw new BusinessException("File is empty");
		}
	}
	
	@Override
	public void init() {
		try {
			System.out.println("Storage Folder Path:"+path);
			if (Files.notExists(path)) {
				Files.createDirectory(path);
				System.out.println("Storage Folder Init success!");
			}else {
				System.out.println("Storage Folder exist!");				
			}

		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}

	@Override
	@Transactional
	public Filedata save(MultipartFile multipartFile) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) userService.loadUserByUsername(auth.getName());
			
			Filedata fileData = new Filedata();
			String uuid = UUIDTools.getUUID();
			long size = multipartFile.getSize();
			String uploadDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			Path subPath = path.resolve(uploadDate);
			if (Files.notExists(subPath)) {
				Files.createDirectory(subPath);
				System.out.println("Storage Sub Folder create success!");
			}else {
				System.out.println("Storage Sub Folder exist!");				
			}
			InputStream io = multipartFile.getInputStream();
			Files.copy(io, subPath.resolve(uuid));
			io.close();
			fileData.setStorageUuid(uuid);
			fileData.setStorageFolder(uploadDate);
			fileData.setSize(size);
			fileData.setFileName(multipartFile.getOriginalFilename());
			fileData.setOwner(user);
			Filedata savedFiledata = filedataRepository.save(fileData);
			System.out.println("File Name:"+savedFiledata.getFileName());
			return savedFiledata;
		} catch (IOException e) {
			throw new RuntimeException("Could not store the file. Error:" + e.getMessage());
		}
	}

	@Override
	@Transactional
	public FiledataResponse storeFile(MultipartFile multipartFile) {
		Filedata saved = save(multipartFile);
		return new FiledataResponse(saved);
	}
	
	@Transactional
	public FiledataResponse save(Showcase showcase, MultipartFile multipartFile) {
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) userService.loadUserByUsername(auth.getName());
			
			Filedata fileData = new Filedata();
			String uuid = UUIDTools.getUUID();
			long size = multipartFile.getSize();
			String uploadDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			Path subPath = path.resolve(uploadDate);
			if (Files.notExists(subPath)) {
				Files.createDirectory(subPath);
				System.out.println("Storage Sub Folder create success!");
			}else {
				System.out.println("Storage Sub Folder exist!");				
			}
			InputStream io = multipartFile.getInputStream();
			Files.copy(io, subPath.resolve(uuid));
			io.close();
			fileData.setStorageUuid(uuid);
			fileData.setStorageFolder(uploadDate);
			fileData.setSize(size);
			fileData.setFileName(multipartFile.getOriginalFilename());
			fileData.setShowcase(showcase);
			fileData.setOwner(user);
			Filedata savedFiledata = filedataRepository.save(fileData);
			System.out.println("File Name:"+savedFiledata.getFileName());
			return new FiledataResponse(savedFiledata);
		} catch (IOException e) {
			throw new RuntimeException("Could not store the file. Error:" + e.getMessage());
		}
	}
	
	public void deleteFile(Long fId) {
		filedataRepository.deleteById(fId);
	}
	
	public void deleteFile(String uuid) {
		filedataRepository.deleteByStorageUuid(uuid);
	}
	
	public Filedata getFiledata(Long fId) {
		return filedataRepository.getReferenceById(fId);
	}
	

	@Override
	public Resource load(String uuid) {
		Filedata fd = filedataRepository.findByStorageUuid(uuid);
		Path file = path.resolve(fd.getStorageFolder()+"/"+uuid);
		try {
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file.");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error:" + e.getMessage());
		}
	}

	@Override
	public Stream<Path> load() {
		try {
			return Files.walk(this.path, 1).filter(path -> !path.equals(this.path)).map(this.path::relativize);
		} catch (IOException e) {
			throw new RuntimeException("Could not load the files.");
		}
	}

	@Override
	public void clear() {
		FileSystemUtils.deleteRecursively(path.toFile());
	}

	@Override
	public void delete(String uuid) {
		filedataRepository.deleteByStorageUuid(uuid);
	}

}
