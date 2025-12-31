# 📋 移除IndustryTranslation實體執行計劃

## 🎯 項目目標
完全移除IndustryTranslation實體及所有相關的多語言功能，簡化系統架構，只保留英文版本。

## 📊 影響範圍分析

### 受影響的檔案清單

#### **1. Entity層**
- **🗑️ [`IndustryTranslation.java`](src/main/java/com/casemgr/entity/IndustryTranslation.java)** - 整個實體需要移除
- **⚠️ [`Industry.java`](src/main/java/com/casemgr/entity/Industry.java:74-75)** - 移除translations關聯字段

#### **2. Repository層**
- **🗑️ [`IndustryTranslationRepository.java`](src/main/java/com/casemgr/repository/IndustryTranslationRepository.java)** - 整個repository需要移除
- **⚠️ [`IndustryServiceImpl.java`](src/main/java/com/casemgr/service/impl/IndustryServiceImpl.java:24,43)** - 移除repository依賴

#### **3. Service層**
- **⚠️ [`IndustryService.java`](src/main/java/com/casemgr/service/IndustryService.java:63)** - 移除getAllTranslations方法
- **⚠️ [`IndustryServiceImpl.java`](src/main/java/com/casemgr/service/impl/IndustryServiceImpl.java)** - 大量翻譯相關邏輯需要重構

#### **4. Controller層**
- **⚠️ [`IndustryController.java`](src/main/java/com/casemgr/controller/IndustryController.java:125-133)** - 移除getAllTranslations endpoint

#### **5. Request/Response類**
- **🗑️ [`IndustryTranslationRequest.java`](src/main/java/com/casemgr/request/IndustryTranslationRequest.java)** - 需要移除
- **🗑️ [`IndustryTranslationResponse.java`](src/main/java/com/casemgr/response/IndustryTranslationResponse.java)** - 需要移除
- **⚠️ [`IndustryRequest.java`](src/main/java/com/casemgr/request/IndustryRequest.java:42)** - 移除translations字段
- **⚠️ [`IndustryResponse.java`](src/main/java/com/casemgr/response/IndustryResponse.java:21)** - 移除translations字段

#### **6. Converter層**
- **⚠️ [`IndustryConverter.java`](src/main/java/com/casemgr/converter/IndustryConverter.java)** - 移除所有翻譯相關轉換方法

## 🚀 執行流程圖

```mermaid
graph TD
    A[開始移除IndustryTranslation] --> B[第一階段：移除核心實體]
    B --> C[移除IndustryTranslation.java]
    B --> D[移除IndustryTranslationRepository.java]
    B --> E[移除Request/Response類]
    
    E --> F[第二階段：修改Industry實體]
    F --> G[移除Industry中的translations關聯]
    F --> H[移除@ToString exclude translations]
    
    H --> I[第三階段：重構Service層]
    I --> J[移除IndustryService中的翻譯方法]
    I --> K[重構IndustryServiceImpl查詢邏輯]
    I --> L[移除翻譯相關依賴注入]
    
    L --> M[第四階段：修改Controller]
    M --> N[移除locale參數支援]
    M --> O[移除getAllTranslations端點]
    M --> P[簡化API響應]
    
    P --> Q[第五階段：清理Converter]
    Q --> R[移除翻譯轉換方法]
    Q --> S[移除翻譯相關imports]
    
    S --> T[第六階段：測試與驗證]
    T --> U[編譯測試]
    T --> V[API功能驗證]
    T --> W[完成]
```

## 🔧 詳細執行步驟

### **第一階段：移除核心實體與Repository**

#### 1.1 完全刪除檔案
- `src/main/java/com/casemgr/entity/IndustryTranslation.java`
- `src/main/java/com/casemgr/repository/IndustryTranslationRepository.java`
- `src/main/java/com/casemgr/request/IndustryTranslationRequest.java`
- `src/main/java/com/casemgr/response/IndustryTranslationResponse.java`

### **第二階段：修改Industry實體**

#### 2.1 修改 [`Industry.java`](src/main/java/com/casemgr/entity/Industry.java)
```java
// 移除這些行 (Line 74-75)
- @OneToMany(mappedBy = "industry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
- private List<IndustryTranslation> translations = new ArrayList<>();

// 修改ToString annotation (Line 30)
- @ToString(exclude = {"contracts", "orderTemplates", "bidRequires", "translations"})
+ @ToString(exclude = {"contracts", "orderTemplates", "bidRequires"})

// 移除相關import
- import java.util.ArrayList;
```

### **第三階段：重構Service層**

#### 3.1 修改 [`IndustryService.java`](src/main/java/com/casemgr/service/IndustryService.java)
```java
// 移除方法
- List<IndustryTranslationResponse> getAllTranslations(Long iId) throws EntityNotFoundException;

// 簡化方法簽名 - 移除locale參數
- IndustryResponse detail(Long iId, String locale) throws EntityNotFoundException;
- List<IndustryResponse> list(String locale);

// 移除相關import
- import com.casemgr.response.IndustryTranslationResponse;
```

#### 3.2 重構 [`IndustryServiceImpl.java`](src/main/java/com/casemgr/service/impl/IndustryServiceImpl.java)

**移除依賴注入：**
```java
// 移除這些行 (Line 21, 24, 43)
- import com.casemgr.entity.IndustryTranslation;
- import com.casemgr.repository.IndustryTranslationRepository;
- private final IndustryTranslationRepository industryTranslationRepository;

// 移除相關imports
- import com.casemgr.request.IndustryTranslationRequest;
- import com.casemgr.response.IndustryTranslationResponse;
```

**移除/簡化方法：**
```java
// 完全移除方法 (Line 271-294)
- public List<IndustryTranslationResponse> getAllTranslations(Long id)

// 簡化create方法 (Line 66-72)
- 移除translation相關邏輯

// 簡化update方法 (Line 106-116)  
- 移除translation相關邏輯

// 簡化detail方法 (Line 153-165)
- 移除locale參數和相關邏輯

// 簡化list方法 (Line 195-213)
- 移除locale參數和相關邏輯

// 重構查詢方法
- findIndustryByNameAndLocale() -> findIndustryByName()
- isIndustryMatchByNameAndLocale() -> isIndustryMatchByName()
```

### **第四階段：修改Controller層**

#### 4.1 修改 [`IndustryController.java`](src/main/java/com/casemgr/controller/IndustryController.java)
```java
// 移除端點 (Line 125-133)
- public ResponseEntity<List<IndustryTranslationResponse>> getAllTranslations()

// 移除locale參數 from all methods
- @RequestParam(required = false) String locale

// 移除相關imports
- import com.casemgr.response.IndustryTranslationResponse;

// 更新API文檔描述
- 移除多語言相關描述
```

### **第五階段：清理Converter層**

#### 5.1 修改 [`IndustryConverter.java`](src/main/java/com/casemgr/converter/IndustryConverter.java)
```java
// 移除翻譯相關方法 (Line 27, 31, 36-53)
- IndustryTranslation toEntity(IndustryTranslationRequest translationRequest);
- IndustryTranslationResponse toTranslationResponse(IndustryTranslation translation);
- List<IndustryTranslation> mapTranslationsRequestToEntity()
- List<IndustryTranslationResponse> mapTranslationsEntityToResponse()

// 移除相關imports
- import com.casemgr.entity.IndustryTranslation;
- import com.casemgr.request.IndustryTranslationRequest;
- import com.casemgr.response.IndustryTranslationResponse;
```

### **第六階段：修改Request/Response**

#### 6.1 修改 [`IndustryRequest.java`](src/main/java/com/casemgr/request/IndustryRequest.java)
```java
// 移除字段 (Line 42)
- private List<IndustryTranslationRequest> translations = new ArrayList<>();

// 移除相關imports
- import com.casemgr.request.IndustryTranslationRequest;
- import java.util.ArrayList;
```

#### 6.2 修改 [`IndustryResponse.java`](src/main/java/com/casemgr/response/IndustryResponse.java)
```java
// 移除字段 (Line 21)
- private List<IndustryTranslationResponse> translations;

// 移除相關imports
- import com.casemgr.response.IndustryTranslationResponse;
```

## 🚨 API變更摘要

### 移除的端點
- `GET /api/industries/{id}/translations`

### 簡化的參數
- `GET /api/industries?locale=zh-TW` → `GET /api/industries`
- `GET /api/industries/{id}?locale=zh-TW` → `GET /api/industries/{id}`
- 所有多語言查詢端點的locale參數

### 保持不變的端點
- `POST /api/industries`
- `PUT /api/industries/{id}`
- `DELETE /api/industries/{id}`
- `GET /api/industries/{parentIndustry}/ordertemplates`
- `GET /api/industries/{parentIndustry}/{childIndustry}/ordertemplates`

## ⚠️ 風險評估與注意事項

### 1. 資料庫影響
- **T_INDUSTRY_TRANSLATION表**：需要確認是否有重要資料需要遷移
- **外鍵約束**：檢查是否有其他表引用IndustryTranslation
- **資料備份**：建議在執行前先備份相關資料

### 2. API向後相容性
- **前端應用**：移除locale參數可能影響前端調用
- **第三方整合**：需要通知使用API的外部系統
- **API文檔**：需要更新所有相關文檔

### 3. 業務邏輯影響
- **多語言查詢**：所有多語言查詢功能將完全失效
- **Industry資料完整性**：需要確保所有Industry實體都有完整的英文資料
- **搜尋功能**：可能影響基於翻譯的搜尋功能

## 🎯 執行優先順序

### **高優先級** (必須修改)
1. Entity和Repository層的移除
2. Service層的重構
3. Controller層的API簡化
4. Request/Response類的修改

### **中優先級** (建議修改)
1. 清理unused imports
2. 更新API文檔和註解
3. 優化查詢邏輯
4. 單元測試更新

### **低優先級** (可選)
1. 程式碼註解更新
2. 日誌訊息調整
3. 效能優化

## ✅ 測試與驗證清單

### 1. 編譯測試
- [ ] 專案能夠成功編譯
- [ ] 沒有compilation errors
- [ ] 所有imports都正確

### 2. 功能測試
- [ ] Industry CRUD操作正常
- [ ] API端點回應正確
- [ ] 資料庫操作無錯誤

### 3. 回歸測試
- [ ] 相關業務功能未受影響
- [ ] OrderTemplate查詢功能正常
- [ ] 用戶介面顯示正常

## 📝 執行記錄

### 執行前檢查清單
- [ ] 程式碼已備份
- [ ] 資料庫已備份
- [ ] 測試環境準備完成
- [ ] 相關團隊已通知

### 執行後驗證清單
- [ ] 所有測試通過
- [ ] API文檔已更新
- [ ] 部署成功
- [ ] 監控指標正常

---

**注意事項：** 此計劃建議在開發環境先完整測試後，再部署到生產環境。建議分階段執行，每個階段完成後進行測試驗證。