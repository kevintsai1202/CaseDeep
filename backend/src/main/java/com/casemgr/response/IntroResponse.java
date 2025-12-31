package com.casemgr.response;

import com.casemgr.entity.Account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntroResponse {
	private Long userId;
	private String title;
	private String content;
	private String vedioUrl;
	private String signatureUrl;
	private Account receivingAccount;
	private Account paymentAccount;
}
