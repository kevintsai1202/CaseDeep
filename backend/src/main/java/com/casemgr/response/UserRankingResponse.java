package com.casemgr.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRankingResponse {
    private Long userId;
    private String userName;
    private Double rankingScore;
    private Integer completedOrderCount;
}