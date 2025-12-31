package com.casemgr.controller;

import com.casemgr.request.UpdateRankingScoreRequest;
import com.casemgr.response.PaginatedUserRankingResponse;
import com.casemgr.response.UserRankingResponse;
import com.casemgr.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Ranking", description = "User ranking and scoring management APIs for calculating and managing provider rankings based on performance metrics.")
@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    /**
     * Retrieve paginated ranking list of users filtered by industry, sub-industry, and location.
     * @param industryId Industry ID to filter rankings by
     * @param subIndustryId Optional sub-industry ID for more specific filtering
     * @param location Optional location filter for geographical ranking
     * @param page Page number for pagination (0-based)
     * @param size Number of items per page
     * @return Paginated list of user rankings
     */
    @GetMapping("/list")
    @Operation(
        summary = "Get User Ranking List",
        description = "Retrieve a paginated list of user rankings filtered by industry, sub-industry, and location. " +
                      "Rankings are calculated based on various performance metrics including ratings, completion rates, " +
                      "and other quality indicators."
    )
    public ResponseEntity<PaginatedUserRankingResponse> getRankingList(
            @Parameter(description = "Industry ID to filter rankings by", required = true)
            @RequestParam Long industryId,
            @Parameter(description = "Optional sub-industry ID for more specific filtering")
            @RequestParam(required = false) Long subIndustryId,
            @Parameter(description = "Optional location filter for geographical ranking")
            @RequestParam(required = false) String location,
            @Parameter(description = "Page number for pagination (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        PaginatedUserRankingResponse response = rankingService.calculateRankingList(industryId, subIndustryId, location, page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a user's ranking score (Admin only).
     * @param request Request containing user ID and new score
     * @return Updated user ranking information
     */
    @PostMapping("/updateScore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update User Ranking Score",
        description = "Update a user's ranking score manually. This endpoint is restricted to administrators only " +
                      "and allows direct modification of user ranking scores for administrative purposes."
    )
    public ResponseEntity<UserRankingResponse> updateScore(
            @Parameter(description = "Request containing user ID and new ranking score", required = true)
            @RequestBody UpdateRankingScoreRequest request) {
        UserRankingResponse response = rankingService.updateUserRankingScore(request.getUserId(), request.getNewScore());
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}