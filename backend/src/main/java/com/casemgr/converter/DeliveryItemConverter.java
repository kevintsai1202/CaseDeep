package com.casemgr.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.casemgr.entity.DeliveryItem;
import com.casemgr.entity.Filedata;
import com.casemgr.response.DeliveryItemResponse;
import com.casemgr.response.FiledataResponse;

@Mapper(componentModel="spring")
public interface DeliveryItemConverter{
    DeliveryItemConverter INSTANCE = Mappers.getMapper(DeliveryItemConverter.class);
    
    @Mapping(source = "order.OId", target = "orderId") // Map order ID
    DeliveryItemResponse entityToResponse(DeliveryItem entity);

    @Mapping(source = "order.OId", target = "orderId") // Map order ID
    List<DeliveryItemResponse> entityToResponse(List<DeliveryItem> entities);

    default FiledataResponse toFiledataResponse(Filedata filedata) {
        if (filedata == null) {
            return null;
        }
        FiledataResponse filedataResponse = new FiledataResponse();
        String homeURL = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        filedataResponse.setFId(filedata.getFId());
        filedataResponse.setFileName(filedata.getFileName());
        filedataResponse.setSize(filedata.getSize());
        filedataResponse.setUuid(filedata.getStorageUuid());
        filedataResponse.setUrl(homeURL + "/api/files/" + filedata.getStorageUuid());
        return filedataResponse;
    }

    // Add requestToEntity mapping if needed later
}