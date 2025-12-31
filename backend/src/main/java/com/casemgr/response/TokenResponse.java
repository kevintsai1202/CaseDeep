package com.casemgr.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
	private String message;
	private Long userId;
	private String token;
	
	public TokenResponse(String message) {
		this.message = message;
		this.userId = 0L;
		this.token = "";
	}
}
