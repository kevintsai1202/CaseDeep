package com.casemgr.service.impl;

import com.casemgr.converter.IndustryConverter;
import com.casemgr.converter.OrderTemplateConverter;
import com.casemgr.entity.Industry;
import com.casemgr.entity.OrderTemplate;
import com.casemgr.repository.IndustryRepository;
import com.casemgr.repository.OrderTemplateRepository;
import com.casemgr.request.IndustryRequest;
import com.casemgr.response.IndustryResponse;
import com.casemgr.response.OrderTemplateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * IndustryService 測試類
 * 測試產業管理服務的業務邏輯
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("IndustryService 測試")
class IndustryServiceImplTest {

    @Mock
    private IndustryRepository industryRepository;
    
    @Mock
    private OrderTemplateRepository orderTemplateRepository;
    
    @Mock
    private IndustryConverter industryConverter;
    
    @Mock
    private OrderTemplateConverter orderTemplateConverter;

    @InjectMocks
    private IndustryServiceImpl industryService;

    private Industry testIndustry;
    private Industry parentIndustry;
    private IndustryRequest testRequest;
    private IndustryResponse testResponse;
    private OrderTemplate testOrderTemplate;
    private OrderTemplateResponse testOrderTemplateResponse;

    @BeforeEach
    void setUp() {
        // 創建測試父產業
        parentIndustry = new Industry();
        parentIndustry.setId(1L);
        parentIndustry.setName("Information Technology");
        parentIndustry.setTitle("資訊科技");
        parentIndustry.setDescription("Information Technology Industry");
        parentIndustry.setRevenueShareRate(0.05f);

        // 創建測試產業
        testIndustry = new Industry();
        testIndustry.setId(2L);
        testIndustry.setName("Software Development");
        testIndustry.setTitle("軟體開發");
        testIndustry.setDescription("Software Development Services");
        testIndustry.setParentIndustry(parentIndustry);
        testIndustry.setRevenueShareRate(0.03f);
        testIndustry.setIcon("software-icon.png");

        // 創建測試請求
        testRequest = new IndustryRequest();
        testRequest.setName("Software Development");
        testRequest.setTitle("軟體開發");
        testRequest.setDescription("Software Development Services");
        testRequest.setParentId(1L);
        testRequest.setIcon("software-icon.png");

        // 創建測試響應
        testResponse = new IndustryResponse();
        testResponse.setId(2L);
        testResponse.setName("Software Development");
        testResponse.setTitle("軟體開發");
        testResponse.setDescription("Software Development Services");
        testResponse.setIcon("software-icon.png");

        // 創建測試訂單模板
        testOrderTemplate = new OrderTemplate();
        testOrderTemplate.setOtId(1L);
        testOrderTemplate.setName("軟體開發服務");
        testOrderTemplate.setIndustry(testIndustry);

        // 創建測試訂單模板響應
        testOrderTemplateResponse = new OrderTemplateResponse();
        testOrderTemplateResponse.setOtId(1L);
        testOrderTemplateResponse.setName("軟體開發服務");
    }

    @Test
    @DisplayName("應該能夠創建新產業")
    void shouldCreateIndustry() {
        // Given
        when(industryConverter.toEntity(testRequest)).thenReturn(testIndustry);
        when(industryRepository.findById(1L)).thenReturn(Optional.of(parentIndustry));
        when(industryRepository.save(any(Industry.class))).thenReturn(testIndustry);
        when(industryConverter.toResponse(testIndustry)).thenReturn(testResponse);

        // When
        IndustryResponse result = industryService.create(testRequest);

        // Then
        assertNotNull(result);
        assertEquals("Software Development", result.getName());
        assertEquals("軟體開發", result.getTitle());
        verify(industryRepository, times(1)).save(any(Industry.class));
        verify(industryConverter, times(1)).toResponse(testIndustry);
    }

    @Test
    @DisplayName("創建沒有父產業的根產業")
    void shouldCreateRootIndustry() {
        // Given
        testRequest.setParentId(null);
        when(industryConverter.toEntity(testRequest)).thenReturn(testIndustry);
        when(industryRepository.save(any(Industry.class))).thenReturn(testIndustry);
        when(industryConverter.toResponse(testIndustry)).thenReturn(testResponse);

        // When
        IndustryResponse result = industryService.create(testRequest);

        // Then
        assertNotNull(result);
        verify(industryRepository, times(1)).save(any(Industry.class));
        verify(industryRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("當父產業不存在時，創建產業應該拋出異常")
    void shouldThrowExceptionWhenParentIndustryNotFound() {
        // Given
        when(industryConverter.toEntity(testRequest)).thenReturn(testIndustry);
        when(industryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            industryService.create(testRequest);
        });
        
        assertTrue(exception.getMessage().contains("Parent industry not found"));
        verify(industryRepository, never()).save(any(Industry.class));
    }

    @Test
    @DisplayName("應該能夠更新產業")
    void shouldUpdateIndustry() {
        // Given
        Long industryId = 2L;
        when(industryRepository.findById(industryId)).thenReturn(Optional.of(testIndustry));
        when(industryRepository.findById(1L)).thenReturn(Optional.of(parentIndustry));
        when(industryRepository.save(any(Industry.class))).thenReturn(testIndustry);
        when(industryConverter.toResponse(testIndustry)).thenReturn(testResponse);

        // When
        IndustryResponse result = industryService.update(industryId, testRequest);

        // Then
        assertNotNull(result);
        assertEquals("Software Development", testIndustry.getName());
        assertEquals("軟體開發", testIndustry.getTitle());
        verify(industryRepository, times(1)).save(testIndustry);
        verify(industryConverter, times(1)).toResponse(testIndustry);
    }

    @Test
    @DisplayName("更新時將父產業設為null")
    void shouldUpdateIndustryWithNullParent() {
        // Given
        Long industryId = 2L;
        testRequest.setParentId(null);
        when(industryRepository.findById(industryId)).thenReturn(Optional.of(testIndustry));
        when(industryRepository.save(any(Industry.class))).thenReturn(testIndustry);
        when(industryConverter.toResponse(testIndustry)).thenReturn(testResponse);

        // When
        IndustryResponse result = industryService.update(industryId, testRequest);

        // Then
        assertNotNull(result);
        assertNull(testIndustry.getParentIndustry());
        verify(industryRepository, times(1)).save(testIndustry);
    }

    @Test
    @DisplayName("當產業不存在時，更新應該拋出異常")
    void shouldThrowExceptionWhenIndustryNotFoundForUpdate() {
        // Given
        Long industryId = 999L;
        when(industryRepository.findById(industryId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            industryService.update(industryId, testRequest);
        });
        
        assertTrue(exception.getMessage().contains("Industry not found"));
        verify(industryRepository, never()).save(any(Industry.class));
    }

    @Test
    @DisplayName("當更新時父產業不存在，應該拋出異常")
    void shouldThrowExceptionWhenParentNotFoundForUpdate() {
        // Given
        Long industryId = 2L;
        when(industryRepository.findById(industryId)).thenReturn(Optional.of(testIndustry));
        when(industryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            industryService.update(industryId, testRequest);
        });
        
        assertTrue(exception.getMessage().contains("Parent industry not found"));
        verify(industryRepository, never()).save(any(Industry.class));
    }

    @Test
    @DisplayName("應該能夠獲取產業詳情")
    void shouldGetIndustryDetail() {
        // Given
        Long industryId = 2L;
        when(industryRepository.findById(industryId)).thenReturn(Optional.of(testIndustry));
        when(industryConverter.toResponse(testIndustry)).thenReturn(testResponse);

        // When
        IndustryResponse result = industryService.detail(industryId);

        // Then
        assertNotNull(result);
        assertEquals("Software Development", result.getName());
        verify(industryConverter, times(1)).toResponse(testIndustry);
    }

    @Test
    @DisplayName("當產業不存在時，獲取詳情應該拋出異常")
    void shouldThrowExceptionWhenIndustryNotFoundForDetail() {
        // Given
        Long industryId = 999L;
        when(industryRepository.findById(industryId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            industryService.detail(industryId);
        });
        
        assertTrue(exception.getMessage().contains("Industry not found"));
    }

    @Test
    @DisplayName("應該能夠列出所有產業")
    void shouldListAllIndustries() {
        // Given
        List<Industry> industries = Arrays.asList(parentIndustry, testIndustry);
        List<IndustryResponse> responses = Arrays.asList(new IndustryResponse(), testResponse);
        
        when(industryRepository.findAll()).thenReturn(industries);
        when(industryConverter.toResponse(parentIndustry)).thenReturn(responses.get(0));
        when(industryConverter.toResponse(testIndustry)).thenReturn(responses.get(1));

        // When
        List<IndustryResponse> result = industryService.list();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(industryRepository, times(1)).findAll();
        verify(industryConverter, times(2)).toResponse(any(Industry.class));
    }

    @Test
    @DisplayName("應該能夠刪除產業")
    void shouldDeleteIndustry() {
        // Given
        Long industryId = 2L;
        when(industryRepository.findById(industryId)).thenReturn(Optional.of(testIndustry));

        // When
        assertDoesNotThrow(() -> {
            industryService.delete(industryId);
        });

        // Then
        verify(industryRepository, times(1)).delete(testIndustry);
    }

    @Test
    @DisplayName("當產業不存在時，刪除應該拋出異常")
    void shouldThrowExceptionWhenIndustryNotFoundForDelete() {
        // Given
        Long industryId = 999L;
        when(industryRepository.findById(industryId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            industryService.delete(industryId);
        });
        
        assertTrue(exception.getMessage().contains("Industry not found"));
        verify(industryRepository, never()).delete(any(Industry.class));
    }

    @Test
    @DisplayName("應該能夠根據名稱查找產業")
    void shouldFindIndustryByName() {
        // Given
        String industryName = "Software Development";
        when(industryRepository.findByName(industryName)).thenReturn(testIndustry);
        when(industryConverter.toResponse(testIndustry)).thenReturn(testResponse);

        // When
        IndustryResponse result = industryService.findByName(industryName);

        // Then
        assertNotNull(result);
        assertEquals(industryName, result.getName());
        verify(industryRepository, times(1)).findByName(industryName);
        verify(industryConverter, times(1)).toResponse(testIndustry);
    }

    @Test
    @DisplayName("當根據名稱查找的產業不存在時，應該拋出異常")
    void shouldThrowExceptionWhenIndustryNotFoundByName() {
        // Given
        String industryName = "Nonexistent Industry";
        when(industryRepository.findByName(industryName)).thenReturn(null);

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            industryService.findByName(industryName);
        });
        
        assertTrue(exception.getMessage().contains("Industry not found with name"));
    }

    @Test
    @DisplayName("應該能夠根據父產業獲取子產業")
    void shouldGetSubIndustries() {
        // Given
        String parentName = "Information Technology";
        List<Industry> subIndustries = Arrays.asList(testIndustry);
        List<IndustryResponse> responses = Arrays.asList(testResponse);
        
        when(industryRepository.findByName(parentName)).thenReturn(parentIndustry);
        when(industryRepository.findByParentIndustryIdAndEnabledTrue(parentIndustry.getId())).thenReturn(subIndustries);
        when(industryConverter.toResponse(testIndustry)).thenReturn(testResponse);

        // When
        List<IndustryResponse> result = industryService.getSubIndustries(parentName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Software Development", result.get(0).getName());
        verify(industryRepository, times(1)).findByParentIndustryIdAndEnabledTrue(parentIndustry.getId());
        verify(industryConverter, times(1)).toResponse(testIndustry);
    }

    @Test
    @DisplayName("當父產業不存在時，獲取子產業應該拋出異常")
    void shouldThrowExceptionWhenParentIndustryNotFoundForSubIndustries() {
        // Given
        String parentName = "Nonexistent Industry";
        when(industryRepository.findByName(parentName)).thenReturn(null);
        when(industryRepository.findByNameOrTitle(parentName, parentName)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            industryService.getSubIndustries(parentName);
        });
        
        assertTrue(exception.getMessage().contains("找不到父產業"));
    }

    @Test
    @DisplayName("應該能夠列出所有父產業")
    void shouldListParentIndustries() {
        // Given
        List<Industry> parentIndustries = Arrays.asList(parentIndustry);
        List<IndustryResponse> responses = Arrays.asList(new IndustryResponse());
        
        when(industryRepository.findByParentIndustryIsNullAndEnabledTrueOrderByName()).thenReturn(parentIndustries);
        when(industryConverter.toResponse(parentIndustry)).thenReturn(responses.get(0));

        // When
        List<IndustryResponse> result = industryService.listParentIndustries();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(industryRepository, times(1)).findByParentIndustryIsNullAndEnabledTrueOrderByName();
        verify(industryConverter, times(1)).toResponse(parentIndustry);
    }

    @Test
    @DisplayName("應該能夠根據父產業名稱獲取OrderTemplate")
    void shouldGetOrderTemplatesByParentIndustry() {
        // Given
        String parentName = "Information Technology";
        String region = "Asia";
        int page = 0;
        int size = 10;
        
        List<Long> industryIds = Arrays.asList(1L, 2L);
        List<OrderTemplate> orderTemplates = Arrays.asList(testOrderTemplate);
        Page<OrderTemplate> orderTemplatePage = new PageImpl<>(orderTemplates);
        List<OrderTemplateResponse> responses = Arrays.asList(testOrderTemplateResponse);
        
        parentIndustry.setChildIndustries(Arrays.asList(testIndustry));
        
        when(industryRepository.findByName(parentName)).thenReturn(parentIndustry);
        when(orderTemplateRepository.findByIndustryIdInAndProviderRegionAndEnabledTrue(
            eq(industryIds), eq(region), any(PageRequest.class))).thenReturn(orderTemplatePage);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        List<OrderTemplateResponse> result = industryService.getOrderTemplatesByParentIndustry(
            parentName, region, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderTemplateRepository, times(1)).findByIndustryIdInAndProviderRegionAndEnabledTrue(
            eq(industryIds), eq(region), any(PageRequest.class));
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }

    @Test
    @DisplayName("根據父產業名稱獲取OrderTemplate時，父產業不存在應拋出異常")
    void shouldThrowExceptionWhenParentIndustryNotFoundForOrderTemplates() {
        // Given
        String parentName = "Nonexistent Industry";
        when(industryRepository.findByName(parentName)).thenReturn(null);
        when(industryRepository.findByNameOrTitle(parentName, parentName)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            industryService.getOrderTemplatesByParentIndustry(parentName, null, 0, 10);
        });
        
        assertTrue(exception.getMessage().contains("找不到產業"));
    }

    @Test
    @DisplayName("應該能夠根據產業階層獲取OrderTemplate")
    void shouldGetOrderTemplatesByIndustryHierarchy() {
        // Given
        String parentName = "Information Technology";
        String childName = "Software Development";
        String region = "Asia";
        int page = 0;
        int size = 10;
        
        List<OrderTemplate> orderTemplates = Arrays.asList(testOrderTemplate);
        Page<OrderTemplate> orderTemplatePage = new PageImpl<>(orderTemplates);
        
        parentIndustry.setChildIndustries(Arrays.asList(testIndustry));
        
        when(industryRepository.findByName(parentName)).thenReturn(parentIndustry);
        when(orderTemplateRepository.findByProviderRegionAndIndustryIdAndNameContainingAndEnabledTrue(
            eq(region), eq(testIndustry.getId()), eq(childName), any(PageRequest.class))).thenReturn(orderTemplatePage);
        when(orderTemplateConverter.entityToResponse(testOrderTemplate)).thenReturn(testOrderTemplateResponse);

        // When
        List<OrderTemplateResponse> result = industryService.getOrderTemplatesByIndustryHierarchy(
            parentName, childName, region, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderTemplateRepository, times(1)).findByProviderRegionAndIndustryIdAndNameContainingAndEnabledTrue(
            eq(region), eq(testIndustry.getId()), eq(childName), any(PageRequest.class));
        verify(orderTemplateConverter, times(1)).entityToResponse(testOrderTemplate);
    }
}