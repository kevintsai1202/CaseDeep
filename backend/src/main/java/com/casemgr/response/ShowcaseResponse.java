package com.casemgr.response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.casemgr.converter.IndustryConverter;
import com.casemgr.converter.OrderTemplateConverter;
import com.casemgr.entity.Showcase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowcaseResponse {
	private Long sId;
	private String title;
	private List<FiledataResponse> files = new ArrayList<>();
	private OrderTemplateResponse orderTemplate;
		
    public ShowcaseResponse(Showcase showcase) {
//    	String homeURL = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
    	this.sId = showcase.getSId();
    	this.title = showcase.getTitle();
    	if (showcase.getFiledatas() != null) {
    		this.files = showcase.getFiledatas().stream().map(FiledataResponse::new).toList();
    	}
    	this.orderTemplate = OrderTemplateConverter.INSTANCE.entityToResponse(showcase.getOrderTemplate());
    }
}
