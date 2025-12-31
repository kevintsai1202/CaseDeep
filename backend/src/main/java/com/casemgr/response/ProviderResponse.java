package com.casemgr.response;

import com.casemgr.entity.BusinessProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Provider用戶回應類
 * 包含Provider用戶的完整資訊和業務數據
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Provider用戶回應")
public class ProviderResponse {
    
    @Schema(description = "用戶ID", example = "12345")
    private Long userId;
    
    @Schema(description = "用戶名稱", example = "provider123")
    private String username;
    
    @Schema(description = "電子郵件", example = "provider@example.com")
    private String email;
    
    @Schema(description = "商業檔案資訊")
    private BusinessProfile businessProfile;
    
    @Schema(description = "標題", example = "專業服務提供者")
    private String title;
    
    @Schema(description = "內容描述", example = "提供高品質的專業服務")
    private String content;
    
    @Schema(description = "影片URL", example = "https://example.com/video.mp4")
    private String vedioUrl;
    
    @Schema(description = "簽名URL", example = "https://example.com/signature.png")
    private String signatureUrl;
    
    // 基本資訊
    @Schema(description = "是否已認證", example = "true")
    private Boolean certified;
    
    @Schema(description = "註冊日期")
    private LocalDateTime registrationDate;
    
    @Schema(description = "最後活躍日期")
    private LocalDateTime lastActiveDate;
    
    @Schema(description = "顯示順序", example = "1")
    private Integer displayOrder;
    
    // 排名相關
    @Schema(description = "排名分數", example = "8.5")
    private Double rankingScore;
    
    @Schema(description = "佣金率", example = "0.15")
    private Double commissionRate;
    
    // 效能指標
    @Schema(description = "效能指標")
    private PerformanceMetrics performance;
    
    // 業務數據
    @Schema(description = "業務指標")
    private BusinessMetrics business;
}