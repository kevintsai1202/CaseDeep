package com.casemgr.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequest {
	@Email
    private String email;
	
	@NotEmpty
    private String username;
	
	@NotEmpty
    private String password;
}