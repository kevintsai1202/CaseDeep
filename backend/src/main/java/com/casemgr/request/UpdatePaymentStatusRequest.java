package com.casemgr.request;

import com.casemgr.enumtype.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePaymentStatusRequest {

    @NotNull(message = "New payment status cannot be null")
    private PaymentStatus newStatus;

    // Optional: Add a reason field if needed for status changes
    // private String reason;
}