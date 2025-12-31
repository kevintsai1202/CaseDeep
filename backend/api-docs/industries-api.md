# Industries API 文件

## 功能概述

Industries API 提供產業分類的完整管理功能，支援階層式產業結構、多語言內容管理，以及與訂單模板的關聯查詢。此 API 允許管理產業分類、設定產業翻譯、管理產業階層關係，並支援根據產業查詢相關的訂單模板。系統採用軟刪除機制，確保資料完整性。

## API 端點列表

| HTTP 方法 | 端點路徑 | 功能描述 |
|-----------|----------|----------|
| GET | `/api/industries` | 獲取產業列表（支援多語言） |
| GET | `/api/industries/{id}` | 獲取產業詳細資訊 |
| POST | `/api/industries` | 建立新產業 |
| PUT | `/api/industries/{id}` | 更新產業資訊 |
| DELETE | `/api/industries/{id}` | 刪除產業（軟刪除） |
| GET | `/api/industries/{id}/translations` | 獲取產業的所有語言翻譯 |
| GET | `/api/industries/type/{type}` | 根據類型獲取系統列表項目 |
| GET | `/api/industries/{parentIndustry}/ordertemplates` | 根據父產業查詢訂單模板 |
| GET | `/api/industries/{parentIndustry}/{childIndustry}/ordertemplates` | 根據產業階層查詢訂單模板 |
| GET | `/api/industries/{name}/subindustries` | 根據產業名稱獲取子產業 |

## 端點詳細說明

### 1. 獲取產業列表

**端點**: `GET /api/industries`

**功能描述**: 獲取系統中所有可用的產業列表，支援根據語言代碼返回本地化內容。

**請求參數**:

| 參數名稱 | 位置 | 資料類型 | 是否必填 | 說明 |
|----------|------|----------|----------|------|
| locale | Query | String | 否 | 語言代碼（如：en-US, zh-TW），未指定時返回預設語言 |

**請求標頭**:
- `Authorization`: Bearer {token} (選填)

**請求範例**:
```
GET /api/industries?locale=zh-TW
```

**響應格式**: `application/json`

**響應範例**:
```json
[
  {
    "name": "consulting",
    "title": "諮詢服務",
    "description": "提供專業諮詢服務",
    "icon": "/icons/consulting.png",
    "meta": "諮詢, 顧問, 專業服務",
    "urlPath": "/consulting",
    "translations": [
      {
        "lang": "en-US",
        "title": "Consulting Services",
        "meta": "consulting, advisory, professional services",
        "urlPath": "/consulting",
        "description": "Professional consulting services"
      }
    ]
  },
  {
    "name": "technology",
    "title": "科技",
    "description": "科技相關產業",
    "icon": "/icons/tech.png",
    "meta": "科技, IT, 軟體開發",
    "urlPath": "/technology",
    "translations": []
  }
]
```

**可能的錯誤碼**:
- `500 Internal Server Error`: 伺服器內部錯誤

### 2. 獲取產業詳細資訊

**端點**: `GET /api/industries/{id}`

**功能描述**: 根據產業ID獲取特定產業的詳細資訊，支援多語言。

**路徑參數**:
- `id`: Long (必填) - 產業ID

**請求參數**:

| 參數名稱 | 位置 | 資料類型 | 是否必填 | 說明 |
|----------|------|----------|----------|------|
| locale | Query | String | 否 | 語言代碼（如：en-US, zh-TW） |

**請求標頭**:
- `Authorization`: Bearer {token} (選填)

**請求範例**:
```
GET /api/industries/1?locale=en-US
```

**響應格式**: `application/json`

**響應範例**:
```json
{
  "name": "consulting",
  "title": "Consulting Services",
  "description": "Professional consulting and advisory services",
  "icon": "/icons/consulting.png",
  "meta": "consulting, advisory, professional services",
  "urlPath": "/consulting",
  "translations": [
    {
      "lang": "zh-TW",
      "title": "諮詢服務",
      "meta": "諮詢, 顧問, 專業服務",
      "urlPath": "/consulting",
      "description": "提供專業諮詢服務"
    },
    {
      "lang": "ja-JP",
      "title": "コンサルティング",
      "meta": "コンサルティング, アドバイザリー",
      "urlPath": "/consulting",
      "description": "プロフェッショナルコンサルティングサービス"
    }
  ]
}
```

**可能的錯誤碼**:
- `404 Not Found`: 指定的產業不存在
- `500 Internal Server Error`: 伺服器內部錯誤

### 3. 建立新產業

**端點**: `POST /api/industries`

**功能描述**: 建立新的產業分類，包含基本資訊和多語言翻譯。

**請求標頭**:
- `Authorization`: Bearer {token} (必填)
- `Content-Type`: application/json

**請求參數**:

| 參數名稱 | 資料類型 | 是否必填 | 驗證規則 | 說明 |
|----------|----------|----------|----------|------|
| name | String | 是 | 最多100字元，必須唯一 | 產業名稱（英文，系統識別碼） |
| title | String | 是 | 最多255字元 | 產業標題（顯示名稱） |
| description | String | 否 | - | 產業描述 |
| meta | String | 否 | 最多500字元 | SEO相關的元資訊 |
| urlPath | String | 是 | 最多255字元，必須唯一 | URL路徑 |
| icon | String | 否 | 最多255字元 | 圖示路徑 |
| parentId | Long | 否 | - | 父產業ID（用於建立階層結構） |
| translations | Array[Object] | 否 | - | 多語言翻譯列表 |

**translations 物件結構**:
```json
{
  "lang": "zh-TW",
  "title": "諮詢服務",
  "meta": "諮詢, 顧問",
  "description": "專業諮詢服務"
}
```

**請求範例**:
```json
{
  "name": "legal_services",
  "title": "Legal Services",
  "description": "Professional legal consultation and services",
  "meta": "legal, law, attorney, lawyer",
  "urlPath": "/legal-services",
  "icon": "/icons/legal.png",
  "parentId": 1,
  "translations": [
    {
      "lang": "zh-TW",
      "title": "法律服務",
      "meta": "法律, 律師, 諮詢",
      "description": "專業法律諮詢與服務"
    },
    {
      "lang": "ja-JP",
      "title": "法律サービス",
      "meta": "法律, 弁護士, 相談",
      "description": "専門的な法律相談とサービス"
    }
  ]
}
```

**響應格式**: `application/json`

**響應範例**:
```json
{
  "name": "legal_services",
  "title": "Legal Services",
  "description": "Professional legal consultation and services",
  "icon": "/icons/legal.png",
  "meta": "legal, law, attorney, lawyer",
  "urlPath": "/legal-services",
  "translations": [
    {
      "lang": "zh-TW",
      "title": "法律服務",
      "meta": "法律, 律師, 諮詢",
      "urlPath": "/legal-services",
      "description": "專業法律諮詢與服務"
    },
    {
      "lang": "ja-JP",
      "title": "法律サービス",
      "meta": "法律, 弁護士, 相談",
      "urlPath": "/legal-services",
      "description": "専門的な法律相談とサービス"
    }
  ]
}
```

**可能的錯誤碼**:
- `400 Bad Request`: 請求參數驗證失敗
- `401 Unauthorized`: 未提供有效的認證令牌
- `403 Forbidden`: 用戶無權限執行此操作
- `409 Conflict`: 產業名稱或URL路徑已存在
- `500 Internal Server Error`: 伺服器內部錯誤

### 4. 更新產業資訊

**端點**: `PUT /api/industries/{id}`

**功能描述**: 更新現有產業的資訊，包含基本資訊和多語言翻譯。

**路徑參數**:
- `id`: Long (必填) - 要更新的產業ID

**請求標頭**:
- `Authorization`: Bearer {token} (必填)
- `Content-Type`: application/json

**請求參數**: 與建立新產業相同，但所有欄位皆為選填（僅更新提供的欄位）

**請求範例**:
```json
{
  "title": "Legal & Compliance Services",
  "description": "Comprehensive legal and compliance services",
  "translations": [
    {
      "lang": "zh-TW",
      "title": "法律與合規服務",
      "description": "全方位法律與合規服務"
    }
  ]
}
```

**響應格式**: `application/json`

**響應範例**:
```json
{
  "name": "legal_services",
  "title": "Legal & Compliance Services",
  "description": "Comprehensive legal and compliance services",
  "icon": "/icons/legal.png",
  "meta": "legal, law, attorney, lawyer",
  "urlPath": "/legal-services",
  "translations": [
    {
      "lang": "zh-TW",
      "title": "法律與合規服務",
      "meta": "法律, 律師, 諮詢",
      "urlPath": "/legal-services",
      "description": "全方位法律與合規服務"
    }
  ]
}
```

**可能的錯誤碼**:
- `400 Bad Request`: 請求參數驗證失敗
- `401 Unauthorized`: 未提供有效的認證令牌
- `403 Forbidden`: 用戶無權限執行此操作
- `404 Not Found`: 指定的產業不存在
- `409 Conflict`: 更新的名稱或URL路徑與其他產業衝突
- `500 Internal Server Error`: 伺服器內部錯誤

### 5. 刪除產業

**端點**: `DELETE /api/industries/{id}`

**功能描述**: 軟刪除指定的產業（將 enabled 設為 false）。

**路徑參數**:
- `id`: Long (必填) - 要刪除的產業ID

**請求標頭**:
- `Authorization`: Bearer {token} (必填)

**響應格式**: 無內容

**成功響應**: `204 No Content`

**可能的錯誤碼**:
- `401 Unauthorized`: 未提供有效的認證令牌
- `403 Forbidden`: 用戶無權限執行此操作
- `404 Not Found`: 指定的產業不存在
- `409 Conflict`: 產業有關聯資料無法刪除
- `500 Internal Server Error`: 伺服器內部錯誤

### 6. 獲取產業的所有語言翻譯

**端點**: `GET /api/industries/{id}/translations`

**功能描述**: 獲取指定產業的所有語言版本翻譯。

**路徑參數**:
- `id`: Long (必填) - 產業ID

**請求標頭**:
- `Authorization`: Bearer {token} (選填)

**響應格式**: `application/json`

**響應範例**:
```json
[
  {
    "lang": "en-US",
    "title": "Legal Services",
    "meta": "legal, law, attorney",
    "urlPath": "/legal-services",
    "description": "Professional legal services"
  },
  {
    "lang": "zh-TW",
    "title": "法律服務",
    "meta": "法律, 律師, 諮詢",
    "urlPath": "/legal-services",
    "description": "專業法律服務"
  },
  {
    "lang": "ja-JP",
    "title": "法律サービス",
    "meta": "法律, 弁護士",
    "urlPath": "/legal-services",
    "description": "専門的な法律サービス"
  }
]
```

**可能的錯誤碼**:
- `404 Not Found`: 指定的產業不存在
- `500 Internal Server Error`: 伺服器內部錯誤

### 7. 根據類型獲取系統列表項目

**端點**: `GET /api/industries/type/{type}`

**功能描述**: 根據類型獲取系統列表項目名稱，通常用於獲取特定類型的訂單模板名稱。

**路徑參數**:
- `type`: String (必填) - 類型名稱（對應 SysListItem 的 type 欄位）

**請求標頭**:
- `Authorization`: Bearer {token} (選填)

**請求範例**:
```
GET /api/industries/type/consulting
```

**響應格式**: `application/json`

**響應範例**:
```json
[
  "Standard Consulting Template",
  "Premium Consulting Package",
  "Hourly Consultation",
  "Project-based Consulting"
]
```

**可能的錯誤碼**:
- `500 Internal Server Error`: 伺服器內部錯誤

### 8. 根據父產業查詢訂單模板

**端點**: `GET /api/industries/{parentIndustry}/ordertemplates`

**功能描述**: 根據父產業名稱或翻譯查詢相關的訂單模板，支援多語言查詢和分頁。

**路徑參數**:
- `parentIndustry`: String (必填) - 父產業名稱（可以是任何語言的名稱或翻譯）

**請求參數**:

| 參數名稱 | 位置 | 資料類型 | 是否必填 | 預設值 | 說明 |
|----------|------|----------|----------|--------|------|
| locale | Query | String | 否 | en-US | 響應資料的語言代碼 |
| region | Query | String | 否 | - | 地區篩選 |
| page | Query | Integer | 否 | 0 | 頁碼（從0開始） |
| size | Query | Integer | 否 | 20 | 每頁筆數 |

**請求標頭**:
- `Authorization`: Bearer {token} (選填)

**請求範例**:
```
GET /api/industries/consulting/ordertemplates?locale=zh-TW&page=0&size=10
```

**響應格式**: `application/json`

**響應範例**:
```json
[
  {
    "id": 1,
    "title": "標準諮詢服務",
    "description": "一般諮詢服務模板",
    "basePrice": 5000.00,
    "currency": "TWD",
    "deliveryDays": 7,
    "industryName": "consulting",
    "industryTitle": "諮詢服務"
  },
  {
    "id": 2,
    "title": "高級諮詢套餐",
    "description": "包含深度分析的諮詢服務",
    "basePrice": 15000.00,
    "currency": "TWD",
    "deliveryDays": 14,
    "industryName": "consulting",
    "industryTitle": "諮詢服務"
  }
]
```

**可能的錯誤碼**:
- `404 Not Found`: 指定的產業不存在
- `500 Internal Server Error`: 伺服器內部錯誤

### 9. 根據產業階層查詢訂單模板

**端點**: `GET /api/industries/{parentIndustry}/{childIndustry}/ordertemplates`

**功能描述**: 根據父產業和子產業的階層關係查詢訂單模板，支援多語言查詢和分頁。

**路徑參數**:
- `parentIndustry`: String (必填) - 父產業名稱（可以是任何語言）
- `childIndustry`: String (必填) - 子產業名稱（可以是任何語言）

**請求參數**:

| 參數名稱 | 位置 | 資料類型 | 是否必填 | 預設值 | 說明 |
|----------|------|----------|----------|--------|------|
| locale | Query | String | 否 | en-US | 響應資料的語言代碼 |
| region | Query | String | 否 | - | 地區篩選 |
| page | Query | Integer | 否 | 0 | 頁碼（從0開始） |
| size | Query | Integer | 否 | 20 | 每頁筆數 |

**請求標頭**:
- `Authorization`: Bearer {token} (選填)

**請求範例**:
```
GET /api/industries/consulting/legal_services/ordertemplates?locale=en-US&region=US
```

**響應格式**: `application/json`

**響應範例**:
```json
[
  {
    "id": 10,
    "title": "Legal Consultation Package",
    "description": "Professional legal consultation service",
    "basePrice": 300.00,
    "currency": "USD",
    "deliveryDays": 3,
    "parentIndustryName": "consulting",
    "parentIndustryTitle": "Consulting Services",
    "childIndustryName": "legal_services",
    "childIndustryTitle": "Legal Services"
  }
]
```

**可能的錯誤碼**:
- `404 Not Found`: 指定的產業階層不存在
- `500 Internal Server Error`: 伺服器內部錯誤

### 10. 根據產業名稱獲取子產業

**端點**: `GET /api/industries/{name}/subindustries`

**功能描述**: 根據產業名稱獲取其所有子產業列表，支援多語言查詢。可使用任何語言的產業名稱或翻譯進行查詢。

**路徑參數**:
- `name`: String (必填) - 父產業名稱（可以是任何語言的名稱或翻譯）

**請求參數**:

| 參數名稱 | 位置 | 資料類型 | 是否必填 | 預設值 | 說明 |
|----------|------|----------|----------|--------|------|
| locale | Query | String | 否 | en-US | 響應資料的語言代碼（如：en-US, zh-TW, ja-JP） |

**請求標頭**:
- `Authorization`: Bearer {token} (選填)

**請求範例**:
```
GET /api/industries/consulting/subindustries?locale=zh-TW
或
GET /api/industries/諮詢服務/subindustries?locale=zh-TW
```

**響應格式**: `application/json`

**響應範例**:
```json
[
  {
    "id": 2,
    "name": "legal_services",
    "title": "法律服務",
    "description": "專業法律諮詢與服務",
    "icon": "/icons/legal.png",
    "meta": "法律, 律師, 諮詢",
    "translations": [
      {
        "lang": "en-US",
        "title": "Legal Services",
        "meta": "legal, law, attorney",
        "description": "Professional legal services"
      },
      {
        "lang": "ja-JP",
        "title": "法律サービス",
        "meta": "法律, 弁護士",
        "description": "専門的な法律サービス"
      }
    ]
  },
  {
    "id": 3,
    "name": "financial_consulting",
    "title": "財務諮詢",
    "description": "財務規劃與投資諮詢服務",
    "icon": "/icons/finance.png",
    "meta": "財務, 投資, 理財",
    "translations": [
      {
        "lang": "en-US",
        "title": "Financial Consulting",
        "meta": "finance, investment, planning",
        "description": "Financial planning and investment advisory"
      }
    ]
  }
]
```

**可能的錯誤碼**:
- `404 Not Found`: 指定名稱的產業不存在
- `500 Internal Server Error`: 伺服器內部錯誤

**使用說明**:
1. 支援使用任何語言的產業名稱進行查詢，系統會自動識別
2. 響應內容會根據 locale 參數返回對應語言版本
3. 若指定的產業沒有子產業，將返回空陣列
4. 只返回啟用狀態（enabled=true）的子產業

## 資料模型說明

### IndustryResponse

產業響應物件，包含產業的完整資訊：

```java
{
  String name           // 產業名稱（系統識別碼）
  String title          // 產業標題（顯示名稱）
  String description    // 產業描述
  String icon           // 圖示路徑
  String meta           // SEO元資訊
  String urlPath        // URL路徑
  List<IndustryTranslationResponse> translations // 翻譯列表
}
```

### IndustryRequest

建立/更新產業請求物件：

```java
{
  String name           // 產業名稱 (最多100字元，必須唯一)
  String title          // 標題 (最多255字元)
  String description    // 描述
  String meta           // 元資訊 (最多500字元)
  String urlPath        // URL路徑 (最多255字元，必須唯一)
  String icon           // 圖示路徑 (最多255字元)
  Long parentId         // 父產業ID
  List<IndustryTranslationRequest> translations // 翻譯列表
}
```

### IndustryTranslationRequest

產業翻譯請求物件：

```java
{
  String lang           // 語言代碼 (最多10字元)
  String title          // 標題 (最多255字元)
  String meta           // 元資訊 (最多500字元)
  String description    // 描述
}
```

### IndustryTranslationResponse

產業翻譯響應物件：

```java
{
  String lang           // 語言代碼
  String title          // 標題
  String meta           // 元資訊
  String urlPath        // URL路徑
  String description    // 描述
}
```

## 業務邏輯和限制說明

### 1. 階層結構管理

- 產業支援多層階層結構，透過 `parentId` 建立父子關係
- 刪除父產業時需考慮子產業的處理
- 查詢時可透過階層關係篩選相關資料

### 2. 多語言支援

- 每個產業可以有多個語言版本的翻譯
- 主要內容使用英文（`name`, `title`, `description`）
- 透過 `translations` 提供其他語言版本
- 查詢時可透過 `locale` 參數指定返回的語言版本

### 3. 唯一性約束

- `name` 欄位必須全系統唯一（用作系統識別碼）
- `urlPath` 欄位必須全系統唯一（用於URL路由）
- 建立或更新時系統會驗證唯一性

### 4. 軟刪除機制

- 刪除操作採用軟刪除（設定 `enabled = false`）
- 已刪除的產業不會出現在查詢結果中
- 保留資料完整性，可追蹤歷史記錄

### 5. 訂單模板關聯

- 產業可關聯多個訂單模板
- 支援透過產業名稱（任何語言）查詢相關模板
- 支援階層式查詢（父產業/子產業）

### 6. 資料驗證規則

- `name`: 必填，最多100字元
- `title`: 必填，最多255字元
- `urlPath`: 必填，最多255字元
- `meta`: 選填，最多500字元
- `icon`: 選填，最多255字元
- 翻譯的語言代碼最多10字元

### 7. 查詢最佳化

- 列表查詢支援語言篩選，減少不必要的資料傳輸
- 分頁查詢支援，避免一次載入過多資料
- 支援多語言名稱查詢，提升使用便利性

### 8. 權限控制

- 查詢端點通常為公開或需要基本認證
- 建立、更新、刪除操作需要管理員權限
- 建議實施角色權限控制（RBAC）

### 9. 錯誤處理

- 所有端點都有完整的錯誤處理機制
- 返回適當的HTTP狀態碼和錯誤訊息
- 記錄詳細的錯誤日誌供除錯使用

### 10. 效能考量

- 使用快取機制提升查詢效能
- 翻譯資料採用延遲載入策略
- 大量資料查詢使用分頁機制