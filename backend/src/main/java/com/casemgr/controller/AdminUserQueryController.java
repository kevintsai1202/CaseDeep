package com.casemgr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.request.UserSearchRequest;
import com.casemgr.request.UserStatisticsRequest;
import com.casemgr.response.UserSearchResponse;
import com.casemgr.response.UserStatistics;
import com.casemgr.service.AdminUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Admin 使用者查詢/統計 控制器
 */
@Tag(name = "Admin User Query", description = "管理員使用者查詢/統計相關 API")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserQueryController {

    private final AdminUserService adminUserService;

    /**
     * 多條件查詢使用者
     */
    @GetMapping("/search")
    @Operation(summary = "Search Users", description = "Search users with filters and return list with statistics")
    public ResponseEntity<UserSearchResponse> searchUsers(
            @Parameter(description = "產業名稱，不填或 All Industries 代表全部") @RequestParam(required = false) String industry,
            @Parameter(description = "使用者類型") @RequestParam(required = false) com.casemgr.enumtype.UserType userType,
            @Parameter(description = "頁碼，從 0 起算") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁筆數") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "排序欄位") @RequestParam(defaultValue = "displayOrder") String sortBy,
            @Parameter(description = "排序方向 asc/desc") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "關鍵字（username/email/title）") @RequestParam(required = false) String searchKeyword
    ) {
        UserSearchRequest req = new UserSearchRequest();
        req.setIndustry(industry == null ? "All Industries" : industry);
        req.setUserType(userType);
        req.setPage(page);
        req.setSize(size);
        req.setSortBy(sortBy);
        req.setSortDir(sortDir);
        req.setSearchKeyword(searchKeyword);
        UserSearchResponse resp = adminUserService.searchUsers(req);
        return ResponseEntity.ok(resp);
    }

    /**
     * 取得使用者統計
     */
    @GetMapping("/statistics")
    @Operation(summary = "User Statistics", description = "Get user statistics by filters")
    public ResponseEntity<UserStatistics> statistics(
            @Parameter(description = "產業名稱") @RequestParam(required = false) String industry,
            @Parameter(description = "使用者類型") @RequestParam(required = false) com.casemgr.enumtype.UserType userType
    ) {
        UserStatisticsRequest req = new UserStatisticsRequest();
        req.setIndustry(industry);
        req.setUserType(userType);
        UserStatistics stats = adminUserService.getUserStatistics(req);
        return ResponseEntity.ok(stats);
    }
}

