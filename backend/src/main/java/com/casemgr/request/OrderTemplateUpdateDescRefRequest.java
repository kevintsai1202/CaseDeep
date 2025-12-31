package com.casemgr.request;

import java.io.Serializable;
import java.util.List;

import com.casemgr.entity.Discount;
import com.casemgr.entity.ListItem;
import com.casemgr.enumtype.PaymentMethod;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderTemplateUpdateDescRefRequest implements Serializable {
	private Boolean hasDescRef;
}