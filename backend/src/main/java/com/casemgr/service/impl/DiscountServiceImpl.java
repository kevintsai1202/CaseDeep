package com.casemgr.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.converter.DiscountConverter;
import com.casemgr.converter.OrderTemplateConverter;
import com.casemgr.entity.Industry;
import com.casemgr.entity.Discount;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.entity.User;
import com.casemgr.enumtype.PaymentMethod;
import com.casemgr.repository.IndustryRepository;
import com.casemgr.repository.DiscountRepository;
import com.casemgr.repository.OrderTemplateRepository;
import com.casemgr.repository.UserRepository;
import com.casemgr.request.DiscountRequest;
import com.casemgr.request.OrderTemplateUpdateDeliveryRequest;
import com.casemgr.request.OrderTemplateUpdatePaymentRequest;
import com.casemgr.response.DiscountResponse;
import com.casemgr.response.FiledataResponse;
import com.casemgr.response.OrderTemplateResponse;
import com.casemgr.service.DiscountService;
import com.casemgr.service.FileStorageService;
import com.casemgr.service.OrderTemplateService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountServiceImpl implements DiscountService {
	private final DiscountRepository discountRepository;
	
	@Override
	@Transactional
	public DiscountResponse updateDiscount(Long dId, DiscountRequest discountReq) throws EntityNotFoundException {
		Discount discount = discountRepository.getReferenceById(dId);
		discount.setDiscount(discountReq.getDiscount());
		discount.setDiscountType(discountReq.getDiscountType());
		discount.setSpend(discountReq.getDiscount());
		discount = discountRepository.save(discount);
		return DiscountConverter.INSTANCE.entityToResponse(discount);
	}

	@Override
	@Transactional
	public void deleteDiscount(Long dId) throws EntityNotFoundException {
		discountRepository.deleteById(dId);
	}


}
