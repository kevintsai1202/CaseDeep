package com.casemgr.request;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Industry 創建/更新請求 DTO
 */
@Getter
@Setter
public class IndustryRequest {
    
    // 英文內容（主要語言）
    @NotBlank(message = "Industry name cannot be blank") // 新增驗證
    @Size(max = 100, message = "Industry name must be less than 100 characters") // 新增驗證
    private String name;

    @NotBlank(message = "Title cannot be blank") // 新增驗證
    @Size(max = 255, message = "Title must be less than 255 characters") // 新增驗證
    private String title;

    private String description;

    @Size(max = 500, message = "Meta must be less than 500 characters") // 新增驗證
    private String meta;

//    @NotBlank(message = "URL path cannot be blank") // 新增驗證
//    @Size(max = 255, message = "URL path must be less than 255 characters") // 新增驗證
//    private String urlPath;

    @Size(max = 255, message = "Icon path must be less than 255 characters") // 新增驗證
    private String icon;
    private Long parentId;
    
}