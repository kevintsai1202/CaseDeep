package com.casemgr.response;

import java.util.Set;
import java.util.stream.Collectors;

import com.casemgr.converter.UserConverter;
import com.casemgr.entity.Favourite;
import com.casemgr.entity.Order;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.entity.PricePackage;
import com.casemgr.entity.ShowcaseType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavouriteResponse {
	public FavouriteResponse(Favourite favourite) {
		this.partners = UserConverter.INSTANCT.entityToResponse(favourite.getPartners() );
		this.showcases = favourite.getShowcaseTypies();
		this.pricings = favourite.getPrices();
		this.orderTemplates = favourite.getOrderTemplates();
	}
	
	private Set<UserResponse> partners;
	
	private Set<ShowcaseType> showcases;
	
	private Set<PricePackage> pricings;
	
	private Set<OrderTemplate> orderTemplates;
}
