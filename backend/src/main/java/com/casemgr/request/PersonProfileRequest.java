package com.casemgr.request;



import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonProfileRequest {   
	
	private String legalFullName;
    
    private String idNumber;
    
    private LocalDate birthDate;
    
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
