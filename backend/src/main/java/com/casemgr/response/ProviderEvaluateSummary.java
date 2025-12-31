package com.casemgr.response;

import java.util.LinkedList;
import java.util.List;

import com.casemgr.entity.Evaluate;
import com.casemgr.enumtype.EvaluateItem;
import com.casemgr.enumtype.EvaluateType;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProviderEvaluateSummary {
	private Double completeRate;
	private Double recoveryRate;
	private Double acceptanceRate;
	private Double responsibility;
	private Double professionalism;
	private Double cooperativeness;
	private Integer honour;
	private Integer referendum;
	private Integer recommend;
}
