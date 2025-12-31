package com.casemgr.service;

import com.casemgr.request.DiscountRequest;
import com.casemgr.response.DiscountResponse;

import jakarta.persistence.EntityNotFoundException;

public interface DiscountService {
	DiscountResponse updateDiscount(Long dId, DiscountRequest discountReq)throws EntityNotFoundException;	
	void deleteDiscount(Long dId) throws EntityNotFoundException;
}
