# Case Manager AI Chat - API 測試規格文件

## 1. 執行摘要

本文件定義了 Case Manager AI Chat 系統的 API 測試策略與規範。系統包含 30 個控制器，超過 200 個 API 端點，涵蓋用戶管理、訂單管理、支付管理、認證管理等核心業務功能。

## 2. 測試策略與方法論

### 2.1 測試策略概述

```mermaid
graph TD
    A[API測試策略] --> B[功能測試]
    A --> C[安全測試]
    A --> D[效能測試]
    A --> E[整合測試]
    
    B --> B1[單元API測試]
    B --> B2[業務流程測試]
    B --> B3[邊界值測試]
    
    C --> C1[認證授權測試]
    C --> C2[輸入驗證測試]
    C --> C3[權限控制測試]
    
    D --> D1[負載測試]
    D --> D2[壓力測試]
    D --> D3[並發測試]
    
    E --> E1[模組間整合]
    E --> E2[第三方服務整合]
    E --> E3[端到端測試]
```

### 2.2 測試方法論

採用**風險驅動測試**方法論，結合以下測試實踐：

1. **BDD (行為驅動開發)**：使用 Given-When-Then 格式編寫測試案例
2. **數據驅動測試**：使用參數化測試覆蓋多種輸入場景
3. **契約測試**：確保 API 符合定義的規格
4. **持續測試**：整合到 CI/CD 流程中

## 3. 測試優先級分類（基於風險評估）

### 3.1 風險評估矩陣

| 風險等級 | 業務影響 | 發生機率 | 測試優先級 | 相關Controller |
|---------|---------|---------|-----------|---------------|
| 極高 | 系統癱瘓/資料外洩 | 高 | P0 - 立即測試 | AuthController, PaymentController |
| 高 | 核心功能失效 | 中 | P1 - 優先測試 | OrderController, UserController |
| 中 | 部分功能受限 | 中 | P2 - 常規測試 | IndustryController, RevenueShareController |
| 低 | 使用體驗影響 | 低 | P3 - 選擇測試 | ShowcaseController, FavouriteController |

### 3.2 優先級定義

```mermaid
graph LR
    P0[P0-極高優先級] --> P0A[認證/授權功能]
    P0 --> P0B[支付處理功能]
    P0 --> P0C[資料安全相關]
    
    P1[P1-高優先級] --> P1A[訂單管理功能]
    P1 --> P1B[用戶管理功能]
    P1 --> P1C[合約處理功能]
    
    P2[P2-中優先級] --> P2A[行業管理功能]
    P2 --> P2B[營收分潤功能]
    P2 --> P2C[評價管理功能]
    
    P3[P3-低優先級] --> P3A[展示功能]
    P3 --> P3B[收藏功能]
    P3 --> P3C[排名功能]
```

## 4. 測試類型詳細說明

### 4.1 功能測試 (Functional Testing)

#### 4.1.1 單元API測試
- **目標**：驗證每個API端點的基本功能
- **覆蓋範圍**：
  - 正常請求流程
  - 參數驗證
  - 回應格式驗證
  - 錯誤處理

#### 4.1.2 業務流程測試
- **目標**：驗證完整的業務場景
- **主要場景**：
  - 用戶註冊 → 登入 → 創建訂單 → 支付 → 完成
  - 服務商認證 → 創建模板 → 接收訂單 → 交付

#### 4.1.3 邊界值測試
- **目標**：測試極限情況
- **測試項目**：
  - 最大/最小值輸入
  - 空值處理
  - 特殊字符處理

### 4.2 安全測試 (Security Testing)

#### 4.2.1 認證授權測試
```mermaid
sequenceDiagram
    participant Client
    participant API
    participant Auth
    participant DB
    
    Client->>API: 未授權請求
    API->>Client: 401 Unauthorized
    
    Client->>Auth: 登入請求
    Auth->>DB: 驗證憑證
    DB->>Auth: 用戶資料
    Auth->>Client: JWT Token
    
    Client->>API: 帶Token請求
    API->>Auth: 驗證Token
    Auth->>API: 驗證成功
    API->>Client: 200 OK + 資料
```

#### 4.2.2 輸入驗證測試
- SQL注入測試
- XSS攻擊測試
- CSRF防護測試
- 檔案上傳安全測試

#### 4.2.3 權限控制測試
- 角色權限驗證（ADMIN, USER, MANAGER_ORDER）
- 資源訪問控制
- 數據隔離測試

### 4.3 效能測試 (Performance Testing)

#### 4.3.1 負載測試目標
| API類型 | 目標TPS | 響應時間(ms) | 並發用戶數 |
|---------|---------|-------------|-----------|
| 認證API | 100 | <200 | 500 |
| 查詢API | 500 | <100 | 1000 |
| 交易API | 50 | <500 | 200 |
| 上傳API | 20 | <2000 | 50 |

#### 4.3.2 壓力測試策略
```mermaid
graph TD
    A[開始測試] --> B[基準線測試]
    B --> C[逐步增加負載]
    C --> D{系統響應正常?}
    D -->|是| E[增加10%負載]
    D -->|否| F[記錄臨界點]
    E --> C
    F --> G[分析瓶頸]
    G --> H[優化建議]
```

### 4.4 整合測試 (Integration Testing)

#### 4.4.1 模組間整合測試
- 訂單與支付模組整合
- 用戶與認證模組整合
- 訂單與營收分潤整合

#### 4.4.2 第三方服務整合
- 郵件服務整合測試
- 檔案存儲服務測試
- WebSocket通訊測試

## 5. 測試環境架構

```mermaid
graph TB
    subgraph "測試環境"
        A[測試客戶端<br/>Postman/Newman] --> B[API Gateway<br/>Port: 8080]
        B --> C[應用服務器<br/>Spring Boot]
        C --> D[(測試數據庫<br/>MySQL)]
        C --> E[Cache<br/>Redis]
        C --> F[檔案存儲<br/>Local/S3]
    end
    
    subgraph "測試工具"
        G[Postman<br/>手動測試]
        H[Newman<br/>自動化測試]
        I[JMeter<br/>效能測試]
        J[OWASP ZAP<br/>安全測試]
    end
    
    subgraph "監控工具"
        K[Application Logs]
        L[Performance Metrics]
        M[Test Reports]
    end
```

### 5.1 環境配置要求

| 環境類型 | 用途 | 配置需求 | 數據策略 |
|---------|------|---------|---------|
| 開發測試環境 | 開發階段測試 | 單機配置 | Mock數據 |
| 整合測試環境 | 整合測試 | 集群配置 | 測試數據集 |
| 效能測試環境 | 效能測試 | 生產級配置 | 大量測試數據 |
| UAT環境 | 用戶驗收測試 | 準生產配置 | 仿真數據 |

## 6. 測試資料規劃

### 6.1 測試數據分類

```mermaid
graph TD
    A[測試數據] --> B[基礎數據]
    A --> C[業務數據]
    A --> D[邊界數據]
    A --> E[異常數據]
    
    B --> B1[用戶帳號]
    B --> B2[角色權限]
    B --> B3[系統配置]
    
    C --> C1[訂單數據]
    C --> C2[支付數據]
    C --> C3[行業數據]
    
    D --> D1[極大值]
    D --> D2[極小值]
    D --> D3[邊界條件]
    
    E --> E1[無效輸入]
    E --> E2[惡意輸入]
    E --> E3[異常格式]
```

### 6.2 測試數據管理策略

1. **數據隔離**：每個測試套件使用獨立的數據集
2. **數據重置**：測試前後自動重置數據狀態
3. **數據生成**：使用數據工廠模式生成測試數據
4. **數據版本控制**：測試數據腳本納入版本管理

### 6.3 測試用戶矩陣

| 用戶類型 | 用戶名 | 角色 | 測試用途 |
|---------|--------|------|---------|
| 超級管理員 | admin@test.com | ADMIN | 管理功能測試 |
| 訂單管理員 | manager@test.com | MANAGER_ORDER | 訂單管理測試 |
| 服務提供商 | provider@test.com | USER (Provider) | 服務商功能測試 |
| 一般客戶 | client@test.com | USER (Client) | 客戶功能測試 |
| 未認證用戶 | guest@test.com | - | 公開API測試 |

## 7. 測試執行流程

```mermaid
flowchart TD
    A[開始測試] --> B{環境準備就緒?}
    B -->|否| C[準備測試環境]
    B -->|是| D[載入測試數據]
    C --> D
    D --> E[執行冒煙測試]
    E --> F{冒煙測試通過?}
    F -->|否| G[修復關鍵問題]
    F -->|是| H[執行功能測試]
    G --> E
    H --> I[執行安全測試]
    I --> J[執行效能測試]
    J --> K[執行整合測試]
    K --> L[生成測試報告]
    L --> M{測試通過?}
    M -->|是| N[部署到下一環境]
    M -->|否| O[記錄缺陷]
    O --> P[修復缺陷]
    P --> E
    N --> Q[結束]
```

### 7.1 測試執行階段

1. **準備階段**
   - 環境配置驗證
   - 測試數據準備
   - 測試工具配置

2. **執行階段**
   - 冒煙測試（Smoke Test）
   - 功能測試執行
   - 非功能測試執行

3. **報告階段**
   - 測試結果收集
   - 缺陷記錄
   - 測試報告生成

## 8. 測試案例設計原則

### 8.1 測試案例結構

```yaml
TestCase:
  id: TC_AUTH_001
  name: 用戶登入測試
  priority: P0
  category: 功能測試/認證
  preconditions:
    - 測試用戶已註冊
    - API服務正常運行
  test_data:
    username: "test@example.com"
    password: "Test123!"
  steps:
    - step: 發送POST請求到/api/auth/login
      expected: 返回200狀態碼
    - step: 驗證響應包含JWT token
      expected: token不為空
    - step: 驗證token格式
      expected: 符合JWT標準格式
  postconditions:
    - 清理測試會話
```

### 8.2 測試案例命名規範

- 格式：`TC_<模組>_<編號>_<描述>`
- 範例：`TC_ORDER_001_CreateOrder`

## 9. Postman 測試集合架構

```mermaid
graph TD
    A[Case Manager API Tests] --> B[環境配置]
    A --> C[全局變數]
    A --> D[測試集合]
    
    B --> B1[開發環境]
    B --> B2[測試環境]
    B --> B3[UAT環境]
    
    C --> C1[baseUrl]
    C --> C2[authToken]
    C --> C3[testData]
    
    D --> D1[認證測試集]
    D --> D2[訂單測試集]
    D --> D3[支付測試集]
    D --> D4[用戶測試集]
    
    D1 --> D1A[登入測試]
    D1 --> D1B[註冊測試]
    D1 --> D1C[權限測試]
```

### 9.1 Postman 環境變數設計

```json
{
  "dev": {
    "baseUrl": "http://localhost:8080",
    "authToken": "{{dynamic}}",
    "testUserId": "{{dynamic}}",
    "testOrderId": "{{dynamic}}"
  },
  "test": {
    "baseUrl": "http://test-api.casemgr.com",
    "authToken": "{{dynamic}}",
    "testUserId": "{{dynamic}}",
    "testOrderId": "{{dynamic}}"
  }
}
```

### 9.2 測試腳本架構

```javascript
// Pre-request Script 範例
pm.environment.set("timestamp", new Date().toISOString());
pm.environment.set("randomEmail", `test_${Date.now()}@example.com`);

// Test Script 範例
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has required fields", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData).to.have.property('status');
});

// 保存動態數據供後續測試使用
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set("lastCreatedId", response.id);
}
```

### 9.3 斷言規則庫

| 斷言類型 | 用途 | 範例 |
|---------|------|------|
| 狀態碼驗證 | 驗證HTTP狀態 | `pm.response.to.have.status(200)` |
| 響應時間 | 效能驗證 | `pm.expect(pm.response.responseTime).to.be.below(200)` |
| 資料結構 | 驗證響應格式 | `pm.expect(jsonData).to.have.property('id')` |
| 業務規則 | 驗證業務邏輯 | `pm.expect(order.status).to.be.oneOf(['PENDING', 'CONFIRMED'])` |
| 安全驗證 | 驗證安全要求 | `pm.expect(pm.response.headers.get('X-Content-Type-Options')).to.eql('nosniff')` |

## 10. 測試自動化策略

### 10.1 CI/CD 整合

```mermaid
graph LR
    A[程式碼提交] --> B[構建觸發]
    B --> C[單元測試]
    C --> D[API測試]
    D --> E{測試通過?}
    E -->|是| F[部署到測試環境]
    E -->|否| G[通知開發團隊]
    F --> H[執行整合測試]
    H --> I[生成測試報告]
```

### 10.2 自動化測試工具鏈

1. **Newman**: Postman集合的命令行執行
2. **Jenkins/GitLab CI**: CI/CD平台
3. **Allure**: 測試報告生成
4. **Slack/Email**: 測試結果通知

## 11. 測試度量指標

### 11.1 關鍵績效指標 (KPIs)

| 指標名稱 | 計算方式 | 目標值 | 監控頻率 |
|---------|---------|--------|---------|
| API覆蓋率 | 已測試API數/總API數 | >95% | 每日 |
| 測試通過率 | 通過測試數/總測試數 | >98% | 每次執行 |
| 缺陷密度 | 缺陷數/測試案例數 | <0.05 | 每週 |
| 平均響應時間 | 所有API響應時間平均值 | <200ms | 每日 |
| 測試執行時間 | 完整測試套件執行時間 | <30分鐘 | 每次執行 |

### 11.2 測試報告模板

```markdown
# API測試報告
- 測試日期: {{date}}
- 測試環境: {{environment}}
- 測試範圍: {{scope}}

## 執行摘要
- 總測試案例數: {{total}}
- 通過: {{passed}}
- 失敗: {{failed}}
- 跳過: {{skipped}}

## 詳細結果
[按模組分類的測試結果]

## 發現的問題
[缺陷列表及嚴重程度]

## 建議與下一步
[改進建議]
```

## 12. 風險與緩解措施

| 風險項目 | 風險等級 | 影響 | 緩解措施 |
|---------|---------|------|---------|
| 測試環境不穩定 | 高 | 測試中斷 | 建立專用測試環境，定期維護 |
| 測試數據污染 | 中 | 測試結果不準確 | 實施數據隔離和自動清理機制 |
| 第三方服務依賴 | 中 | 整合測試失敗 | 使用Mock服務和服務虛擬化 |
| 測試覆蓋不足 | 高 | 缺陷遺漏 | 定期審查和更新測試案例 |

## 13. 測試最佳實踐

1. **測試獨立性**: 每個測試案例應該獨立執行，不依賴其他測試的結果
2. **數據驅動**: 使用外部數據源驅動測試，提高測試覆蓋率
3. **持續優化**: 定期回顧測試結果，優化測試案例和執行策略
4. **文檔化**: 保持測試文檔與代碼同步更新
5. **協作溝通**: 與開發團隊保持密切溝通，及時反饋問題

## 14. 附錄

### 14.1 測試工具清單
- Postman v9.0+
- Newman v5.0+
- JMeter v5.4+
- OWASP ZAP v2.11+

### 14.2 參考文獻
- REST API Testing Best Practices
- OWASP API Security Top 10
- ISO/IEC 25010 軟體品質模型

### 14.3 術語表
- **TPS**: Transactions Per Second，每秒交易數
- **JWT**: JSON Web Token，用於API認證
- **UAT**: User Acceptance Testing，用戶驗收測試
- **CI/CD**: Continuous Integration/Continuous Deployment，持續整合/持續部署