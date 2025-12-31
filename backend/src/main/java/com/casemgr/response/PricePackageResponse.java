package com.casemgr.response;

import java.io.Serializable;
import java.util.List;

import com.casemgr.converter.ListItemConverter;
import com.casemgr.converter.OrderTemplateConverter;
import com.casemgr.entity.ListItem;
import com.casemgr.entity.PricePackage;
import com.casemgr.entity.Showcase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricePackageResponse implements Serializable {
	private Long pId;
	private Double price;
	private String packageName;
	private String packageDesc;
	private String fileUrl;
	private List<PriceListItemResponse> listItems;
	private OrderTemplateResponse orderTemplate;
	
    public PricePackageResponse(PricePackage pricePackage) {
//    	String homeURL = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
    	this.pId = pricePackage.getPId();
    	this.packageName = pricePackage.getPackageName();
    	this.packageDesc = pricePackage.getPackageDesc();
    	this.price = pricePackage.getPrice();
    	this.fileUrl = pricePackage.getFileUrl();
    	this.orderTemplate = OrderTemplateConverter.INSTANCE.entityToResponse(pricePackage.getOrderTemplate());
    	this.listItems = ListItemConverter.INSTANCE.entityToPriceItemResponse(pricePackage.getListItems());
    }
}