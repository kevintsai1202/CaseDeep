package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.Order;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.request.OrderCreateRequest;
import com.casemgr.request.OrderTemplateRequest;
import com.casemgr.response.OrderResponse;
import com.casemgr.response.OrderTemplateResponse;


@Mapper(componentModel="spring")
public interface OrderConverter{
    OrderConverter INSTANCE = Mappers.getMapper(OrderConverter.class);

    // 預設使用 OrderResponse(Order) 建構子，確保欄位一致
    default OrderResponse entityToResponse(Order order) {
        if (order == null) return null;
        return new OrderResponse(order);
    }

    default List<OrderResponse> entityToResponse(List<Order> orders) {
        if (orders == null) return java.util.Collections.emptyList();
        return orders.stream().map(this::entityToResponse).toList();
    }
}
