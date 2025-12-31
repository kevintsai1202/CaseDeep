package com.casemgr.request;

import com.casemgr.enumtype.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用戶統計查詢請求類
 * 用於獲取用戶統計資訊的篩選條件
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "用戶統計查詢請求")
public class UserStatisticsRequest {
    
    @Schema(description = "行業篩選，可選")
    private String industry;
    
    @Schema(description = "用戶類型篩選，可選")
    private UserType userType;
}