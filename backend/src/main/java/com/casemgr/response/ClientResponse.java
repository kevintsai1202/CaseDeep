package com.casemgr.response;

import com.casemgr.entity.PersonProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Client用戶回應類
 * 包含Client用戶的完整資訊和業務數據
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Client用戶回應")
public class ClientResponse {
    
    @Schema(description = "用戶ID", example = "12345")
    private Long userId;
    
    @Schema(description = "用戶名稱", example = "client123")
    private String username;
    
    @Schema(description = "電子郵件", example = "client@example.com")
    private String email;
    
    @Schema(description = "個人檔案資訊")
    private PersonProfile personProfile;
    
    @Schema(description = "地區", example = "TW")
    private String region;
    
    @Schema(description = "貨幣符號", example = "NT$")
    private String currencySymbol;
    
    // 基本資訊
    @Schema(description = "註冊日期")
    private LocalDateTime registrationDate;
    
    @Schema(description = "最後活躍日期")
    private LocalDateTime lastActiveDate;
    
    @Schema(description = "平均評分", example = "4.2")
    private Double rating;
    
    @Schema(description = "顯示順序", example = "5")
    private Integer displayOrder;
    
    // 業務數據
    @Schema(description = "業務指標")
    private ClientBusinessMetrics business;
}