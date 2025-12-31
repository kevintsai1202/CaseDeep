package com.casemgr.request;

import java.io.Serializable;
import java.util.List;

import com.casemgr.enumtype.OrderTemplateSelectType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderTemplateSkipContractRequest implements Serializable {
	private Boolean skipContract;
}