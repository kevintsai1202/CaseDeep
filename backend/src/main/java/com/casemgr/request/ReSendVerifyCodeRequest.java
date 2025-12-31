package com.casemgr.request;



import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReSendVerifyCodeRequest {
	@NotNull
    private Long userId;
}
