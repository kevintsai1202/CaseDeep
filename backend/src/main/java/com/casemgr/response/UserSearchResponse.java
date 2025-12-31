package com.casemgr.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.casemgr.entity.User;

/**
 * 用戶查詢回應類
 * 統一的查詢回應格式，支援泛型以適應不同的用戶類型回應
 * 
 * @param <T> 用戶回應類型，可以是ProviderResponse、ClientResponse或混合類型
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用戶查詢回應")
public class UserSearchResponse {
    
    @Schema(description = "用戶列表")
    private List<ProviderResponse> users;
    
    @Schema(description = "統計資訊")
    private UserStatistics statistics;
}