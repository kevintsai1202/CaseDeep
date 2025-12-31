package com.casemgr.service.impl;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.converter.UserConverter;
import com.casemgr.entity.Account;
import com.casemgr.entity.BusinessProfile;
import com.casemgr.entity.PersonProfile;
import com.casemgr.entity.User;
import com.casemgr.repository.BusinessProfileRepository;
import com.casemgr.repository.PersonProfileRepository;
import com.casemgr.repository.UserRepository;
import com.casemgr.request.BusinessProfileRequest;
import com.casemgr.request.PersonProfileRequest;
import com.casemgr.response.FiledataResponse;
import com.casemgr.response.IntroResponse;
import com.casemgr.service.FileStorageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {
	private final PersonProfileRepository personProfileRepository;
	
	private final BusinessProfileRepository businessProfileRepository;
	
	private final FileStorageService fileStorageService;
	
	private final UserServiceImpl userService;
	
	private final UserRepository userRepository;
	
	@Transactional
	public IntroResponse updateIntroVideo(MultipartFile file) {
		FiledataResponse fileData = new FiledataResponse(fileStorageService.save(file));
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		User user = (User) userService.loadUserByUsername(auth.getName());
//		User user = (User) oUser.get();
		
		user.setVedioUrl(fileData.getUrl());
		User savedUser = userRepository.save(user);
		return UserConverter.INSTANCT.entityToIntroResponse(savedUser);
	}
	
	@Transactional
	public IntroResponse updateSignature(MultipartFile file) {
		FiledataResponse fileData = new FiledataResponse(fileStorageService.save(file));
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		User user = (User) userService.loadUserByUsername(auth.getName());
//		User user = (User) oUser.get();
		
		user.setSignatureUrl(fileData.getUrl());
		User savedUser = userRepository.save(user);
		return UserConverter.INSTANCT.entityToIntroResponse(savedUser);
	}
	
	@Transactional
	public IntroResponse updateReceivingAccount(Account account) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		User user = (User) userService.loadUserByUsername(auth.getName());
//		User user = (User) oUser.get();
		
		user.setReceivingAccount(account);
		User savedUser = userRepository.save(user);
		return UserConverter.INSTANCT.entityToIntroResponse(savedUser);
	}
	
	@Transactional
	public IntroResponse updatePaymentAccount(Account account) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		User user = (User) userService.loadUserByUsername(auth.getName());
//		User user = (User) oUser.get();
		
		user.setPaymentAccount(account);
		User savedUser = userRepository.save(user);
		return UserConverter.INSTANCT.entityToIntroResponse(savedUser);
	}
	
	@Transactional
	public IntroResponse updateIntroVideo(String url) {
//		FiledataResponse fileData = fileStorageService.save(file);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		User user = (User) userService.loadUserByUsername(auth.getName());
//		User user = (User) oUser.get();
		
		user.setVedioUrl(url);
		User savedUser = userRepository.save(user);
		return UserConverter.INSTANCT.entityToIntroResponse(savedUser);
	}
	
	@Transactional
	public IntroResponse updateIntroTitle( String title ) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		Optional<User> oUser = userRepository.findByUsername(auth.getName());
		User user = (User) oUser.get();
		
		user.setTitle(title);
		User savedUser = userRepository.save(user);
		return UserConverter.INSTANCT.entityToIntroResponse(savedUser);
	}
	
	@Transactional
	public IntroResponse updateIntroContent( String content ) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		Optional<User> oUser = userRepository.findByUsername(auth.getName());
		User user = (User) oUser.get();
		
		user.setContent(content);
		User savedUser = userRepository.save(user);
		return UserConverter.INSTANCT.entityToIntroResponse(savedUser);
	}
	
	public IntroResponse getUserIntro( Long userId ) {
		User user = userRepository.getReferenceById(userId);
		return UserConverter.INSTANCT.entityToIntroResponse(user);
	}
	
	@Transactional
	public PersonProfile updatePersonProfile(PersonProfileRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
//		User user = (User) userRepository.getReferenceById(id);
		PersonProfile profile = user.getPersonProfile();
		if (profile == null) profile = new PersonProfile();
		profile.setAddress(request.getAddress());
		profile.setBirthDate(request.getBirthDate());
		profile.setCity(request.getCity());
		profile.setCountry(request.getCountry());
		profile.setCurrency(request.getCurrency());
		profile.setIdNumber(request.getIdNumber());
		profile.setIndustry(request.getIndustry());
		profile.setJobTitle(request.getJobTitle());
		profile.setLanguage(request.getLanguage());
		profile.setLegalFullName(request.getLegalFullName());
		profile.setPhone(request.getPhone());
		profile.setState(request.getState());
		profile.setZipCode(request.getZipCode());
		profile.setUser(user);
		profile = personProfileRepository.save(profile);
//		user.setPersonProfile(profile);
		return profile;
	}
	

	@Transactional
	public BusinessProfile updateBunissProfile(BusinessProfileRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		
//		User user = (User) userRepository.getReferenceById(id);
		BusinessProfile profile =user.getBusinessProfile();
		if (profile == null) profile = new BusinessProfile();
		profile.setAddress(request.getAddress());
//		profile.setBirthDate(request.getBirthDate());
		profile.setContactFullName(request.getContactFullName());
		profile.setStartDate(request.getStartDate());
		profile.setCity(request.getCity());
		profile.setCountry(request.getCountry());
		profile.setCurrency(request.getCurrency());
		profile.setIdNumber(request.getIdNumber());
		profile.setIndustry(request.getIndustry());
		profile.setJobTitle(request.getJobTitle());
		profile.setLanguage(request.getLanguage());
		profile.setLegalBusinessName(request.getLegalBusinessName());
		profile.setPhone(request.getPhone());
		profile.setState(request.getState());
		profile.setZipCode(request.getZipCode());
		profile.setUser(user);
		profile = businessProfileRepository.save(profile);
		return profile;
	}
}
