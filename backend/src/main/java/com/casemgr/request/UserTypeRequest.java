package com.casemgr.request;

import com.casemgr.enumtype.UserType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserTypeRequest {
	@NotNull
	private UserType userType;
}
