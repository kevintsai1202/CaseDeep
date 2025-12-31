# Admin User Manager API 文件

## 功能概述

Admin User Manager API 提供管理員對系統用戶進行完整管理的功能，包括創建、查詢、更新和刪除用戶，以及管理費用代碼（Fee Code）。此 API 支援角色產業範圍（Role Industry Scope）的設定，允許管理員指定用戶在特定產業或所有產業的管理權限。

## API 端點列表

| HTTP 方法 | 端點路徑 | 功能描述 |
|-----------|----------|----------|
| GET | `/api/admin/users` | 獲取所有用戶（包含詳細資訊） |
| POST | `/api/admin/users` | 創建新用戶 |
| PUT | `/api/admin/users/{userId}` | 更新現有用戶 |
| DELETE | `/api/admin/users/{userId}` | 刪除（軟刪除）用戶 |
| POST | `/api/admin/feecodies/{code}` | 創建費用代碼 |
| GET | `/api/admin/feecodies` | 獲取所有費用代碼 |

## 端點詳細說明

### 1. 獲取所有用戶

**端點**: `GET /api/admin/users`

**功能描述**: 獲取系統中所有用戶的詳細資訊，包括個人資料、商業資料、角色等。

**請求參數**: 無

**請求標頭**:
- `Authorization`: Bearer {token} (必填)

**響應格式**: `application/json`

**響應範例**:
```json
[
  {
    "userId": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "userType": "CLIENT",
    "personProfile": {
      "firstName": "John",
      "lastName": "Doe",
      "phone": "+1234567890"
    },
    "businessProfile": {
      "companyName": "ABC Corp",
      "businessRegistrationNumber": "12345678"
    },
    "roles": [
      {
        "id": 1,
        "name": "ROLE_USER_MANAGE",
        "description": "User Management Role"
      }
    ],
    "title": "Senior Manager",
    "vedioUrl": "https://example.com/video.mp4",
    "signatureUrl": "https://example.com/signature.png",
    "content": "Professional services provider"
  }
]
```

**可能的錯誤碼**:
- `401 Unauthorized`: 未提供有效的認證令牌
- `403 Forbidden`: 用戶無管理員權限
- `500 Internal Server Error`: 伺服器內部錯誤

### 2. 創建新用戶

**端點**: `POST /api/admin/users`

**功能描述**: 管理員創建新用戶，可指定角色和產業管理範圍。

**請求參數**:

| 參數名稱 | 資料類型 | 是否必填 | 驗證規則 | 說明 |
|----------|----------|----------|----------|------|
| username | String | 是 | 3-45字元 | 用戶名稱 |
| password | String | 是 | 最少8字元 | 用戶密碼 |
| email | String | 是 | 有效的Email格式，最多100字元 | 電子郵件地址 |
| userType | String | 否 | CLIENT/PROVIDER | 用戶類型，預設為CLIENT |
| region | String | 是 | - | 地區代碼（如：US, CA, TW） |
| roleNames | Array[String] | 否 | - | 角色名稱列表 |
| roleIndustryScopes | Array[Object] | 否 | - | 角色產業範圍設定 |

**roleIndustryScopes 物件結構**:
```json
{
  "roleName": "ROLE_USER_MANAGE",
  "isAllIndustries": false,
  "industryIds": [1, 2, 3]
}
```

**請求範例**:
```json
{
  "username": "new_admin",
  "password": "SecurePassword123!",
  "email": "admin@example.com",
  "userType": "CLIENT",
  "region": "TW",
  "roleNames": ["ROLE_USER_MANAGE", "ROLE_INDUSTRY_MANAGE"],
  "roleIndustryScopes": [
    {
      "roleName": "ROLE_USER_MANAGE",
      "isAllIndustries": true,
      "industryIds": []
    },
    {
      "roleName": "ROLE_INDUSTRY_MANAGE",
      "isAllIndustries": false,
      "industryIds": [1, 2, 3]
    }
  ]
}
```

**響應格式**: `application/json`

**響應範例**:
```json
{
  "userId": 100,
  "username": "new_admin",
  "email": "admin@example.com",
  "userType": "CLIENT",
  "roles": [
    {
      "id": 1,
      "name": "ROLE_USER_MANAGE",
      "description": "User Management Role"
    },
    {
      "id": 2,
      "name": "ROLE_INDUSTRY_MANAGE",
      "description": "Industry Management Role"
    }
  ],
  "personProfile": null,
  "businessProfile": null,
  "title": null,
  "vedioUrl": null,
  "signatureUrl": null,
  "content": null
}
```

**可能的錯誤碼**:
- `400 Bad Request`: 請求參數驗證失敗
- `401 Unauthorized`: 未提供有效的認證令牌
- `403 Forbidden`: 用戶無管理員權限
- `409 Conflict`: 用戶名或電子郵件已存在
- `500 Internal Server Error`: 伺服器內部錯誤

### 3. 更新用戶資訊

**端點**: `PUT /api/admin/users/{userId}`

**功能描述**: 管理員更新現有用戶的資訊、角色和產業管理範圍。

**路徑參數**:
- `userId`: Long (必填) - 要更新的用戶ID

**請求參數**:

| 參數名稱 | 資料類型 | 是否必填 | 驗證規則 | 說明 |
|----------|----------|----------|----------|------|
| password | String | 否 | 最少8字元 | 新密碼（僅在需要更改時提供） |
| userType | String | 否 | CLIENT/PROVIDER | 用戶類型 |
| region | String | 否 | - | 地區代碼 |
| roleNames | Array[String] | 否 | - | 完整的角色名稱列表（將替換現有角色） |
| locked | Boolean | 否 | - | 是否鎖定帳戶 |
| enabled | Boolean | 否 | - | 是否啟用帳戶（軟刪除控制） |
| commissionRate | Double | 否 | 0.0-1.0 | 傭金率 |
| roleIndustryScopes | Array[Object] | 否 | - | 角色產業範圍設定 |

**請求範例**:
```json
{
  "userType": "PROVIDER",
  "region": "US",
  "roleNames": ["ROLE_USER_MANAGE", "ROLE_ORDER_MANAGE"],
  "locked": false,
  "enabled": true,
  "commissionRate": 0.15,
  "roleIndustryScopes": [
    {
      "roleName": "ROLE_USER_MANAGE",
      "isAllIndustries": false,
      "industryIds": [1, 2, 5, 8]
    }
  ]
}
```

**響應格式**: `application/json`

**響應範例**:
```json
{
  "userId": 100,
  "username": "new_admin",
  "email": "admin@example.com",
  "userType": "PROVIDER",
  "roles": [
    {
      "id": 1,
      "name": "ROLE_USER_MANAGE",
      "description": "User Management Role"
    },
    {
      "id": 3,
      "name": "ROLE_ORDER_MANAGE",
      "description": "Order Management Role"
    }
  ],
  "personProfile": null,
  "businessProfile": null,
  "title": null,
  "vedioUrl": null,
  "signatureUrl": null,
  "content": null
}
```

**可能的錯誤碼**:
- `400 Bad Request`: 請求參數驗證失敗
- `401 Unauthorized`: 未提供有效的認證令牌
- `403 Forbidden`: 用戶無管理員權限
- `404 Not Found`: 指定的用戶不存在
- `500 Internal Server Error`: 伺服器內部錯誤

### 4. 刪除用戶

**端點**: `DELETE /api/admin/users/{userId}`

**功能描述**: 管理員軟刪除現有用戶（將用戶狀態設為停用）。

**路徑參數**:
- `userId`: Long (必填) - 要刪除的用戶ID

**請求標頭**:
- `Authorization`: Bearer {token} (必填)

**響應格式**: `text/plain`

**成功響應範例**:
```
User with ID: 100 has been deleted successfully.
```

**可能的錯誤碼**:
- `401 Unauthorized`: 未提供有效的認證令牌
- `403 Forbidden`: 用戶無管理員權限
- `404 Not Found`: 指定的用戶不存在
- `500 Internal Server Error`: 刪除過程中發生錯誤

### 5. 創建費用代碼

**端點**: `POST /api/admin/feecodies/{code}`

**功能描述**: 創建新的費用代碼，用於追蹤和管理費用相關功能。

**路徑參數**:
- `code`: String (必填) - 費用代碼

**請求標頭**:
- `Authorization`: Bearer {token} (必填)

**響應格式**: `text/plain`

**成功響應範例**:
```
FEE001
```

**可能的錯誤碼**:
- `401 Unauthorized`: 未提供有效的認證令牌
- `403 Forbidden`: 用戶無管理員權限
- `409 Conflict`: 費用代碼已存在
- `500 Internal Server Error`: 伺服器內部錯誤

### 6. 獲取所有費用代碼

**端點**: `GET /api/admin/feecodies`

**功能描述**: 獲取系統中所有的費用代碼列表。

**請求參數**: 無

**請求標頭**:
- `Authorization`: Bearer {token} (必填)

**響應格式**: `application/json`

**響應範例**:
```json
[
  "FEE001",
  "FEE002",
  "DISCOUNT10",
  "PREMIUM20"
]
```

**可能的錯誤碼**:
- `401 Unauthorized`: 未提供有效的認證令牌
- `403 Forbidden`: 用戶無管理員權限
- `500 Internal Server Error`: 伺服器內部錯誤

## 資料模型說明

### UserResponse

用戶響應物件，包含用戶的完整資訊：

```java
{
  Long userId           // 用戶ID
  String username       // 用戶名稱
  String email          // 電子郵件
  List<Role> roles      // 角色列表
  String userType       // 用戶類型 (CLIENT/PROVIDER)
  PersonProfile personProfile    // 個人資料
  BusinessProfile businessProfile // 商業資料
  String title          // 職稱
  String vedioUrl       // 影片URL
  String signatureUrl   // 簽名URL
  String content        // 內容描述
}
```

### AdminUserCreateRequest

創建用戶請求物件：

```java
{
  String username       // 用戶名稱 (3-45字元)
  String password       // 密碼 (最少8字元)
  String email          // 電子郵件 (最多100字元)
  UserType userType     // 用戶類型
  String region         // 地區代碼
  List<String> roleNames // 角色名稱列表
  List<RoleIndustryScopeRequest> roleIndustryScopes // 角色產業範圍
}
```

### RoleIndustryScopeRequest

角色產業範圍請求物件：

```java
{
  String roleName       // 角色名稱
  Boolean isAllIndustries // 是否管理所有產業
  List<Long> industryIds  // 特定產業ID列表
}
```

## 業務邏輯和限制說明

### 1. 角色產業範圍設定邏輯

- 當 `isAllIndustries` 為 `true` 時，該角色擁有管理所有產業的權限，`industryIds` 將被忽略
- 當 `isAllIndustries` 為 `false` 時，該角色只能管理 `industryIds` 中指定的產業
- 每個角色可以有不同的產業管理範圍設定

### 2. 用戶類型限制

- 用戶類型分為 `CLIENT`（客戶）和 `PROVIDER`（服務提供者）
- 創建用戶時如未指定類型，預設為 `CLIENT`
- 某些功能可能會根據用戶類型有不同的權限限制

### 3. 軟刪除機制

- 刪除用戶採用軟刪除方式，即將用戶的 `enabled` 狀態設為 `false`
- 被軟刪除的用戶資料仍保留在系統中，但無法登入或執行任何操作
- 管理員可透過更新用戶 API 重新啟用被刪除的用戶

### 4. 密碼安全要求

- 密碼最少需要8個字元
- 建議實施更複雜的密碼策略（如：必須包含大小寫字母、數字和特殊字元）
- 密碼應在儲存前進行加密處理

### 5. 費用代碼管理

- 費用代碼必須唯一，不可重複
- 創建費用代碼時會記錄創建者資訊
- 費用代碼一旦創建不可修改或刪除（根據目前API設計）

### 6. 權限控制

- 所有 Admin User Manager API 端點都需要管理員權限
- 需要在請求標頭中提供有效的 JWT 令牌
- 系統應驗證操作者是否有足夠權限執行特定操作

### 7. 資料驗證

- 所有輸入資料都需要進行驗證
- Email 必須符合標準格式
- 用戶名稱長度限制為3-45字元
- 地區代碼為必填欄位

### 8. 併發控制

- 更新用戶時應考慮併發情況
- 建議實施樂觀鎖或其他併發控制機制
- 避免多個管理員同時修改同一用戶造成資料不一致