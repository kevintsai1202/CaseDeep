package com.casemgr.request;

import com.casemgr.enumtype.CEStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCertificationUpdateRequest {

    @NotNull(message = "New status cannot be null")
    private CEStatus newStatus;

    private String reviewComment; // Optional comment, especially for rejection

    private Double score; // Optional score, typically provided on approval
}