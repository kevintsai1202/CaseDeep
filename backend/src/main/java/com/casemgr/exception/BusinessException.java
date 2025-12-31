package com.casemgr.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BusinessException extends RuntimeException {
	public BusinessException(String message) {
		super(message);
	}
}
