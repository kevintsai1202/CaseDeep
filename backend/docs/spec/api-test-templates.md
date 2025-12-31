# API 測試案例模板集

## 1. AuthController 測試案例模板

### TC_AUTH_001: 用戶登入成功測試

```yaml
test_case:
  id: TC_AUTH_001
  name: 用戶登入成功測試
  description: 驗證使用有效憑證可以成功登入並獲得JWT token
  priority: P0
  category: 功能測試/認證
  
preconditions:
  - 測試用戶已註冊（test@example.com / Test123!）
  - API服務正常運行
  
test_data:
  valid_credentials:
    username: "test@example.com"
    password: "Test123!"
  
test_steps:
  - step: 1
    action: "發送POST請求到 /api/auth/login"
    data: |
      {
        "username": "test@example.com",
        "password": "Test123!"
      }
    expected_result:
      - "HTTP狀態碼為200"
      - "響應包含token欄位"
      - "token為有效的JWT格式"
      
  - step: 2
    action: "解析JWT token"
    expected_result:
      - "token包含用戶ID"
      - "token包含用戶角色"
      - "token有效期為預設值"
      
  - step: 3
    action: "使用獲得的token訪問受保護的API"
    data: "Authorization: Bearer {token}"
    expected_result:
      - "請求成功，返回200"
      - "能夠獲取用戶資料"

postconditions:
  - 保存token供後續測試使用
  
test_scripts:
  pre_request: |
    // 設置時間戳
    pm.environment.set("timestamp", new Date().toISOString());
    
  test: |
    pm.test("Status code is 200", function () {
        pm.response.to.have.status(200);
    });
    
    pm.test("Response has token", function () {
        const jsonData = pm.response.json();
        pm.expect(jsonData).to.have.property('token');
        pm.expect(jsonData.token).to.be.a('string');
        pm.expect(jsonData.token.length).to.be.above(20);
    });
    
    pm.test("Token is valid JWT", function () {
        const token = pm.response.json().token;
        const parts = token.split('.');
        pm.expect(parts.length).to.equal(3);
    });
    
    // 保存token
    pm.environment.set("authToken", pm.response.json().token);
```

### TC_AUTH_002: 用戶登入失敗測試（錯誤密碼）

```yaml
test_case:
  id: TC_AUTH_002
  name: 用戶登入失敗測試（錯誤密碼）
  description: 驗證使用錯誤密碼無法登入
  priority: P0
  category: 功能測試/認證/負面測試
  
test_data:
  invalid_credentials:
    - username: "test@example.com"
      password: "WrongPassword"
    - username: "test@example.com"
      password: ""
    - username: "test@example.com"
      password: "' OR '1'='1"
      
test_steps:
  - step: 1
    action: "使用錯誤密碼發送登入請求"
    expected_result:
      - "HTTP狀態碼為401"
      - "響應包含錯誤訊息"
      - "不返回token"
      - "錯誤訊息不洩露敏感信息"
```

### TC_AUTH_003: 用戶註冊測試

```yaml
test_case:
  id: TC_AUTH_003
  name: 新用戶註冊測試
  description: 驗證新用戶可以成功註冊
  priority: P0
  category: 功能測試/認證
  
test_data:
  new_user:
    username: "newuser_{{timestamp}}@example.com"
    password: "NewUser123!"
    email: "newuser_{{timestamp}}@example.com"
    userType: "CLIENT"
    
validation_rules:
  - 郵箱格式驗證
  - 密碼強度驗證（至少8位，包含大小寫字母和數字）
  - 用戶名唯一性驗證
  
test_steps:
  - step: 1
    action: "發送註冊請求"
    expected_result:
      - "HTTP狀態碼為201"
      - "返回用戶ID"
      - "用戶狀態為待驗證"
      
  - step: 2
    action: "嘗試重複註冊相同郵箱"
    expected_result:
      - "HTTP狀態碼為409"
      - "返回重複註冊錯誤"
```

### TC_AUTH_004: SQL注入防護測試

```yaml
test_case:
  id: TC_AUTH_004
  name: SQL注入防護測試
  description: 驗證登入API對SQL注入的防護
  priority: P0
  category: 安全測試/認證
  
test_data:
  sql_injection_attempts:
    - username: "admin' --"
      password: "anything"
    - username: "admin' OR '1'='1"
      password: "anything"
    - username: "admin'; DROP TABLE users; --"
      password: "anything"
      
test_steps:
  - step: 1
    action: "使用SQL注入payload發送請求"
    expected_result:
      - "請求被拒絕"
      - "返回400或401錯誤"
      - "不執行SQL語句"
      - "記錄安全事件"
```

## 2. OrderController 測試案例模板

### TC_ORDER_001: 建立訂單測試

```yaml
test_case:
  id: TC_ORDER_001
  name: 從模板建立訂單測試
  description: 驗證可以成功從訂單模板建立新訂單
  priority: P1
  category: 功能測試/訂單管理
  
preconditions:
  - 用戶已登入（獲得有效token）
  - 存在可用的訂單模板
  - 用戶帳戶狀態正常
  
test_data:
  order_request:
    templateId: "{{templateId}}"
    clientNotes: "測試訂單備註"
    deliveryDate: "{{futureDate}}"
    customFields:
      - fieldId: "field1"
        value: "自定義值1"
      - fieldId: "field2"
        value: "自定義值2"
        
test_steps:
  - step: 1
    action: "發送POST請求到 /api/orders"
    headers:
      Authorization: "Bearer {{authToken}}"
    expected_result:
      - "HTTP狀態碼為201"
      - "返回訂單ID（base62格式）"
      - "訂單狀態為PENDING"
      - "訂單包含正確的模板資訊"
      
  - step: 2
    action: "查詢新建立的訂單"
    expected_result:
      - "可以成功獲取訂單詳情"
      - "訂單資料與請求一致"
      
validation_points:
  - 訂單號格式驗證（base62）
  - 必填欄位完整性
  - 價格計算正確性
  - 審計欄位自動填充（createTime, createUser）
```

### TC_ORDER_002: 訂單狀態轉換測試

```yaml
test_case:
  id: TC_ORDER_002
  name: 訂單狀態轉換測試
  description: 驗證訂單狀態機的正確性
  priority: P1
  category: 功能測試/訂單管理/狀態管理
  
test_scenario: |
  PENDING -> CONFIRMED -> IN_PROGRESS -> COMPLETED
  PENDING -> CANCELLED
  
test_steps:
  - step: 1
    action: "建立新訂單（狀態：PENDING）"
    
  - step: 2
    action: "確認訂單"
    endpoint: "PATCH /api/orders/{{orderId}}/status"
    data:
      status: "CONFIRMED"
    expected_result:
      - "狀態成功更新為CONFIRMED"
      - "觸發確認通知"
      
  - step: 3
    action: "嘗試非法狀態轉換"
    data:
      status: "COMPLETED"  # 不能直接從CONFIRMED到COMPLETED
    expected_result:
      - "請求被拒絕"
      - "返回400錯誤"
      - "錯誤訊息說明狀態轉換規則"
      
validation_rules:
  - 只有訂單擁有者或管理員可以更新狀態
  - 狀態轉換必須符合業務規則
  - 每次狀態變更都要記錄審計日誌
```

### TC_ORDER_003: 訂單查詢與權限測試

```yaml
test_case:
  id: TC_ORDER_003
  name: 訂單查詢與數據隔離測試
  description: 驗證用戶只能查看自己的訂單
  priority: P1
  category: 功能測試/訂單管理/權限控制
  
test_users:
  - user_a: "client_a@example.com"
  - user_b: "client_b@example.com"
  - admin: "admin@example.com"
  
test_steps:
  - step: 1
    action: "User A 建立訂單"
    user: "user_a"
    
  - step: 2
    action: "User B 嘗試查看 User A 的訂單"
    user: "user_b"
    expected_result:
      - "返回403 Forbidden"
      - "無法獲取其他用戶的訂單資料"
      
  - step: 3
    action: "Admin 查看所有訂單"
    user: "admin"
    expected_result:
      - "可以查看所有用戶的訂單"
      - "返回完整的訂單列表"
```

## 3. PaymentController 測試案例模板

### TC_PAYMENT_001: 支付流程測試

```yaml
test_case:
  id: TC_PAYMENT_001
  name: 完整支付流程測試
  description: 驗證從發起支付到完成的完整流程
  priority: P0
  category: 功能測試/支付管理
  
preconditions:
  - 存在待支付的訂單
  - 用戶已設置支付方式
  
test_flow:
  1. 發起支付
  2. 處理支付
  3. 上傳收據
  4. 完成支付
  5. 觸發營收分潤
  
test_steps:
  - step: 1
    action: "發起支付請求"
    endpoint: "PATCH /api/orders/payment/{{paymentId}}/pay"
    expected_result:
      - "支付狀態更新為PROCESSING"
      - "生成支付流水號"
      - "記錄支付時間"
      
  - step: 2
    action: "模擬支付回調"
    expected_result:
      - "支付狀態更新"
      - "訂單狀態同步更新"
      
  - step: 3
    action: "上傳支付收據"
    endpoint: "POST /api/orders/payment/{{paymentId}}/receipt"
    data: "multipart/form-data with receipt file"
    expected_result:
      - "檔案上傳成功"
      - "收據與支付記錄關聯"
      
concurrent_test:
  description: "並發支付測試"
  concurrent_users: 10
  expected_behavior:
    - "防止重複支付"
    - "保持數據一致性"
    - "正確的錯誤處理"
```

### TC_PAYMENT_002: 支付安全測試

```yaml
test_case:
  id: TC_PAYMENT_002
  name: 支付安全測試
  description: 驗證支付過程的安全性
  priority: P0
  category: 安全測試/支付管理
  
test_scenarios:
  - scenario: "防止金額篡改"
    steps:
      - action: "嘗試修改支付金額"
        expected: "請求被拒絕，金額不可修改"
        
  - scenario: "防止重複支付"
    steps:
      - action: "對同一訂單發起多次支付"
        expected: "只有第一次支付成功"
        
  - scenario: "支付超時處理"
    steps:
      - action: "發起支付後不完成"
        expected: "超時後自動取消"
        
  - scenario: "退款安全"
    steps:
      - action: "申請退款"
        expected: "需要審核流程"
```

## 4. UserController 測試案例模板

### TC_USER_001: 用戶資料管理測試

```yaml
test_case:
  id: TC_USER_001
  name: 用戶個人資料更新測試
  description: 驗證用戶可以更新自己的個人資料
  priority: P1
  category: 功能測試/用戶管理
  
test_data:
  personal_info:
    firstName: "測試"
    lastName: "用戶"
    phone: "+886912345678"
    address: "台北市信義區測試路100號"
    birthDate: "1990-01-01"
    
  business_info:
    companyName: "測試公司"
    taxId: "12345678"
    businessAddress: "台北市內湖區商業路200號"
    
test_steps:
  - step: 1
    action: "更新個人資料"
    endpoint: "PUT /api/profile/personal"
    validation:
      - "電話號碼格式驗證"
      - "生日日期驗證（不能是未來）"
      - "地址長度限制"
      
  - step: 2
    action: "更新商業資料"
    endpoint: "PUT /api/profile/business"
    validation:
      - "統一編號格式驗證"
      - "公司名稱唯一性（選擇性）"
      
  - step: 3
    action: "驗證部分更新"
    description: "只更新部分欄位，其他欄位應保持不變"
    data:
      phone: "+886987654321"
    expected:
      - "只有電話號碼被更新"
      - "其他欄位保持原值"
```

### TC_USER_002: 密碼管理測試

```yaml
test_case:
  id: TC_USER_002
  name: 密碼變更測試
  description: 驗證密碼變更功能的安全性
  priority: P0
  category: 功能測試/用戶管理/安全
  
test_steps:
  - step: 1
    action: "使用正確的舊密碼變更"
    data:
      oldPassword: "Current123!"
      newPassword: "NewPass456!"
    expected:
      - "密碼成功變更"
      - "需要重新登入"
      
  - step: 2
    action: "使用錯誤的舊密碼"
    expected:
      - "變更失敗"
      - "返回401錯誤"
      
  - step: 3
    action: "密碼強度驗證"
    test_cases:
      - password: "weak"  # 太短
      - password: "12345678"  # 沒有字母
      - password: "password"  # 沒有數字
      - password: "Password"  # 沒有數字
    expected:
      - "所有弱密碼都被拒絕"
      - "返回明確的錯誤訊息"
```

## 5. 通用測試模式

### 分頁查詢測試模板

```yaml
test_pattern:
  name: 分頁查詢測試
  applicable_to:
    - GET /api/orders
    - GET /api/users
    - GET /api/revenue-shares
    
  test_cases:
    - case: "預設分頁"
      params: {}
      expected:
        - "返回第一頁"
        - "預設20筆資料"
        
    - case: "指定分頁參數"
      params:
        page: 2
        size: 50
      expected:
        - "返回第二頁"
        - "每頁50筆"
        
    - case: "排序測試"
      params:
        sort: "createTime,desc"
      expected:
        - "按建立時間降序排列"
        
    - case: "超出範圍"
      params:
        page: 9999
      expected:
        - "返回空資料"
        - "不返回錯誤"
        
postman_test_script: |
  // 分頁結構驗證
  pm.test("Has pagination structure", function () {
      const jsonData = pm.response.json();
      pm.expect(jsonData).to.have.property('content');
      pm.expect(jsonData).to.have.property('pageable');
      pm.expect(jsonData).to.have.property('totalElements');
      pm.expect(jsonData).to.have.property('totalPages');
  });
  
  // 分頁參數驗證
  pm.test("Pagination parameters are correct", function () {
      const jsonData = pm.response.json();
      const requestedPage = pm.request.url.query.get('page') || 0;
      const requestedSize = pm.request.url.query.get('size') || 20;
      
      pm.expect(jsonData.pageable.pageNumber).to.eql(parseInt(requestedPage));
      pm.expect(jsonData.pageable.pageSize).to.eql(parseInt(requestedSize));
  });
```

### 輸入驗證測試模板

```yaml
test_pattern:
  name: 輸入驗證測試
  description: 通用的輸入驗證測試模式
  
  validation_scenarios:
    - type: "必填欄位"
      test_data:
        - field: "email"
          value: null
          expected_error: "Email is required"
          
    - type: "格式驗證"
      test_data:
        - field: "email"
          value: "invalid-email"
          expected_error: "Invalid email format"
          
    - type: "長度限制"
      test_data:
        - field: "name"
          value: "a" * 256  # 超過255字符
          expected_error: "Name too long"
          
    - type: "數值範圍"
      test_data:
        - field: "age"
          value: -1
          expected_error: "Age must be positive"
          
    - type: "特殊字符"
      test_data:
        - field: "username"
          value: "<script>alert('xss')</script>"
          expected_error: "Invalid characters"
```

### 權限控制測試模板

```yaml
test_pattern:
  name: 權限控制測試
  description: 驗證不同角色的訪問權限
  
  test_matrix:
    - endpoint: "/api/admin/*"
      roles:
        ADMIN: 200
        USER: 403
        ANONYMOUS: 401
        
    - endpoint: "/api/orders"
      roles:
        ADMIN: 200  # 可看所有
        USER: 200   # 只看自己的
        ANONYMOUS: 401
        
    - endpoint: "/api/profile"
      roles:
        ADMIN: 200
        USER: 200
        ANONYMOUS: 401
        
postman_pre_request: |
  // 根據測試角色設置不同的token
  const role = pm.variables.get("testRole");
  const tokens = {
      "ADMIN": pm.environment.get("adminToken"),
      "USER": pm.environment.get("userToken"),
      "ANONYMOUS": null
  };
  
  if (tokens[role]) {
      pm.request.headers.add({
          key: 'Authorization',
          value: `Bearer ${tokens[role]}`
      });
  }
```

### 並發測試模板

```yaml
test_pattern:
  name: 並發測試
  description: 測試API在並發請求下的行為
  
  scenarios:
    - name: "並發建立"
      endpoint: "POST /api/orders"
      concurrent_requests: 10
      expected_behavior:
        - "所有請求都應成功或部分失敗"
        - "不應產生重複資料"
        - "資料一致性保持"
        
    - name: "並發更新"
      endpoint: "PATCH /api/orders/{id}"
      concurrent_requests: 5
      expected_behavior:
        - "使用樂觀鎖防止並發衝突"
        - "返回版本衝突錯誤"
        
newman_command: |
  # 使用Newman執行並發測試
  newman run collection.json \
    -n 10 \
    --delay-request 0 \
    --reporters cli,json \
    --reporter-json-export results.json
```

## 6. Postman Collection 結構範例

```json
{
  "info": {
    "name": "Case Manager API Tests",
    "description": "完整的API測試集合",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "auth": {
    "type": "bearer",
    "bearer": [
      {
        "key": "token",
        "value": "{{authToken}}",
        "type": "string"
      }
    ]
  },
  "event": [
    {
      "listen": "prerequest",
      "script": {
        "type": "text/javascript",
        "exec": [
          "// 全局前置腳本",
          "console.log('Executing: ' + pm.info.requestName);",
          "pm.variables.set('timestamp', new Date().getTime());"
        ]
      }
    },
    {
      "listen": "test",
      "script": {
        "type": "text/javascript",
        "exec": [
          "// 全局測試腳本",
          "pm.test('Response time is less than 1000ms', function () {",
          "    pm.expect(pm.response.responseTime).to.be.below(1000);",
          "});"
        ]
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    },
    {
      "key": "apiVersion",
      "value": "v1"
    }
  ],
  "item": [
    {
      "name": "Authentication",
      "item": [
        {
          "name": "Login Success",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n    \"username\": \"test@example.com\",\n    \"password\": \"Test123!\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{baseUrl}}/api/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["api", "auth", "login"]
            }
          },
          "event": [
            {
              "listen": "test",
              "script": {
                "exec": [
                  "pm.test('Login successful', function () {",
                  "    pm.response.to.have.status(200);",
                  "    const jsonData = pm.response.json();",
                  "    pm.expect(jsonData).to.have.property('token');",
                  "    pm.environment.set('authToken', jsonData.token);",
                  "});"
                ]
              }
            }
          ]
        }
      ]
    }
  ]
}
```

## 7. 測試數據管理

### 測試數據工廠

```javascript
// 測試數據生成器
const TestDataFactory = {
  // 生成隨機郵箱
  generateEmail: () => {
    const timestamp = new Date().getTime();
    return `test_${timestamp}@example.com`;
  },
  
  // 生成測試用戶
  generateUser: (type = 'CLIENT') => {
    return {
      username: TestDataFactory.generateEmail(),
      password: 'Test123!',
      email: TestDataFactory.generateEmail(),
      userType: type,
      firstName: '測試',
      lastName: '用戶'
    };
  },
  
  // 生成訂單資料
  generateOrder: (templateId) => {
    return {
      templateId: templateId,
      clientNotes: '測試訂單 - ' + new Date().toISOString(),
      deliveryDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
      customFields: [
        { fieldId: 'field1', value: '測試值1' },
        { fieldId: 'field2', value: '測試值2' }
      ]
    };
  }
};

// 在Postman中使用
pm.globals.set('TestDataFactory', TestDataFactory.toString());
```

### 測試環境變數管理

```json
{
  "development": {
    "baseUrl": "http://localhost:8080",
    "adminUser": "admin@test.com",
    "adminPassword": "Admin123!",
    "testUser": "user@test.com",
    "testPassword": "User123!"
  },
  "testing": {
    "baseUrl": "http://test-api.casemgr.com",
    "adminUser": "admin@test.com",
    "adminPassword": "Admin123!",
    "testUser": "user@test.com",
    "testPassword": "User123!"
  },
  "production": {
    "baseUrl": "https://api.casemgr.com",
    "note": "不在生產環境執行自動化測試"
  }
}
```

## 8. 測試執行最佳實踐

### 測試執行順序

```yaml
execution_order:
  1. setup:
     - name: "環境檢查"
       tests:
         - "健康檢查API"
         - "數據庫連接測試"
         
  2. authentication:
     - name: "認證測試"
       tests:
         - "管理員登入"
         - "一般用戶登入"
         - "保存tokens"
         
  3. core_features:
     - name: "核心功能測試"
       parallel: false
       tests:
         - "用戶管理"
         - "訂單管理"
         - "支付處理"
         
  4. integration:
     - name: "整合測試"
       parallel: true
       tests:
         - "訂單流程"
         - "支付流程"
         
  5. cleanup:
     - name: "清理測試數據"
       tests:
         - "刪除測試訂單"
         - "刪除測試用戶"
```

### 錯誤處理策略

```javascript
// Postman測試腳本中的錯誤處理
try {
    const jsonData = pm.response.json();
    
    // 檢查是否為錯誤響應
    if (pm.response.code >= 400) {
        console.error('API Error:', jsonData);
        pm.test(`API返回錯誤: ${jsonData.message || 'Unknown error'}`, function() {
            pm.expect(pm.response.code).to.be.below(400);
        });
    }
} catch (e) {
    // 處理非JSON響應
    console.error('Response parsing error:', e);
    pm.test('響應應該是有效的JSON', function() {
        pm.response.to.be.json;
    });
}
```

### 測試報告範例

```markdown
# API測試執行報告

**執行日期**: 2024-01-15
**測試環境**: Testing
**執行時間**: 15分32秒

## 執行摘要

| 類別 | 總數 | 通過 | 失敗 | 跳過 | 通過率 |
|------|------|------|------|------|--------|
| 認證測試 | 10 | 10 | 0 | 0 | 100% |
| 訂單測試 | 25 | 23 | 2 | 0 | 92% |
| 支付測試 | 15 | 15 | 0 | 0 | 100% |
| 用戶測試 | 20 | 19 | 1 | 0 | 95% |
| **總計** | **70** | **67** | **3** | **0** | **95.7%** |

## 失敗測試詳情

1. **TC_ORDER_015**: 並發訂單建立測試
   - 錯誤：偶發性的數據庫鎖定錯誤
   - 嚴重性：中
   - 建議：優化數據庫事務處理

2. **TC_ORDER_023**: 大量訂單查詢效能測試
   - 錯誤：響應時間超過預期（>500ms）
   - 嚴重性：低
   - 建議：添加查詢快取

3. **TC_USER_008**: 用戶資料並發更新測試
   - 錯誤：樂觀鎖版本衝突
   - 嚴重性：低
   - 建議：這是預期行為，調整測試預期
```

---

這個測試案例模板集提供了完整的測試框架，包含了主要Controller的詳細測試案例、通用測試模式、Postman配置範例和最佳實踐。每個測試案例都有清晰的結構，包含前置條件、測試步驟、預期結果和測試腳本，方便測試團隊快速上手並執行測試。