package com.casemgr.response;

import java.util.List;

import com.casemgr.converter.OrderTemplateConverter;
import com.casemgr.converter.ShowcaseConverter;
import com.casemgr.converter.UserConverter;
import com.casemgr.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToppickResponse {
	private UserResponse userData;
	private List<OrderTemplateResponse> orderTemplates;
	private List<ShowcaseResponse> showcases;
	private List<PricePackageResponse> pricePackages;
	
	public ToppickResponse(User user) {
		this.userData = UserConverter.INSTANCT.entityToResponse(user);
		this.orderTemplates = OrderTemplateConverter.INSTANCE.entityToResponse(user.getOrderTemplates());
		this.showcases = user.getShowcases().stream().map(ShowcaseResponse::new).toList();
		this.pricePackages = user.getPricePackages().stream().map(PricePackageResponse::new).toList();
	}
}
