package com.casemgr.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.PaymentCard;
import com.casemgr.response.PaymentCardResponse;

import java.util.List;


@Mapper(componentModel="spring")
public interface PaymentCardConverter{ 
    PaymentCardConverter INSTANCE = Mappers.getMapper(PaymentCardConverter.class);
    
    PaymentCardResponse entityToResponse(PaymentCard entity);
    List<PaymentCardResponse> entityToResponse(List<PaymentCard> entities);

    // Add requestToEntity mapping if needed later
}