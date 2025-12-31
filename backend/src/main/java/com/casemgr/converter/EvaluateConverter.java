package com.casemgr.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.casemgr.entity.Evaluate;
import com.casemgr.response.EvaluateResponse;
import com.casemgr.utils.Base62Utils;

import java.util.List;


@Mapper(componentModel="spring")
public interface EvaluateConverter{
    EvaluateConverter INSTANCE = Mappers.getMapper(EvaluateConverter.class);
//    @Mapping(source = "order.", target = "orderId")
    @Mapping(source = "evaluator.UId", target = "evaluatorId")
    @Mapping(source = "evaluatee.UId", target = "evaluateeId")
    @Mapping(source = "EId", target = "EId")
    EvaluateResponse entityToResponse(Evaluate entity);

//    @Mapping(source = "order.OId", target = "orderId")
    @Mapping(source = "evaluator.UId", target = "evaluatorId")
    @Mapping(source = "evaluatee.UId", target = "evaluateeId")
    @Mapping(source = "EId", target = "EId")
    List<EvaluateResponse> entityToResponse(List<Evaluate> entities);

    
    // static String idToBase62(Long id) {
    //     return id == null ? null : Base62Utils.encode(id.toString());
    // }
    // Add requestToEntity mapping if needed later (e.g., from RatingRequest)
}