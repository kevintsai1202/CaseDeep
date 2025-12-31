# Service 層單元測試 & API Mock 測試計畫

## 📊 測試現況分析

### 當前測試基礎設施
- **測試框架**: JUnit 5 + Spring Boot Test + Mockito (內建於 spring-boot-starter-test)
- **現有測試**: 3個測試類別 (AdminUserInitializationTest, RoleDataInitializationTest, RoleControllerIntegrationTest)
- **測試配置**: @SpringBootTest + @ActiveProfiles("test") + @Transactional

### Service 層架構概覽
- **服務接口**: 20個 (service/)
- **服務實現**: 27個 (service/impl/)  
- **核心依賴**: Spring Data JPA, MapStruct, JWT, Bean Validation

## 🎯 階段一：Service 單元測試實施計畫

### P0 優先級 - 核心業務服務 (第1-2週)

#### 1. AuthServiceImpl
**測試重點**:
```java
// 主要測試方法
- login(AuthRequest) → TokenResponse
- register(RegisterRequest) → User
- parseToken(String) → Claims  
- isTokenExpired(String) → Boolean
- refreshToken(String) → TokenResponse
```

**Mock 依賴**:
- UserRepository
- PasswordEncoder  
- JwtUtils
- AuthenticationManager

**測試案例設計**:
```
✓ 有效登入 → 返回 JWT Token
✓ 無效憑證 → 拋出 AuthenticationException
✓ 用戶註冊 → 創建新用戶並加密密碼
✓ Token 解析 → 正確提取用戶資訊
✓ Token 過期檢查 → 正確判斷有效性
✓ SQL 注入防護 → 惡意輸入安全處理
```

#### 2. OrderServiceImpl  
**測試重點**:
```java
// 主要測試方法
- createOrder(OrderCreateRequest) → OrderCreateResponse
- updateOrderStatus(Long, OrderUpdateStatusRequest) → OrderResponse
- getOrdersByUser(Long, Pageable) → Page<OrderResponse>
- bidOnOrder(Long, BidRequest) → OrderResponse
```

**Mock 依賴**:
- OrderRepository
- UserRepository  
- OrderConverter
- ContractService
- PaymentService

#### 3. PaymentServiceImpl
**測試重點**:
```java
// 主要測試方法  
- processPayment(PaymentRequest) → PaymentResponse
- updatePaymentStatus(Long, UpdatePaymentStatusRequest) → PaymentResponse
- refundPayment(Long) → PaymentResponse
- getPaymentHistory(Long) → List<PaymentResponse>
```

#### 4. AdminUserServiceImpl
**測試重點**:
```java
// 主要測試方法
- createAdminUser(AdminUserCreateRequest) → UserResponse  
- updateAdminUser(Long, AdminUserUpdateRequest) → UserResponse
- deleteAdminUser(Long) → void
- getUsersByRole(String, Pageable) → Page<UserResponse>
```

### P1 優先級 - 重要業務服務 (第3-4週)

#### 5. OrderTemplateServiceImpl
#### 6. RevenueShareServiceImpl  
#### 7. ContractServiceImpl
#### 8. EvaluateServiceImpl

### P2 優先級 - 輔助功能服務 (第5-6週)

#### 9. FileStorageServiceImpl
#### 10. MailServiceImpl
#### 11. IndustryServiceImpl  
#### 12. CertificationServiceImpl

## 🎭 階段二：API Mock 測試實施計畫

### Controller 測試策略

#### 測試架構設計
```java
@WebMvcTest(ControllerClass.class)
@MockBean(ServiceClass.class)  
@AutoConfigureTestDatabase(replace = NONE)
class ControllerMockTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean 
    private ServiceClass serviceClass;
    
    // Mock 測試方法
}
```

### P0 優先級 - 核心 API 測試 (第7-8週)

#### 1. AuthController Mock 測試
```java
// 測試端點
POST /api/auth/login
POST /api/auth/register  
POST /api/auth/parse
POST /api/auth/isexpired
POST /api/auth/refresh
```

**測試案例**:
```
✓ 登入成功 → 200 OK + JWT Token
✓ 登入失敗 → 401 Unauthorized  
✓ 註冊成功 → 201 Created
✓ Token 驗證 → 200 OK
✓ 輸入驗證 → 400 Bad Request
✓ SQL 注入防護 → 安全處理
```

#### 2. OrderController Mock 測試
```java
// 測試端點  
GET /api/orders
POST /api/orders
PUT /api/orders/{id}/status
POST /api/orders/{id}/bid
```

#### 3. AdminUserController Mock 測試
```java
// 測試端點
GET /api/admin/users
POST /api/admin/users
PUT /api/admin/users/{id}
DELETE /api/admin/users/{id}  
```

### P1 優先級 - 重要 API 測試 (第9-10週)

#### 4. PaymentController Mock 測試
#### 5. ContractController Mock 測試

### P2 優先級 - 輔助 API 測試 (第11-12週)  

#### 6. IndustryController Mock 測試
#### 7. LocationController Mock 測試

## 🛠️ 測試工具與配置

### 測試依賴配置
```xml
<!-- 已包含在 spring-boot-starter-test 中 -->
- JUnit 5
- Mockito  
- AssertJ
- Hamcrest
- Spring Test & Spring Boot Test
```

### 測試基礎類別設計

#### Service 單元測試基礎類別
```java
@ExtendWith(MockitoExtension.class)
abstract class ServiceUnitTestBase {
    
    // 通用 Mock 配置
    @Mock protected UserRepository userRepository;
    @Mock protected MapStruct converters;
    
    // 測試數據工廠方法
    protected User createTestUser() { ... }
    protected Order createTestOrder() { ... }
}
```

#### Controller Mock 測試基礎類別  
```java
@WebMvcTest
@AutoConfigureTestDatabase(replace = NONE)
abstract class ControllerMockTestBase {
    
    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    
    // 通用測試工具方法
    protected String toJson(Object obj) { ... }
    protected <T> T fromJson(String json, Class<T> clazz) { ... }
}
```

## 📋 測試執行與驗證

### Maven 測試命令
```bash
# 執行所有單元測試  
mvn test

# 執行特定服務測試
mvn test -Dtest="*ServiceTest"

# 執行特定控制器測試
mvn test -Dtest="*ControllerTest"

# 執行測試並生成覆蓋率報告
mvn clean test jacoco:report
```

### 測試覆蓋率目標
- **Service 層**: 80% 行覆蓋率
- **Controller 層**: 90% 行覆蓋率  
- **核心業務邏輯**: 95% 行覆蓋率

## 📈 測試質量保證

### Code Review 檢查清單
```
✓ 測試案例覆蓋所有主要業務邏輯分支
✓ Mock 物件正確配置，避免真實依賴  
✓ 測試資料獨立，不依賴外部狀態
✓ 異常情況測試完整
✓ 測試名稱清晰描述測試意圖
✓ 測試方法保持簡潔，單一職責
```

### 持續集成配置
```bash
# CI/CD Pipeline 中執行
mvn clean test -Dspring.profiles.active=test
mvn jacoco:report  
mvn sonar:sonar # SonarQube 代碼質量檢查
```

## 🎯 預期成果

### 測試完成後收益
1. **代碼質量提升**: 通過測試驅動開發提高代碼可靠性
2. **重構安全**: 完整測試套件保障重構安全性  
3. **Bug 減少**: 早期發現並修復潛在問題
4. **文檔價值**: 測試案例作為代碼使用範例
5. **開發效率**: 快速驗證功能正確性

### 長期維護策略
- 新功能開發必須包含對應測試
- 定期執行測試套件確保回歸質量
- 持續優化測試覆蓋率和測試效率
- 建立測試最佳實踐文檔和團隊培訓

## 📞 支援與資源

### 團隊協作
- **測試負責人**: 指定專人負責測試計畫執行
- **Code Review**: 所有測試代碼必須經過審查
- **知識分享**: 定期分享測試經驗和最佳實踐

### 技術支援資源
- Spring Boot Testing Reference
- Mockito Documentation  
- JUnit 5 User Guide
- AssertJ Documentation