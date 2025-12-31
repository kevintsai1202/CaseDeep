package com.casemgr.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Location 創建/更新請求 DTO
 */
@Getter
@Setter
public class LocationRequest {
    
    @NotBlank(message = "Location name cannot be blank")
    @Size(max = 100, message = "Location name must be less than 100 characters")
    private String location;
}