package com.casemgr.request;

import java.time.LocalDateTime;

import com.casemgr.enumtype.RevenueShareStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RevenueShareStatusUpdateRequest {
    
    @NotNull(message = "Status cannot be null")
    private RevenueShareStatus status;
    
    private LocalDateTime paymentTime;
}