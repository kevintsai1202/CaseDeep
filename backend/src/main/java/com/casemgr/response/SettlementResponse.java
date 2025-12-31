package com.casemgr.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementResponse implements Serializable {
	private Integer frequency; //0:custom 1:all 2 / 3 / 4 / 5 / 10
	private Long total;
	private Long sum;
}
