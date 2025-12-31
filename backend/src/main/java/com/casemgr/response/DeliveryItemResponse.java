package com.casemgr.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.casemgr.enumtype.DeliveryStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeliveryItemResponse implements Serializable {

    private Long diId;
    private Long orderId; // Include associated order ID
    private String description;
//    private String fileUrl;
    private List<FiledataResponse> deliveries;
    private DeliveryStatus status;
    private Date deliveryDate;
    private String modificationRequestComment;
    private Date createTime;
    private Date updateTime;

    // Constructor or Mapper needed
}