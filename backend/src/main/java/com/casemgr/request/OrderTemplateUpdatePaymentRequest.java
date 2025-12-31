package com.casemgr.request;

import java.io.Serializable;
import java.util.List;

import com.casemgr.entity.Discount;
import com.casemgr.entity.ListItem;
import com.casemgr.enumtype.OrderTemplateSelectType;
import com.casemgr.enumtype.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderTemplateUpdatePaymentRequest implements Serializable {
	@Schema(description = "PaymentMethods", allowableValues = {"FullPayment","Installment2_1","Installment2_2",
			"Installment2_3","Installment2_4","Installment2_5","Installment2_6",
			"Installment2_7","Installment2_8","Installment2_9","Installment3_1",
			"Installment4_1", "Installment5_1"})
	private List<PaymentMethod> paymentMethods;
}