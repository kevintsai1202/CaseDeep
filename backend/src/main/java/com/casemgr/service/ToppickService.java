package com.casemgr.service;

import java.util.List;

import com.casemgr.response.ToppickResponse;

public interface ToppickService {
    
    List<ToppickResponse> getToppicByIndustryAndCountry(String industry, String country);
    
    List<ToppickResponse> getToppicByIndustryAndCountryAndTemaplteName(String industry, String country, String templateName);
    
    List<ToppickResponse> getToppicByIndustry(String industry);
    
    List<ToppickResponse> getToppicByIndustryAndTemplateName(String industry, String templateName);
    
    List<ToppickResponse> getToppicByIndustryAndLocation(String industry, String location);
    
    List<ToppickResponse> getToppicByIndustryTemplateNameAndLocation(String industry, String templateName, String location);
}