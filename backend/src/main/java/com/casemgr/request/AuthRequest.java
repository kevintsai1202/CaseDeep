package com.casemgr.request;



import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthRequest {
	@NotEmpty
    private String username;
	@NotEmpty
    private String password;
}
