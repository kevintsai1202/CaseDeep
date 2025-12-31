package com.casemgr.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.casemgr.entity.BusinessProfile;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.entity.User;
import com.casemgr.repository.UserRepository;
import com.casemgr.response.ToppickResponse;
import com.casemgr.service.ToppickService;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import com.casemgr.entity.Location;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ToppickServiceImpl implements ToppickService {
	private final UserRepository userRepository;

	public List<ToppickResponse> getToppicByIndustryAndCountry(String industry, String country){
		List<ToppickResponse> toppicks = null;
		List<User> users = userRepository.findAll((user, query, cb) -> {
			Join<User, BusinessProfile> businessProfile = user.join("businessProfile");
			Predicate certifiedPredicate = cb.isTrue(user.get("certified"));
			Predicate industryPredicate = cb.equal(businessProfile.get("industry"), industry);
			Predicate countryPredicate = cb.equal(businessProfile.get("country"), country);
//			Predicate statePredicate = cb.equal(businessProfile.get("state"), state);
			return cb.and(certifiedPredicate, industryPredicate, countryPredicate);
		});
		if (users.size()==0) {
			users = userRepository.findAll((user, query, cb) -> {
				Join<User, BusinessProfile> businessProfile = user.join("businessProfile");
				Predicate certifiedPredicate = cb.isTrue(user.get("certified"));
				Predicate industryPredicate = cb.equal(businessProfile.get("industry"), industry);
				Predicate countryPredicate = cb.equal(businessProfile.get("country"), "United States");
//				Predicate statePredicate = cb.equal(businessProfile.get("state"), state);
				return cb.and(certifiedPredicate, industryPredicate, countryPredicate);
			});
		}
		toppicks = users.stream().map(ToppickResponse::new).toList();
		return toppicks;
	}
	
	public List<ToppickResponse> getToppicByIndustryAndCountryAndTemaplteName(String industry, String country, String temaplteName){
		List<ToppickResponse> toppicks = null;
		List<User> users = userRepository.findAll((user, query, cb) -> {
			Join<User, BusinessProfile> businessProfile = user.join("businessProfile");
			Join<User, OrderTemplate> orderTemplate = user.join("orderTemplates", JoinType.LEFT);
			Predicate certifiedPredicate = cb.isTrue(user.get("certified"));
			Predicate industryPredicate = cb.equal(businessProfile.get("industry"), industry);
			Predicate countryPredicate = cb.equal(businessProfile.get("country"), country);
//			Predicate statePredicate = cb.equal(businessProfile.get("state"), state);
			Predicate templateNamePredicate = cb.equal(orderTemplate.get("name"), temaplteName);
			return cb.and(certifiedPredicate, industryPredicate, countryPredicate, templateNamePredicate);
		});
		if (users.size()==0) {
			users = userRepository.findAll((user, query, cb) -> {
				Join<User, BusinessProfile> businessProfile = user.join("businessProfile");
				Join<User, OrderTemplate> orderTemplate = user.join("orderTemplates", JoinType.LEFT);
				Predicate certifiedPredicate = cb.isTrue(user.get("certified"));
				Predicate industryPredicate = cb.equal(businessProfile.get("industry"), industry);
				Predicate countryPredicate = cb.equal(businessProfile.get("country"), "United States");
//				Predicate statePredicate = cb.equal(businessProfile.get("state"), state);
				Predicate templateNamePredicate = cb.equal(orderTemplate.get("name"), temaplteName);
				return cb.and(certifiedPredicate, industryPredicate, countryPredicate, templateNamePredicate);
			});
		}
		toppicks = users.stream().map(ToppickResponse::new).toList();
		return toppicks;
	}

	public List<ToppickResponse> getToppicByIndustry(String industry) {
		List<ToppickResponse> toppicks = null;
		List<User> users = userRepository.findAll((user, query, cb) -> {
			Join<User, BusinessProfile> businessProfile = user.join("businessProfile");
			Predicate certifiedPredicate = cb.isTrue(user.get("certified"));
			Predicate industryPredicate = cb.equal(businessProfile.get("industry"), industry);
			return cb.and(certifiedPredicate, industryPredicate);
		});
		toppicks = users.stream().map(ToppickResponse::new).toList();
		return toppicks;
	}

	public List<ToppickResponse> getToppicByIndustryAndTemplateName(String industry, String templateName) {
		List<ToppickResponse> toppicks = null;
		List<User> users = userRepository.findAll((user, query, cb) -> {
			Join<User, BusinessProfile> businessProfile = user.join("businessProfile");
			Join<User, OrderTemplate> orderTemplate = user.join("orderTemplates", JoinType.LEFT);
			Predicate certifiedPredicate = cb.isTrue(user.get("certified"));
			Predicate industryPredicate = cb.equal(businessProfile.get("industry"), industry);
			Predicate templateNamePredicate = cb.equal(orderTemplate.get("name"), templateName);
			return cb.and(certifiedPredicate, industryPredicate, templateNamePredicate);
		});
		toppicks = users.stream().map(ToppickResponse::new).toList();
		return toppicks;
	}

	public List<ToppickResponse> getToppicByIndustryAndLocation(String industry, String location) {
		List<ToppickResponse> toppicks = null;
		List<User> users = userRepository.findAll((user, query, cb) -> {
			Join<User, BusinessProfile> businessProfile = user.join("businessProfile");
			Join<BusinessProfile, Location> locationJoin = businessProfile.join("location", JoinType.LEFT);
			Predicate certifiedPredicate = cb.isTrue(user.get("certified"));
			Predicate industryPredicate = cb.equal(businessProfile.get("industry"), industry);
			Predicate locationPredicate = cb.equal(locationJoin.get("location"), location);
			return cb.and(certifiedPredicate, industryPredicate, locationPredicate);
		});
		toppicks = users.stream().map(ToppickResponse::new).toList();
		return toppicks;
	}

	public List<ToppickResponse> getToppicByIndustryTemplateNameAndLocation(String industry, String templateName, String location) {
		List<ToppickResponse> toppicks = null;
		List<User> users = userRepository.findAll((user, query, cb) -> {
			Join<User, BusinessProfile> businessProfile = user.join("businessProfile");
			Join<User, OrderTemplate> orderTemplate = user.join("orderTemplates", JoinType.LEFT);
			Join<BusinessProfile, Location> locationJoin = businessProfile.join("location", JoinType.LEFT);
			Predicate certifiedPredicate = cb.isTrue(user.get("certified"));
			Predicate industryPredicate = cb.equal(businessProfile.get("industry"), industry);
			Predicate templateNamePredicate = cb.equal(orderTemplate.get("name"), templateName);
			Predicate locationPredicate = cb.equal(locationJoin.get("location"), location);
			return cb.and(certifiedPredicate, industryPredicate, templateNamePredicate, locationPredicate);
		});
		toppicks = users.stream().map(ToppickResponse::new).toList();
		return toppicks;
	}

}
