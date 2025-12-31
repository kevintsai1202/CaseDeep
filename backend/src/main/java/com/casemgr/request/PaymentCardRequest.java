package com.casemgr.request;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import jakarta.validation.constraints.DecimalMin; // For validation
import jakarta.validation.constraints.NotNull;   // For validation
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentCardRequest implements Serializable {

    // orderId is usually passed in the path or set by the service, not in request body

//    @NotNull(message = "Installment number cannot be null")
//    private Integer installmentNumber; // Which installment this card represents (e.g., 6 for an extra payment)

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount; // Amount for this specific payment

//    private Date dueDate; // Optional due date

    // Receiving account info might be pre-filled from provider's profile or specified here

    // Other relevant fields? E.g., a description for the extra payment
//    private String description;
}