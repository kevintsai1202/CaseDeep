package com.casemgr.service.impl;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.casemgr.entity.Favourite;
import com.casemgr.entity.Showcase;
import com.casemgr.entity.ShowcaseType;
import com.casemgr.entity.User;
import com.casemgr.repository.FavouriteRepository;
import com.casemgr.repository.ShowcaseRepository;
import com.casemgr.repository.ShowcaseTypeRepository;
import com.casemgr.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FavouriteService {
	private final FavouriteRepository favouriteRepository;
	private final UserRepository userRepository;
	private final ShowcaseRepository showcaseRepository;
	private final ShowcaseTypeRepository showcaseTypeRepository;
	
	
	public Favourite getMyFavourite() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> oUser = userRepository.findByUsername(auth.getName());
		User user = (User) oUser.get();
		Favourite favourite = user.getFavourite();
		if (favourite == null) {
			favourite = new Favourite();
			favourite.setShowcaseTypies(Set.of(new ShowcaseType("My Favourities")));
			favourite = favouriteRepository.save(favourite);
		}
		return favourite;
	}
	
	@Transactional
	public Favourite addPartner(Long uId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> oUser = userRepository.findByUsername(auth.getName());
		User user = (User) oUser.get();
		User partner = userRepository.getReferenceById(uId);
		Favourite favourite = user.getFavourite();
		Set<User> partners = favourite.getPartners();
		partners.add(partner);
		favourite.setPartners(partners);
		return favouriteRepository.save(favourite);
	}
	
	@Transactional
	public Favourite addShowcase(String stTitle, Long sId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> oUser = userRepository.findByUsername(auth.getName());
		User user = (User) oUser.get();
		ShowcaseType showcaseType = new ShowcaseType(stTitle);
		showcaseType = showcaseTypeRepository.save(showcaseType);
		Showcase showcase = showcaseRepository.getReferenceById(sId);
		showcaseType.getShowcases().add(showcase);
		showcaseTypeRepository.save(showcaseType);
		return user.getFavourite();
	}

}
