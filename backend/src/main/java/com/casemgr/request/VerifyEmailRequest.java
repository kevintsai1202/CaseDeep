package com.casemgr.request;



import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerifyEmailRequest {
	@NotNull
    private Long userId;
	@NotEmpty
    private String vCode;
}
