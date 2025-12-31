package com.casemgr.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.casemgr.config.RankingWeightConfig;
import com.casemgr.entity.Message;
import com.casemgr.entity.Order;
import com.casemgr.entity.User;
import com.casemgr.enumtype.OrderStatus;
import com.casemgr.repository.ChatMessageRepository;
import com.casemgr.repository.OrderRepository;
import com.casemgr.repository.UserRepository;
import com.casemgr.response.PaginatedUserRankingResponse;
import com.casemgr.response.UserRankingResponse;
import com.casemgr.service.RankingService;
import com.casemgr.specification.UserSpecification;

@Service
public class RankingServiceImpl implements RankingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private RankingWeightConfig weightConfig;

    /**
     * 計算單一用戶的RR分數，包含進行中訂單懲罰機制
     */
    @Override
    public UserRankingResponse calculateRankingScore(Long userId) {
        // 取得用戶
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new UserRankingResponse(userId, "Unknown User", 0.0, 0);
        }

        // 取得各項指標分數 S1~S6
        double S1 = calculateLivechatConversionRate(userId); // 即時聊天轉換率（標準化後）
        double S2 = calculateRating(userId); // 評分（標準化後）
        double S3 = calculateCommissionRate(userId); // 佣金率（標準化後）
        double S4 = calculateOrderCompletionRate(userId); // 訂單完成率（標準化後）
        double S5 = calculateRepeatOrderRate(userId); // 重複訂單率（標準化後）
        double S6 = calculateAverageResponseScore(userId); // 平均回應時間（標準化後）

        // 使用配置的權重計算基礎RR分數
        double rrScore = (weightConfig.getS1() * S1) + (weightConfig.getS2() * S2) + (weightConfig.getS3() * S3)
                + (weightConfig.getS4() * S4) + (weightConfig.getS5() * S5) + (weightConfig.getS6() * S6);

        // 進行中訂單懲罰機制：每筆進行中訂單扣4%，最高20%
        List<Order> ongoingOrders = orderRepository.findAllByProviderAndStatusIn(user, List.of(OrderStatus.in_progress, OrderStatus.in_revision, OrderStatus.delivered));
        int ongoingCount = ongoingOrders.size();
        double penaltyRate = Math.min(ongoingCount * 0.04, 0.20);
        rrScore = rrScore * (1 - penaltyRate);

        
        List<Order> completedOrders = orderRepository.findAllByProviderAndStatusIn(user, List.of(OrderStatus.completed));
        return new UserRankingResponse(userId, user.getUsername(), rrScore, completedOrders.size());
    }

    /**
     * 計算用戶排名列表，支援行業、子行業、位置篩選，RR分數降序排序，分頁
     */
    @Override
    public PaginatedUserRankingResponse calculateRankingList(Long industryId, Long subIndustryId, String location, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rankingScore"));

        // 使用Specification動態查詢用戶，並依 rankingScore 降序排序
        Page<User> userPage = userRepository.findAll(
                UserSpecification.filterByIndustrySubIndustryLocation(industryId, subIndustryId, location),
                pageable);

        List<UserRankingResponse> rankingResponses = new ArrayList<>();

        // 直接使用已儲存的 RR 分數
        for (User user : userPage.getContent()) {
        	List<Order> completedOrders = orderRepository.findAllByProviderAndStatusIn(user, List.of(OrderStatus.completed));
            rankingResponses.add(new UserRankingResponse(user.getUId(), user.getUsername(), user.getRankingScore() != null ? user.getRankingScore() : 0.0, completedOrders.size()));
        }

        int totalPages = userPage.getTotalPages();
        long totalItems = userPage.getTotalElements();

        return new PaginatedUserRankingResponse(rankingResponses, page, totalPages, totalItems);
    }
    
    
    /**
     * 管理員手動更新用戶的排名分數(RR)
     */
    @Override
    public UserRankingResponse updateUserRankingScore(Long userId, Double newScore) {
        // 取得用戶
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new UserRankingResponse(userId, "Unknown User", 0.0,0);
        }

        // 更新用戶的 rankingScore 欄位
        user.setRankingScore(newScore);
        userRepository.save(user);
        List<Order> completedOrders = orderRepository.findAllByProviderAndStatusIn(user, List.of(OrderStatus.completed));
        return new UserRankingResponse(userId, user.getUsername(), newScore, completedOrders.size());
    }

    private double calculateAverageResponseScore(Long userId) {
        String userIdStr = userId.toString();
        List<Message> messages = chatMessageRepository.findAll().stream()
                .filter(m -> userIdStr.equals(m.getSenderId()) || userIdStr.equals(m.getReceiverId()))
                .sorted((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                .toList();

        if (messages.size() < 2) {
            return 1.0;
        }

        long totalResponseTime = 0;
        int responseCount = 0;

        for (int i = 1; i < messages.size(); i++) {
            Message prev = messages.get(i - 1);
            Message curr = messages.get(i);
            long diff = curr.getTimestamp().getTime() - prev.getTimestamp().getTime();
            if (diff > 0) {
                totalResponseTime += diff;
                responseCount++;
            }
        }

        if (responseCount == 0) {
            return 1.0;
        }

        double avgResponseTime = (double) totalResponseTime / responseCount;

        // 標準化平均回應時間，假設最大合理回應時間為1小時(3600000毫秒)
        double normalizedScore = 1.0 - Math.min(avgResponseTime / 3600000.0, 1.0);

        return normalizedScore;
    }

    private double calculateOrderCompletionRate(Long userId) {
    	User provider = userRepository.findById(userId).orElse(null);
    	long totalOrders = orderRepository.countByProvider(provider);
        if (totalOrders == 0) {
            return 1.0;
        }
        long completedOrders = orderRepository.countByProviderAndStatusIn(provider, List.of(OrderStatus.completed));
        return (double) completedOrders / totalOrders;
    }

    private double calculateRating(Long userId) {
        // 這裡假設有某種方法來計算評分
        // 實際上需要根據業務邏輯來實現
        double rawRating = 4.5; // 假設值，需替換為實際計算邏輯
        // 標準化到 0-100 範圍（假設評分範圍為 0-5）
        return Math.min(Math.max(rawRating * 20, 0), 100);
    }

    private double calculateCommissionRate(Long userId) {
        // 這裡假設有某種方法來計算佣金率
        // 實際上需要根據業務邏輯來實現
        double rawCommissionRate = 0.7; // 假設值，需替換為實際計算邏輯
        // 標準化到 0-100 範圍
        return Math.min(Math.max(rawCommissionRate * 100, 0), 100);
    }
    
    
    private double calculateLivechatConversionRate(Long userId) {
        // 這裡假設有某種方法來計算即時聊天轉換率
        // 實際上需要根據業務邏輯來實現
        double rawConversionRate = 0.8; // 假設值，需替換為實際計算邏輯
        // 標準化到 0-100 範圍
        return Math.min(Math.max(rawConversionRate * 100, 0), 100);
    }

    private double calculateRepeatOrderRate(Long userId) {
        java.time.LocalDateTime ninetyDaysAgo = java.time.LocalDateTime.now().minusDays(90);
        long totalUniqueClients = orderRepository.countUniqueClientsByUserIdAndDateAfter(userId, ninetyDaysAgo);
        if (totalUniqueClients == 0) {
            return 1.0;
        }
        long repeatClients = orderRepository.countRepeatClientsByUserIdAndDateAfter(userId, ninetyDaysAgo);
        double repeatRate = (double) repeatClients / totalUniqueClients;
        // 標準化到 0-100 範圍
        return Math.min(Math.max(repeatRate * 100, 0), 100);
    }
}