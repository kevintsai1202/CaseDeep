package com.casemgr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.casemgr.entity.Order;
import com.casemgr.entity.User;
import com.casemgr.enumtype.OrderStatus;
import com.casemgr.response.OrderResponse;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // public List<OrderTemplate> findAllByProviderAndOrderType(User provider, OrderType orderType);
    public List<Order> findAllByProvider(User provider);
    public List<Order> findAllByClient(User client);
    public List<Order> findAllByProviderAndClient(User provider, User client);
    public List<Order> findAllByStatus(OrderStatus orderStatus);
    public List<Order> findAllByProviderAndStatusIn(User provider, List<OrderStatus> statusList);

    public Optional<Order> getByOrderNo(String orderNo);

    // Find orders where either the provider's region or the client's region matches
    Page<Order> findByProviderRegionOrClientRegion(String providerRegion, String clientRegion, Pageable pageable);

    // 新增依用戶ID和訂單狀態列表查詢訂單
//    List<Order> findByUserIdAndStatusIn(Long userId, List<String> statusList);
    long countByProvider(User provider);

    long countByProviderAndStatusIn(User provider, List<OrderStatus> statusList);
    
//    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId AND o.status = 'cancelled' AND o.cancelledDate <= o.createdDate + 2")
//    long countCancelledWithin48HoursByUserId(@Param("userId") Long userId);
    
    @Query(value ="SELECT COUNT(DISTINCT o.CLIENT_ID) FROM Order o WHERE o.PROVIDER_ID = :providerId AND o.CREATE_TIME >= :date", nativeQuery = true)
    long countUniqueClientsByUserIdAndDateAfter(@Param("providerId") Long providerId, @Param("date") java.time.LocalDateTime date);
    
    @Query(value ="SELECT COUNT(DISTINCT o.CLIENT_ID) FROM Order o WHERE o.PROVIDER_ID = :providerId AND o.CREATE_TIME >= :date AND o.CLIENT_ID IN (SELECT o2.CLIENT_ID FROM Order o2 WHERE o2.PROVIDER_ID = :providerId AND o2.CREATE_TIME >= :date GROUP BY o2.CLIENT_ID HAVING COUNT(o2) > 1)", nativeQuery = true)
    long countRepeatClientsByUserIdAndDateAfter(@Param("providerId") Long providerId, @Param("date") java.time.LocalDateTime date);
}
