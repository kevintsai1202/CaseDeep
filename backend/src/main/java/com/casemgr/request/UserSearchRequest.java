package com.casemgr.request;

import com.casemgr.enumtype.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用戶多條件查詢請求類
 * 支援按行業、用戶類型等條件進行篩選查詢
 */
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "用戶多條件查詢請求")
public class UserSearchRequest {
    
    @Schema(description = "行業名稱，'All Industries'表示全部行業", example = "All Industries")
    private String industry = "All Industries";
    
    @Schema(description = "用戶類型，null表示全部類型")
    private UserType userType;
    
    @Schema(description = "頁碼，從0開始", example = "0")
    private int page = 0;
    
    @Schema(description = "每頁數量", example = "20")
    private int size = 20;
    
    @Schema(description = "排序欄位", example = "displayOrder")
    private String sortBy = "displayOrder";
    
    @Schema(description = "排序方向，asc或desc", example = "asc")
    private String sortDir = "asc";
    
    @Schema(description = "搜尋關鍵字，可搜尋用戶名稱或顯示名稱")
    private String searchKeyword;
}