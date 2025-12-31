package com.casemgr.request;



import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BusinessProfileRequest {   
	
	private String legalBusinessName;
    
    private String idNumber;
    
    private LocalDate startDate;
    
//    private LocalDateTime birthDate;
    
    private String contactFullName;
    
    private String jobTitle;
    
    private String industry;
    
    private String phone;
    
    private String currency;
    
    private String language;
    
    private String country;
    
    private String state;
    
    private String Address;
    
    private String City;
    
    private String zipCode;
}
