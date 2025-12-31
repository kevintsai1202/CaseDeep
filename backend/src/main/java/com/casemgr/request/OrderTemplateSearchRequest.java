package com.casemgr.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 訂單模板搜尋請求
 */
@Data
public class OrderTemplateSearchRequest {
    
    /**
     * 模板名稱（支援模糊查詢）
     */
    @NotBlank(message = "Template name cannot be blank")
    private String name;
}