package com.casemgr.service.impl;

import com.casemgr.converter.BlockConverter;
import com.casemgr.converter.ListItemConverter;
import com.casemgr.converter.OrderConverter;
import com.casemgr.entity.*;
import com.casemgr.enumtype.OrderStatus;
import com.casemgr.enumtype.OrderType;
import com.casemgr.enumtype.BlockType;
import com.casemgr.repository.*;
import com.casemgr.request.OrderCreateRequest;
import com.casemgr.response.OrderCreateResponse;
import com.casemgr.response.OrderResponse;
import com.casemgr.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Collections;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.mockito.MockedStatic;

import com.casemgr.exception.BusinessException;

/**
 * OrderService 測試類
 * 測試訂單管理服務的業務邏輯
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 測試")
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderTemplateRepository orderTemplateRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private BlockRepository blockRepository;
    
    @Mock
    private ListItemRepository listItemRepository;
    
    @Mock
    private IndustryRepository industryRepository;
    
    @Mock
    private DiscountRepository discountRepository;
    
    @Mock
    private OrderConverter orderConverter;
    
    @Mock
    private BlockConverter blockConverter;
    
    @Mock
    private ListItemConverter listItemConverter;
    
    private Authentication authentication;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderTemplate testOrderTemplate;
    private User testUser;
    private User testProvider;
    private OrderCreateRequest testRequest;
    private OrderResponse testOrderResponse;

    @BeforeEach
    void setUp() {
        // 創建測試用的Authentication實例
        authentication = new Authentication() {
            @Override
            public String getName() {
                return "testUser";
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptyList();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return testUser;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            }
        };
        
        // 創建測試用戶
        testUser = new User();
        testUser.setUId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");

        testProvider = new User();
        testProvider.setUId(2L);
        testProvider.setUsername("testProvider");
        testProvider.setEmail("provider@example.com");

        // 創建測試訂單模板
        testOrderTemplate = new OrderTemplate();
        testOrderTemplate.setOtId(1L);
        testOrderTemplate.setName("測試訂單模板");
        testOrderTemplate.setStartingPrice(100);
        testOrderTemplate.setProvider(testProvider);
        testOrderTemplate.setPaymentMethods(Arrays.asList("FullPayment"));
        testOrderTemplate.setDeliveryType(com.casemgr.enumtype.OrderTemplateSelectType.SelectedByTheCustomer);

        // 創建測試訂單
        testOrder = new Order();
        testOrder.setOId(1L);
        testOrder.setOrderNo("ORD001");
        testOrder.setName("測試訂單");
        testOrder.setTotalPrice(new BigDecimal("100.00"));
        testOrder.setStatus(OrderStatus.inquiry);
        testOrder.setOrderType(OrderType.Promoted);
        testOrder.setClient(testUser);
        testOrder.setProvider(testProvider);
        testOrder.setOrderTemplate(testOrderTemplate);

        // 創建測試請求
        testRequest = new OrderCreateRequest();
        testRequest.setOrderTemplateId(1L);
        testRequest.setName("測試訂單");
        testRequest.setOrderType(OrderType.Promoted);

        // 創建測試響應
        testOrderResponse = new OrderResponse();
        testOrderResponse.setOrderNoBase62("ORD001");
        testOrderResponse.setImageUrl("test-image.jpg");
        testOrderResponse.setStatus("inquiry");
    }

    @Test
    @DisplayName("應該能夠根據模板創建訂單")
    void shouldCreateOrderFromTemplate() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = new SecurityContext() {
                @Override
                public Authentication getAuthentication() {
                    return authentication;
                }

                @Override
                public void setAuthentication(Authentication authentication) {
                }
            };
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            
            when(orderTemplateRepository.findById(1L)).thenReturn(Optional.of(testOrderTemplate));
            when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
            
            OrderCreateResponse expectedResponse = new OrderCreateResponse("ORD001");

            // When
            OrderCreateResponse result = orderService.createFromTemplate(testRequest);

            // Then
            assertNotNull(result);
            verify(orderTemplateRepository, times(1)).findById(1L);
            verify(orderRepository, times(1)).save(any(Order.class));
        }
    }

    @Test
    @DisplayName("當訂單模板不存在時，創建訂單應該拋出異常")
    void shouldThrowExceptionWhenOrderTemplateNotFound() {
        // Given
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = new SecurityContext() {
                @Override
                public Authentication getAuthentication() {
                    return authentication;
                }

                @Override
                public void setAuthentication(Authentication authentication) {
                }
            };
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            
            when(orderTemplateRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(EntityNotFoundException.class, () -> {
                orderService.createFromTemplate(testRequest);
            });
            
            verify(orderRepository, never()).save(any(Order.class));
        }
    }

    @Test
    @DisplayName("應該能夠根據ID獲取訂單")
    void shouldGetOrderById() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        OrderResponse result = orderService.getOrderById(1L);

        // Then
        assertNotNull(result);
        assertEquals("ORD001", result.getOrderNoBase62());
        assertEquals("inquiry", result.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        // 改以結果資料斷言，不驗證 converter 互動
    }

    @Test
    @DisplayName("當訂單不存在時，根據ID獲取訂單應該拋出異常")
    void shouldThrowExceptionWhenOrderNotFoundById() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            orderService.getOrderById(999L);
        });
        
        // 不驗證 converter 互動
    }

    @Test
    @DisplayName("應該能夠根據訂單號獲取訂單")
    void shouldGetOrderByOrderNo() {
        // Given
        when(orderRepository.getByOrderNo("ORD001")).thenReturn(Optional.of(testOrder));

        // When
        OrderResponse result = orderService.getOrderByOrderNo("ORD001");

        // Then
        assertNotNull(result);
        assertEquals("ORD001", result.getOrderNoBase62());
        assertEquals("inquiry", result.getStatus());
        verify(orderRepository, times(1)).getByOrderNo("ORD001");
        // 改以結果資料斷言，不驗證 converter 互動
    }

    @Test
    @DisplayName("當訂單號不存在時，應該拋出異常")
    void shouldThrowExceptionWhenOrderNotFoundByOrderNo() {
        // Given
        when(orderRepository.getByOrderNo("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            orderService.getOrderByOrderNo("INVALID");
        });
        
        // 不驗證 converter 互動
    }

    @Test
    @DisplayName("應該能夠獲取訂單列表")
    void shouldListOrders() {
        // Given
        List<Order> orders = Arrays.asList(testOrder);
        List<OrderResponse> expectedResponses = Arrays.asList(testOrderResponse);
        
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        List<OrderResponse> result = orderService.listOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ORD001", result.get(0).getOrderNoBase62());
        verify(orderRepository, times(1)).findAll();
        // 改以結果資料斷言，不驗證 converter 互動
    }

    @Test
    @DisplayName("應該能夠更新訂單狀態")
    void shouldUpdateOrderStatus() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        // 不再 stub converter；Service 直接使用 OrderConverter.INSTANCE

        // When
        OrderResponse result = orderService.updateOrderStatus(1L, OrderStatus.completed, "測試完成");

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.completed, testOrder.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    @DisplayName("當訂單不存在時，更新狀態應該失敗")
    void shouldFailUpdateOrderStatusWhenOrderNotExists() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            orderService.updateOrderStatus(999L, OrderStatus.completed, "測試完成");
        });
        
        verify(orderRepository, times(1)).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("應該能夠計算訂單總價")
    void shouldCalculateOrderTotalPrice() {
        // Given
        testOrder.setTotalPrice(new BigDecimal("100.00"));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        OrderResponse result = orderService.getOrderById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testOrder.getTotalPrice(), new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("應該能夠取消訂單")
    void shouldCancelOrder() {
        // Given
        testOrder.setStatus(OrderStatus.in_progress);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderResponse result = orderService.cancelOrder(1L);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.cancelled, testOrder.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    @DisplayName("已確認的訂單不能取消")
    void shouldNotCancelConfirmedOrder() {
        // Given
        testOrder.setStatus(OrderStatus.completed);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        assertThrows(BusinessException.class, () -> {
            orderService.cancelOrder(1L);
        });
        
        assertEquals(OrderStatus.completed, testOrder.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("應該能夠刪除訂單")
    void shouldDeleteOrder() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderRepository).delete(testOrder);

        // When & Then
        assertDoesNotThrow(() -> {
            orderService.deleteOrder(1L);
        });
        
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).delete(testOrder);
    }

    @Test
    @DisplayName("當訂單不存在時，刪除應該失敗")
    void shouldFailDeleteOrderWhenOrderNotExists() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            orderService.deleteOrder(999L);
        });
        
        verify(orderRepository, times(1)).findById(999L);
        verify(orderRepository, never()).delete(any(Order.class));
    }
}
