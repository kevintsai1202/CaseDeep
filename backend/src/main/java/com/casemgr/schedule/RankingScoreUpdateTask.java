package com.casemgr.schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.casemgr.entity.User;
import com.casemgr.repository.UserRepository;
import com.casemgr.response.UserRankingResponse;
import com.casemgr.service.RankingService;

@Component
public class RankingScoreUpdateTask {
    
    private static final Logger logger = LoggerFactory.getLogger(RankingScoreUpdateTask.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RankingService rankingService;
    
    @Scheduled(cron = "0 0 8,14,20 * * ?") // 每天上午8點、下午2點、晚上8點執行
    public void updateRankingScores() {
        logger.info("開始執行 RR 分數更新任務，時間: {}", LocalDateTime.now());
        
        try {
            // 查詢所有用戶，可考慮分批處理
            List<User> users = userRepository.findAll();
            int totalUsers = users.size();
            logger.info("查詢到 {} 個用戶需要更新 RR 分數", totalUsers);
            
            int updatedCount = 0;
            for (User user : users) {
                try {
                    // 計算用戶的 RR 分數
                    UserRankingResponse rankingResponse = rankingService.calculateRankingScore(user.getUId());
                    Double newScore = rankingResponse.getRankingScore();
                    
                    // 更新用戶的 rankingScore 和 lastUpdated 欄位
                    user.setRankingScore(newScore);
                    userRepository.save(user);
                    
                    updatedCount++;
                    if (updatedCount % 100 == 0) {
                        logger.info("已更新 {} / {} 個用戶的 RR 分數", updatedCount, totalUsers);
                    }
                } catch (Exception e) {
                    logger.error("更新用戶 {} 的 RR 分數時發生錯誤: {}", user.getUId(), e.getMessage());
                }
            }
            
            logger.info("RR 分數更新任務完成，共更新 {} / {} 個用戶，時間: {}", 
                updatedCount, totalUsers, LocalDateTime.now());
        } catch (Exception e) {
            logger.error("RR 分數更新任務執行失敗: {}", e.getMessage(), e);
        }
    }
}