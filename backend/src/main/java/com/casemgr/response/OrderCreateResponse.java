package com.casemgr.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Slf4j
public class OrderCreateResponse implements Serializable {
	private String orderNoBase62;
}