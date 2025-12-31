package com.casemgr.request;

import java.time.LocalDateTime;

import com.casemgr.enumtype.RevenueShareStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RevenueShareUpdateRequest {
    
    private RevenueShareStatus status;
    
    private LocalDateTime paymentTime;
    
    private String remarks;
}