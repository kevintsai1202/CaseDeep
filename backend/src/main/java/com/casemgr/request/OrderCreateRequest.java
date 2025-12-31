package com.casemgr.request;

import java.io.Serializable;

import com.casemgr.enumtype.OrderType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull; // Added NotNull for ID
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Added for potential use
public class OrderCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L; // Added serialVersionUID

    @NotNull(message = "Order Template ID cannot be null") // Added validation
    private Long orderTemplateId;

    @NotNull(message = "Order Name cannot be null") // Added validation
    private String name;
    // Consider adding other necessary fields if needed for creation,
    // e.g., client ID if not derived from security context.
    // private Long clientId;
    @NotNull(message = "Promoted or Direct")
    private OrderType orderType;
}