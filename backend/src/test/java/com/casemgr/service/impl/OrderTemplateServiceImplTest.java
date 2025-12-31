package com.casemgr.service.impl;

import com.casemgr.converter.BlockConverter;
import com.casemgr.converter.DiscountConverter;
import com.casemgr.converter.OrderTemplateConverter;
import com.casemgr.entity.*;
import com.casemgr.enumtype.BlockType;
import com.casemgr.enumtype.PaymentMethod;
import com.casemgr.repository.*;
import com.casemgr.request.*;
import com.casemgr.response.*;
import com.casemgr.service.FileStorageService;
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
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.mockito.MockedStatic;

/**
 * OrderTemplateService 測試類
 * 測試訂單範本管理服務的業務邏輯
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderTemplateService 測試")
class OrderTemplateServiceImplTest {

    @Mock
    private OrderTemplateRepository orderTemplateRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private BlockRepository blockRepository;
    
    @Mock
    private DiscountRepository discountRepository;
    
    @Mock
    private ContractRepository contractRepository;
    
    @Mock
    private IndustryRepository industryRepository;
    
    @Mock
    private OrderTemplateConverter orderTemplateConverter;
    
    @Mock
    private BlockConverter blockConverter;
    
    @Mock
    private DiscountConverter discountConverter;
    
    @Mock
    private FileStorageService fileStorageService;
    
    private Authentication authentication;
    
    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private OrderTemplateServiceImpl orderTemplateService;

    private OrderTemplate testOrderTemplate;
    private User testUser;
    private OrderTemplateResponse testOrderTemplateResponse;
    private Block testBlock;
    private Discount testDiscount;

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

        // 創建測試訂單模板
        testOrderTemplate = new OrderTemplate();
        testOrderTemplate.setOtId(1L);
        testOrderTemplate.setName("測試訂單模板");
        testOrderTemplate.setImageUrl("test-image.jpg");
        testOrderTemplate.setStartingPrice(100);
        testOrderTemplate.setBusinessDays(5);
        testOrderTemplate.setProvider(testUser);

        // 創建測試響應
        testOrderTemplateResponse = new OrderTemplateResponse();
        testOrderTemplateResponse.setOtId(1L);
        testOrderTemplateResponse.setName("測試訂單模板");
        testOrderTemplateResponse.setImageUrl("test-image.jpg");
        testOrderTemplateResponse.setStartingPrice(100);
        testOrderTemplateResponse.setBusinessDays(5);

        // 創建測試區塊
        testBlock = new Block();
        testBlock.setBId(1L);
        testBlock.setName("測試區塊");
        testBlock.setContext("測試區塊內容");
        testBlock.setType(BlockType.text);
        testBlock.setOrderTemplate(testOrderTemplate);

        // 創建測試折扣
        testDiscount = new Discount();
        testDiscount.setDId(1L);
        testDiscount.setSpend(500L);
        testDiscount.setDiscount(10L);
        testDiscount.setOrderTemplate(testOrderTemplate);
    }

    @Test
    @DisplayName("應該能夠創建訂單模板")
    void shouldCreateTemplate() {
        // Given
        String templateName = "新的訂單模板";
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
            
            when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
            when(orderTemplateRepository.save(any(OrderTemplate.class))).thenReturn(testOrderTemplate);
            when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

            // When
            OrderTemplateResponse result = orderTemplateService.createTemplate(templateName);

            // Then
            assertNotNull(result);
            assertEquals("測試訂單模板", result.getName());
            verify(orderTemplateRepository, times(1)).save(any(OrderTemplate.class));
            verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
        }
    }

    @Test
    @DisplayName("應該能夠更新模板名稱")
    void shouldUpdateTemplateName() {
        // Given
        Long templateId = 1L;
        String newName = "更新後的模板名稱";
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.of(testOrderTemplate));
        when(orderTemplateRepository.save(any(OrderTemplate.class))).thenReturn(testOrderTemplate);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        OrderTemplateResponse result = orderTemplateService.updateTemplateName(templateId, newName);

        // Then
        assertNotNull(result);
        assertEquals(newName, testOrderTemplate.getName());
        verify(orderTemplateRepository, times(1)).save(testOrderTemplate);
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("當模板不存在時，更新名稱應該拋出異常")
    void shouldThrowExceptionWhenTemplateNotFoundForNameUpdate() {
        // Given
        Long templateId = 999L;
        String newName = "新名稱";
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderTemplateService.updateTemplateName(templateId, newName);
        });

        assertTrue(exception.getMessage().contains("OrderTemplate not found"));
        verify(orderTemplateRepository, never()).save(any(OrderTemplate.class));
    }

    @Test
    @DisplayName("應該能夠上傳模板圖片（使用文件URL）")
    void shouldUploadTemplateImageWithUrl() {
        // Given
        Long templateId = 1L;
        String fileUrl = "https://example.com/image.jpg";
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.of(testOrderTemplate));
        when(orderTemplateRepository.save(any(OrderTemplate.class))).thenReturn(testOrderTemplate);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        OrderTemplateResponse result = orderTemplateService.uploadTemplateImage(templateId, fileUrl);

        // Then
        assertNotNull(result);
        assertEquals(fileUrl, testOrderTemplate.getImageUrl());
        verify(orderTemplateRepository, times(1)).save(testOrderTemplate);
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("應該能夠上傳模板圖片（使用MultipartFile）")
    void shouldUploadTemplateImageWithFile() throws Exception {
        // Given
        Long templateId = 1L;
        String uploadedFileUrl = "uploaded-image.jpg";
        FiledataResponse fileResponse = new FiledataResponse();
        fileResponse.setFileUrl(uploadedFileUrl);
        
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.of(testOrderTemplate));
        when(fileStorageService.storeFile(multipartFile)).thenReturn(fileResponse);
        when(orderTemplateRepository.save(any(OrderTemplate.class))).thenReturn(testOrderTemplate);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        OrderTemplateResponse result = orderTemplateService.uploadTemplateImage(templateId, multipartFile);

        // Then
        assertNotNull(result);
        assertEquals(uploadedFileUrl, testOrderTemplate.getImageUrl());
        verify(fileStorageService, times(1)).storeFile(multipartFile);
        verify(orderTemplateRepository, times(1)).save(testOrderTemplate);
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("應該能夠更新模板付款方式")
    void shouldUpdateTemplatePayment() {
        // Given
        Long templateId = 1L;
        OrderTemplateUpdatePaymentRequest paymentRequest = new OrderTemplateUpdatePaymentRequest();
        paymentRequest.setPaymentMethods(Arrays.asList(PaymentMethod.FullPayment, PaymentMethod.Installment2_1));
        
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.of(testOrderTemplate));
        when(orderTemplateRepository.save(any(OrderTemplate.class))).thenReturn(testOrderTemplate);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        OrderTemplateResponse result = orderTemplateService.updateTemplatePayment(templateId, paymentRequest);

        // Then
        assertNotNull(result);
        assertEquals(paymentRequest.getPaymentMethods(), testOrderTemplate.getPaymentMethods());
        verify(orderTemplateRepository, times(1)).save(testOrderTemplate);
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("應該能夠更新模板起始價格")
    void shouldUpdateTemplateStartingPrice() {
        // Given
        Long templateId = 1L;
        OrderTemplateUpdateStartingPriceRequest priceRequest = new OrderTemplateUpdateStartingPriceRequest();
        priceRequest.setStartingPrice(200);
        
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.of(testOrderTemplate));
        when(orderTemplateRepository.save(any(OrderTemplate.class))).thenReturn(testOrderTemplate);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        OrderTemplateResponse result = orderTemplateService.updateTemplateStartingPrice(templateId, priceRequest);

        // Then
        assertNotNull(result);
        assertEquals(200, testOrderTemplate.getStartingPrice());
        verify(orderTemplateRepository, times(1)).save(testOrderTemplate);
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("應該能夠添加模板折扣")
    void shouldAddTemplateDiscount() {
        // Given
        Long templateId = 1L;
        DiscountRequest discountRequest = new DiscountRequest();
        discountRequest.setSpend(1000L);
        discountRequest.setDiscount(15L);
        discountRequest.setDiscountType(com.casemgr.enumtype.DiscountType.Percentage);
        
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.of(testOrderTemplate));
        when(discountRepository.save(any(Discount.class))).thenReturn(testDiscount);
        when(orderTemplateRepository.save(any(OrderTemplate.class))).thenReturn(testOrderTemplate);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        OrderTemplateResponse result = orderTemplateService.addTemplateDiscount(templateId, discountRequest);

        // Then
        assertNotNull(result);
        verify(discountRepository, times(1)).save(any(Discount.class));
        verify(orderTemplateRepository, times(1)).save(testOrderTemplate);
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("應該能夠更新模板折扣")
    void shouldUpdateTemplateDiscount() {
        // Given
        Long templateId = 1L;
        Long discountId = 1L;
        DiscountRequest discountRequest = new DiscountRequest();
        discountRequest.setSpend(1500L);
        discountRequest.setDiscount(20L);
        discountRequest.setDiscountType(com.casemgr.enumtype.DiscountType.Fixed);
        
        DiscountResponse discountResponse = new DiscountResponse();
        discountResponse.setDId(1L);
        discountResponse.setSpend(1500L);
        discountResponse.setDiscount(20L);
        
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.of(testOrderTemplate));
        when(discountRepository.findById(discountId)).thenReturn(Optional.of(testDiscount));
        when(discountRepository.save(any(Discount.class))).thenReturn(testDiscount);
        when(discountConverter.entityToResponse(testDiscount)).thenReturn(discountResponse);

        // When
        DiscountResponse result = orderTemplateService.updateTemplateDiscount(templateId, discountId, discountRequest);

        // Then
        assertNotNull(result);
        assertEquals(1500L, testDiscount.getSpend());
        assertEquals(20L, testDiscount.getDiscount());
        verify(discountRepository, times(1)).save(testDiscount);
        verify(discountConverter, times(1)).entityToResponse(testDiscount);
    }

    @Test
    @DisplayName("應該能夠添加模板輸入區塊")
    void shouldAddTemplateInput() {
        // Given
        Long templateId = 1L;
        InputBlockRequest inputRequest = new InputBlockRequest();
        inputRequest.setName("測試輸入區塊");
        inputRequest.setContext("測試描述");
        inputRequest.setSort(1);
        
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.of(testOrderTemplate));
        when(blockRepository.save(any(Block.class))).thenReturn(testBlock);
        when(orderTemplateRepository.save(any(OrderTemplate.class))).thenReturn(testOrderTemplate);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        OrderTemplateResponse result = orderTemplateService.addTemplateInput(templateId, inputRequest);

        // Then
        assertNotNull(result);
        verify(blockRepository, times(1)).save(any(Block.class));
        verify(orderTemplateRepository, times(1)).save(testOrderTemplate);
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("應該能夠更新模板輸入區塊")
    void shouldUpdateTemplateInput() {
        // Given
        Long blockId = 1L;
        InputBlockRequest inputRequest = new InputBlockRequest();
        inputRequest.setName("更新後的輸入區塊");
        inputRequest.setContext("更新後的描述");
        inputRequest.setSort(2);
        
        BlockResponse blockResponse = new BlockResponse();
        blockResponse.setBId(1L);
        blockResponse.setName("更新後的輸入區塊");
        
        when(blockRepository.findById(blockId)).thenReturn(Optional.of(testBlock));
        when(blockRepository.save(any(Block.class))).thenReturn(testBlock);
        when(blockConverter.entityToResponse(testBlock)).thenReturn(blockResponse);

        // When
        BlockResponse result = orderTemplateService.updateTemplateInput(blockId, inputRequest);

        // Then
        assertNotNull(result);
        assertEquals("更新後的輸入區塊", testBlock.getName());
        assertEquals("更新後的描述", testBlock.getContext());
        verify(blockRepository, times(1)).save(testBlock);
        verify(blockConverter, times(1)).entityToResponse(testBlock);
    }

    @Test
    @DisplayName("應該能夠刪除模板區塊項目")
    void shouldDeleteTemplateBlockItem() {
        // Given
        Long blockId = 1L;
        when(blockRepository.findById(blockId)).thenReturn(Optional.of(testBlock));
        when(orderTemplateRepository.save(any(OrderTemplate.class))).thenReturn(testOrderTemplate);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        OrderTemplateResponse result = orderTemplateService.deleteTemplateBlockItem(blockId);

        // Then
        assertNotNull(result);
        verify(blockRepository, times(1)).delete(testBlock);
        verify(orderTemplateRepository, times(1)).save(testOrderTemplate);
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("當區塊不存在時，刪除應該拋出異常")
    void shouldThrowExceptionWhenBlockNotFoundForDelete() {
        // Given
        Long blockId = 999L;
        when(blockRepository.findById(blockId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderTemplateService.deleteTemplateBlockItem(blockId);
        });

        assertTrue(exception.getMessage().contains("Block not found"));
        verify(blockRepository, never()).delete(any(Block.class));
    }

    @Test
    @DisplayName("應該能夠獲取模板詳情")
    void shouldGetTemplateDetail() {
        // Given
        Long templateId = 1L;
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.of(testOrderTemplate));
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        OrderTemplateResponse result = orderTemplateService.templateDetail(templateId);

        // Then
        assertNotNull(result);
        assertEquals("測試訂單模板", result.getName());
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("當模板不存在時，獲取詳情應該拋出異常")
    void shouldThrowExceptionWhenTemplateNotFoundForDetail() {
        // Given
        Long templateId = 999L;
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderTemplateService.templateDetail(templateId);
        });

        assertTrue(exception.getMessage().contains("OrderTemplate not found"));
    }

    @Test
    @DisplayName("應該能夠列出所有模板")
    void shouldListAllTemplates() {
        // Given
        List<OrderTemplate> templates = Arrays.asList(testOrderTemplate);
        List<OrderTemplateResponse> responses = Arrays.asList(testOrderTemplateResponse);
        
        when(orderTemplateRepository.findAll()).thenReturn(templates);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        List<OrderTemplateResponse> result = orderTemplateService.list();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("測試訂單模板", result.get(0).getName());
        verify(orderTemplateRepository, times(1)).findAll();
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("應該能夠列出我的模板")
    void shouldListMyTemplates() {
        // Given
        List<OrderTemplate> myTemplates = Arrays.asList(testOrderTemplate);
        
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
            
            when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
            when(orderTemplateRepository.findByProviderOrderByUpdateTimeDesc(testUser)).thenReturn(myTemplates);
            when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

            // When
            List<OrderTemplateResponse> result = orderTemplateService.listMyTemplate();

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("測試訂單模板", result.get(0).getName());
            verify(orderTemplateRepository, times(1)).findByProviderOrderByUpdateTimeDesc(testUser);
            verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
        }
    }

    @Test
    @DisplayName("應該能夠根據名稱查找模板")
    void shouldFindTemplateByName() {
        // Given
        String templateName = "測試";
        List<OrderTemplate> templates = Arrays.asList(testOrderTemplate);
        
        when(orderTemplateRepository.findByNameContainingIgnoreCase(templateName)).thenReturn(templates);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        List<OrderTemplateResponse> result = orderTemplateService.findByName(templateName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("測試訂單模板", result.get(0).getName());
        verify(orderTemplateRepository, times(1)).findByNameContainingIgnoreCase(templateName);
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("應該能夠刪除訂單模板")
    void shouldDeleteOrderTemplate() {
        // Given
        Long templateId = 1L;
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.of(testOrderTemplate));

        // When
        assertDoesNotThrow(() -> {
            orderTemplateService.deleteOrderTemplate(templateId);
        });

        // Then
        verify(orderTemplateRepository, times(1)).delete(testOrderTemplate);
    }

    @Test
    @DisplayName("當模板不存在時，刪除應該拋出異常")
    void shouldThrowExceptionWhenTemplateNotFoundForDelete() {
        // Given
        Long templateId = 999L;
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderTemplateService.deleteOrderTemplate(templateId);
        });

        assertTrue(exception.getMessage().contains("OrderTemplate not found"));
        verify(orderTemplateRepository, never()).delete(any(OrderTemplate.class));
    }

    @Test
    @DisplayName("應該能夠設置跳過合約選項")
    void shouldSetAllowSkipContract() {
        // Given
        Long templateId = 1L;
        Boolean allowSkip = true;
        when(orderTemplateRepository.findById(templateId)).thenReturn(Optional.of(testOrderTemplate));

        // When
        assertDoesNotThrow(() -> {
            orderTemplateService.setAllowSkipContract(templateId, allowSkip);
        });

        // Then
        verify(orderTemplateRepository, times(1)).save(testOrderTemplate);
    }
}