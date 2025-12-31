package com.casemgr.service.impl;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.converter.ShowcaseConverter;
import com.casemgr.entity.Filedata;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.entity.Showcase;
import com.casemgr.entity.User;
import com.casemgr.repository.OrderTemplateRepository;
import com.casemgr.repository.ShowcaseRepository;
import com.casemgr.repository.UserRepository;
import com.casemgr.request.ShowcaseRequest;
import com.casemgr.response.FiledataResponse;
import com.casemgr.response.ShowcaseResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShowcaseServiceImpl {
	private final FileStorageServiceImpl fileService;
	private final UserServiceImpl userService;
	private final UserRepository userRepository;
	private final OrderTemplateRepository orderTemplateRepository;
	private final ShowcaseRepository showcaseRepository;
//	private final CategoryRepository categoryRepository;
	
	@Transactional
	public ShowcaseResponse createShowcase(ShowcaseRequest showcaseReq) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) userService.loadUserByUsername(auth.getName());
		Showcase showcase = ShowcaseConverter.INSTANCT.requestToEntity(showcaseReq);
		log.info("Showcase:{}",showcase);
		showcase.setUser(user);
		Showcase savedShowcase = showcaseRepository.save(showcase);
		return new ShowcaseResponse(savedShowcase);
	}
	
	@Transactional
	public FiledataResponse addFile(Long sId,  MultipartFile file) {
		Showcase sc = showcaseRepository.getReferenceById(sId);
		FiledataResponse filedataRes = fileService.save(sc, file);
		return filedataRes;
	}
	
	@Transactional
	public ShowcaseResponse addOrderTemplate(Long sId,  Long oId) {
		Showcase sc = showcaseRepository.getReferenceById(sId);
		OrderTemplate order = orderTemplateRepository.getReferenceById(oId);
		sc.setOrderTemplate(order);
		sc = showcaseRepository.save(sc);
		return new ShowcaseResponse(sc);
	}
	
	@Transactional
	public FiledataResponse changeFile(Long fId,  MultipartFile file) {
		
		Filedata filedata = fileService.getFiledata(fId);
		
		Showcase sc = filedata.getShowcase();
		FiledataResponse filedataRes = fileService.save(sc, file);
		fileService.deleteFile(fId);
		
		return filedataRes;
	}
	
//	@Transactional
//	public ShowcaseResponse setCategory(Long sId, Long cId) {
//		Showcase sc = showcaseRepository.getReferenceById(sId);
//		Category category = categoryRepository.getReferenceById(cId);
//		sc.setCategory(category);
//		Showcase savedSc = showcaseRepository.save(sc);
//		return new ShowcaseResponse(savedSc);
//	}
	
	public List<ShowcaseResponse> getMyShowcase(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) userService.loadUserByUsername(auth.getName());
		List<Showcase> showcases = user.getShowcases();
		List<ShowcaseResponse> showcaseResponses = showcases.stream().map(ShowcaseResponse::new).toList();
		return showcaseResponses;
	}
	
	public List<ShowcaseResponse> getProviderShowcase(Long uId){
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.getReferenceById(uId);
		List<Showcase> showcases = user.getShowcases();
		List<ShowcaseResponse> showcaseResponses = showcases.stream().map(ShowcaseResponse::new).toList();
		return showcaseResponses;
	}
	
	@Transactional
	public void deleteFile(Long fId) {
		fileService.deleteFile(fId);
	}
	
	@Transactional
	public void deleteFile(String uuid) {
		fileService.deleteFile(uuid);
	}
}
