# Case Manager AI Chat - API 清單 / API Inventory

## 專案概述 / Project Overview
- **專案名稱 / Project Name**: Case Deep API
- **版本 / Version**: 0.8.5
- **描述 / Description**: Case Deep API - 案件管理系統 / Case Management System
- **API文檔 / API Documentation**: 使用 Swagger/OpenAPI 3.0 / Using Swagger/OpenAPI 3.0
- **認證方式 / Authentication**: JWT Bearer Token

## API文檔配置 / API Documentation Configuration
- **Swagger UI**: `/swagger-ui/index.html`
- **OpenAPI JSON**: `/v3/api-docs`
- **安全配置 / Security Configuration**: JWT Bearer Authentication

## Controller 統計 / Controller Statistics
- **總計Controller數量 / Total Controllers**: 30個 / 30 controllers
- **總計API端點數量 / Total API Endpoints**: 約200+個 / Approximately 200+ endpoints
- **主要功能模組 / Main Functional Modules**: 用戶管理、訂單管理、支付管理、認證管理、行業管理等 / User Management, Order Management, Payment Management, Authentication Management, Industry Management, etc.

---

## API 端點清單 / API Endpoints List

### 1. 認證管理 / Authentication Management (AuthController)
**基礎路徑 / Base Path**: `/api/auth`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| POST | `/login` | 用戶登入 / User Login | username, password |
| POST | `/register` | 用戶註冊 / User Registration | 註冊資訊 / Registration Info |
| POST | `/forgetpassword` | 忘記密碼 / Forgot Password | email |
| POST | `/resetpassword` | 重設密碼 / Reset Password | token, newPassword |

### 2. 用戶資料管理 / User Data Management (UserDataController)
**基礎路徑 / Base Path**: `/api/profile`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 取得當前用戶資料 / Get Current User Profile | - |
| GET | `/roles` | 取得用戶角色 / Get User Roles | - |
| GET | `/intro/{uId}` | 取得用戶介紹 / Get User Introduction | uId |
| PUT | `/changepassword` | 變更密碼 / Change Password | username, password |
| PUT | `/changeusertype` | 變更用戶類型 / Change User Type | userType |
| PUT | `/personal` | 更新個人資料 / Update Personal Information | 個人資料 / Personal Data |
| PUT | `/business` | 更新商業資料 / Update Business Information | 商業資料 / Business Data |
| POST | `/intro/title` | 更新介紹標題 / Update Introduction Title | title |
| POST | `/intro/content` | 更新介紹內容 / Update Introduction Content | content |
| POST | `/intro/video` | 上傳介紹影片 / Upload Introduction Video | file |
| POST | `/intro/videourl` | 設定影片URL / Set Video URL | fileUrl |
| POST | `/intro/signature` | 上傳數位簽名 / Upload Digital Signature | file |
| POST | `/intro/paymentaccount` | 更新付款帳戶 / Update Payment Account | account |
| POST | `/intro/receivingaccount` | 更新收款帳戶 / Update Receiving Account | account |

### 3. 訂單管理 / Order Management (OrderController)
**基礎路徑 / Base Path**: `/api/orders`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| POST | `/` | 從模板建立訂單 / Create Order from Template | orderReq |
| GET | `/myclientorders` | 取得我的客戶訂單 / Get My Client Orders | - |
| GET | `/myproviderorders/all` | 取得我的服務商訂單 / Get My Provider Orders | - |
| GET | `/myproviderorders/{clientId}` | 依客戶取得服務商訂單 / Get Provider Orders by Client | clientId |
| GET | `/{base62no}` | 依訂單號取得訂單 / Get Order by Number | base62no |
| GET | `/` | 列出所有訂單 / List All Orders | - |
| PATCH | `/{oId}/status` | 更新訂單狀態 / Update Order Status | oId, statusRequest |
| PATCH | `/confirmation/{bid}/select/{iid}` | 選擇確認項目 / Select Confirmation Item | bid, iid |
| PATCH | `/confirmation/{bid}/text` | 更新確認文字 / Update Confirmation Text | bid, updateText |
| PATCH | `/{oid}/confirmation/check` | 檢查確認完成 / Check Confirmation Complete | oid |
| POST | `/{base62no}/sendquote` | 發送報價 / Send Quote | base62no, quoteRequest |
| POST | `/{base62no}/requestquote` | 請求報價 / Request Quote | base62no |
| POST | `/{base62no}/rejectquote` | 拒絕報價 / Reject Quote | base62no |
| POST | `/{base62no}/acceptquote` | 接受報價 / Accept Quote | base62no |
| PATCH | `/{base62no}/complete` | 完成訂單 / Complete Order | base62no |
| PATCH | `/{base62no}/cancel` | 取消訂單 / Cancel Order | base62no |
| PATCH | `/{oId}/price` | 更新訂單價格 / Update Order Price | oId, priceRequest |
| DELETE | `/{oId}` | 刪除訂單 / Delete Order | oId |

#### 合約相關端點 / Contract Related Endpoints
| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| POST | `/contract/{cId}/clientsign` | 客戶簽署合約 / Client Sign Contract | cId |
| POST | `/contract/{cId}/providersign` | 服務商簽署合約 / Provider Sign Contract | cId |
| POST | `/{oId}/contract/request-change` | 請求合約變更 / Request Contract Change | oId, changeRequest |
| POST | `/{oId}/contract/{cId}/approve` | 批准合約變更 / Approve Contract Change | oId, cId |
| POST | `/{oId}/contract/{cId}/reject` | 拒絕合約變更 / Reject Contract Change | oId, cId |

#### 付款相關端點 / Payment Related Endpoints
| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| PATCH | `/payment/{pId}/pay` | 處理付款 / Process Payment | pId |
| PATCH | `/payment/{pId}/complete` | 完成付款 / Complete Payment | pId |
| POST | `/payment/{pId}/receipt` | 上傳付款收據 / Upload Payment Receipt | pId, file |
| POST | `/payment/{pId}/invoice` | 上傳發票 / Upload Invoice | pId, file |
| POST | `/{base62no}/payment` | 新增付款卡 / Add Payment Card | base62no, cardRequest |

#### 交付相關端點 / Delivery Related Endpoints
| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/{base62no}/deliveries` | 取得交付項目 / Get Delivery Items | base62no |
| GET | `/deliveries/{diId}` | 取得交付項目詳情 / Get Delivery Item Details | diId |
| POST | `/{base62no}/deliveries` | 新增交付項目 / Add Delivery Item | base62no, itemRequest |

### 4. 訂單模板管理 / Order Template Management (OrderTemplateController)
**基礎路徑 / Base Path**: `/api/ordertemplates`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/me` | 取得我的訂單模板 / Get My Order Templates | - |
| GET | `/` | 取得所有訂單模板(管理員) / Get All Order Templates (Admin) | - |
| GET | `/provider/{uid}` | 取得服務商的模板 / Get Provider Templates | uid |
| POST | `/` | 建立訂單模板 / Create Order Template | orderReq |
| GET | `/{oId}` | 取得模板詳情 / Get Template Details | oId |
| DELETE | `/{oId}` | 刪除訂單模板 / Delete Order Template | oId |
| PATCH | `/{oId}/image` | 更新模板圖片 / Update Template Image | oId, file |
| PATCH | `/{oId}/imageurl` | 更新模板圖片URL / Update Template Image URL | oId, urlReq |
| PATCH | `/{oId}/name` | 更新模板名稱 / Update Template Name | oId, orderRequest |
| PATCH | `/{oId}/paymentmethod` | 更新付款方式 / Update Payment Method | oId, paymentReq |
| PATCH | `/{oId}/deliverytype` | 更新交付類型 / Update Delivery Type | oId, deliveryDatesReq |
| PATCH | `/{oId}/hasdescref` | 更新描述需求 / Update Description Requirement | oId, descRefReq |
| PATCH | `/{oId}/startingprice` | 更新起始價格 / Update Starting Price | oId, startingPriceReq |
| POST | `/{oId}/discount` | 新增折扣 / Add Discount | oId, discountReq |
| PUT | `/{oId}/discounts/{dId}` | 更新折扣 / Update Discount | oId, dId, discountReq |
| POST | `/{oId}/input` | 新增輸入區塊 / Add Input Block | oId, inputBlockReq |
| POST | `/{otId}/contractitem` | 新增合約項目 / Add Contract Item | otId, inputBlockReq |
| PUT | `/contractitem/{bid}` | 更新合約項目 / Update Contract Item | bid, inputBlockReq |
| DELETE | `/contractitem/{bid}` | 刪除合約項目 / Delete Contract Item | bid |
| POST | `/{oId}/option` | 新增選項區塊 / Add Option Block | oId, optionBlockReq |
| PUT | `/input/{bId}` | 更新輸入區塊 / Update Input Block | bId, inputBlockReq |
| PUT | `/option/{bId}` | 更新選項區塊 / Update Option Block | bId, optionBlockReq |
| POST | `/option/{bId}` | 新增選項清單項目 / Add Option List Item | bId, listItemRequest |
| PUT | `/option/{bId}/listitems/{liId}` | 更新清單項目 / Update List Item | bId, liId, listItemRequest |
| PATCH | `/{otId}/skipcontract` | 設定跳過合約 / Set Skip Contract | otId, skipReq |
| DELETE | `/discounts/{dId}` | 刪除折扣 / Delete Discount | dId |
| DELETE | `/blocks/{bId}` | 刪除區塊 / Delete Block | bId |
| DELETE | `/listitems/{liId}` | 刪除清單項目 / Delete List Item | liId |

### 5. 行業管理 / Industry Management (IndustryController)
**基礎路徑 / Base Path**: `/api/industries`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 取得行業清單 / Get Industry List | - |
| GET | `/{id}` | 取得行業詳情 / Get Industry Details | id |
| POST | `/` | 建立新行業 / Create New Industry | industryRequest |
| PUT | `/{id}` | 更新行業 / Update Industry | id, industryRequest |
| DELETE | `/{id}` | 刪除行業 / Delete Industry | id |
| GET | `/type/{type}` | 依類型取得系統清單項目 / Get System List Items by Type | type |
| GET | `/{parentIndustry}/ordertemplates` | 取得父行業的訂單模板 / Get Order Templates by Parent Industry | parentIndustry, region, page, size |
| GET | `/{parentIndustry}/{childIndustry}/ordertemplates` | 取得行業階層的訂單模板 / Get Order Templates by Industry Hierarchy | parentIndustry, childIndustry, region, page, size |
| GET | `/{name}/subindustries` | 取得子行業 / Get Sub Industries | name |

### 6. 價格套餐管理 / Price Package Management (PricePackageController)
**基礎路徑 / Base Path**: `/api/pricepackages`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/provider/{userId}` | 取得服務商的價格套餐 / Get Provider Price Packages | userId |
| GET | `/me` | 取得我的價格套餐 / Get My Price Packages | - |
| POST | `/` | 建立價格套餐 / Create Price Package | req |
| PUT | `/image/{priceId}` | 上傳套餐圖片 / Upload Package Image | priceId, file |
| PUT | `/itemlists/{itemId}` | 更新套餐項目 / Update Package Item | itemId, itemReq |
| DELETE | `/{priceId}` | 刪除價格套餐 / Delete Price Package | priceId |
| DELETE | `/itemlists/{itemId}` | 刪除套餐項目 / Delete Package Item | itemId |
| POST | `/itemlists/{priceId}` | 新增套餐項目 / Add Package Item | priceId, itemReq |
| PUT | `/{priceId}` | 連結到訂單模板 / Link to Order Template | priceId, priceReq |

### 7. 作品展示管理 / Showcase Management (ShowcaseController)
**基礎路徑 / Base Path**: `/api/showcase`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| POST | `/` | 建立作品展示 / Create Showcase | showcaseRequest |
| POST | `/{sId}` | 上傳展示檔案 / Upload Showcase File | sId, file |
| POST | `/filedata/{fId}/change` | 替換展示檔案 / Replace Showcase File | fId, file |
| PUT | `/{sId}/{oId}` | 連結到訂單模板 / Link to Order Template | sId, oId |
| GET | `/me` | 取得我的作品展示 / Get My Showcases | - |
| GET | `/provider/{uId}` | 取得服務商的作品展示 / Get Provider Showcases | uId |

### 8. 用戶聊天管理 / User Chat Management (UserChatController)
**基礎路徑 / Base Path**: `/api/chat`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/messages/{senderId}/{receiverId}` | 取得聊天訊息歷史 / Get Chat Message History | senderId, receiverId |
| GET | `/chatuser/{uId}` | 取得聊天用戶資訊 / Get Chat User Information | uId |
| GET | `/chatuser/online` | 取得線上用戶 / Get Online Users | - |
| GET | `/chatuser/offline` | 取得離線用戶 / Get Offline Users | - |
| GET | `/chatuser/alluser` | 取得所有聊天用戶 / Get All Chat Users | - |

#### WebSocket 端點 / WebSocket Endpoints
| 端點 / Endpoint | 功能描述 / Description |
|------|---------|
| `/app/connect` | 用戶連線 / User Connect |
| `/app/disconnect` | 用戶斷線 / User Disconnect |
| `/app/chat/message` | 發送聊天訊息 / Send Chat Message |
| `/app/candidate` | 發送WebRTC ICE候選 / Send WebRTC ICE Candidate |
| `/app/video/offer` | 發送視訊通話邀請 / Send Video Call Offer |
| `/app/video/answer` | 回應視訊通話 / Answer Video Call |

### 9. 認證管理 / Certification Management (CertificationController)
**基礎路徑 / Base Path**: `/api/certifications`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| POST | `/` | 申請認證 / Apply for Certification | certificationRequest |
| GET | `/{cenobase62}` | 取得認證詳情 / Get Certification Details | cenobase62 |
| PATCH | `/{cenobase62}/submit` | 提交認證申請 / Submit Certification Application | cenobase62 |

### 10. 評價管理 / Evaluation Management (EvaluateController)
**基礎路徑 / Base Path**: `/api/evaluations`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| POST | `/` | 建立評價 / Create Evaluation | evaluationRequest |
| GET | `/{evaluationId}` | 取得評價詳情 / Get Evaluation Details | evaluationId |
| PUT | `/{evaluationId}` | 更新評價 / Update Evaluation | evaluationId, evaluationRequest |
| DELETE | `/{evaluationId}` | 刪除評價 / Delete Evaluation | evaluationId |
| GET | `/order/{orderId}` | 取得訂單評價 / Get Order Evaluations | orderId |
| GET | `/provider/{providerId}` | 取得服務商評價 / Get Provider Evaluations | providerId |
| GET | `/client/{clientId}` | 取得客戶評價 / Get Client Evaluations | clientId |

### 11. 收藏管理 / Favourite Management (FavouriteController)
**基礎路徑 / Base Path**: `/api/favourites`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| POST | `/` | 新增收藏 / Add Favourite | favouriteRequest |
| GET | `/` | 取得我的收藏 / Get My Favourites | - |
| DELETE | `/{favouriteId}` | 刪除收藏 / Delete Favourite | favouriteId |

### 12. 檔案上傳管理 / File Upload Management (FileUploadController)
**基礎路徑 / Base Path**: `/api/files`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| POST | `/upload` | 上傳檔案 / Upload File | file |
| GET | `/{filename:.+}` | 下載檔案 / Download File | filename |
| DELETE | `/{uuid}` | 刪除檔案 / Delete File | uuid |

### 13. 清單項目管理 / List Item Management (ListItemController)
**基礎路徑 / Base Path**: `/api/listitems`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 取得所有清單項目 / Get All List Items | - |
| GET | `/blocks/{bId}` | 取得區塊的清單項目 / Get Block List Items | bId |
| POST | `/blocks/{bId}` | 為區塊建立清單項目 / Create List Item for Block | bId, listItemRequest |
| POST | `/blocks/{bId}/batch` | 批次建立清單項目 / Batch Create List Items | bId, listItemRequests |
| PUT | `/{liId}` | 更新清單項目 / Update List Item | liId, listItemRequest |

### 14. 地點管理 / Location Management (LocationController)
**基礎路徑 / Base Path**: `/api/admin/locations`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 取得所有地點 / Get All Locations | - |
| GET | `/{id}` | 取得地點詳情 / Get Location Details | id |
| POST | `/` | 建立新地點 / Create New Location | locationRequest |
| PUT | `/{id}` | 更新地點 / Update Location | id, locationRequest |
| DELETE | `/{id}` | 刪除地點 / Delete Location | id |
| GET | `/search/{location}` | 依名稱搜尋地點 / Search Location by Name | location |

### 15. 排名管理 / Ranking Management (RankingController)
**基礎路徑 / Base Path**: `/api/ranking`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/list` | 取得用戶排名清單 / Get User Ranking List | industryId, subIndustryId, location, page, size |
| POST | `/updateScore` | 更新用戶排名分數(管理員) / Update User Ranking Score (Admin) | request |

### 16. 營收分潤管理 / Revenue Share Management (RevenueShareController)
**基礎路徑 / Base Path**: `/api/revenue-shares`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 列出營收分潤 / List Revenue Shares | page, size, clientId, providerId |
| GET | `/{id}` | 取得營收分潤詳情 / Get Revenue Share Details | id |
| GET | `/number/{revenueShareNo}` | 依編號取得營收分潤 / Get Revenue Share by Number | revenueShareNo |
| GET | `/order/{orderId}` | 取得訂單的營收分潤 / Get Order Revenue Share | orderId |
| PUT | `/{id}/status` | 更新營收分潤狀態 / Update Revenue Share Status | id, request |
| GET | `/stats` | 取得營收分潤統計 / Get Revenue Share Statistics | clientId, providerId |
| GET | `/client/{clientId}` | 取得客戶的營收分潤 / Get Client Revenue Shares | clientId |
| GET | `/provider/{providerId}` | 取得服務商的營收分潤 / Get Provider Revenue Shares | providerId |

### 17. 角色管理 / Role Management (RoleController)
**基礎路徑 / Base Path**: `/api/admin/roles`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 取得所有角色 / Get All Roles | - |
| GET | `/{id}` | 取得角色詳情 / Get Role Details | id |
| POST | `/` | 建立新角色 / Create New Role | request |
| PUT | `/{id}` | 更新角色 / Update Role | id, request |
| DELETE | `/{id}` | 刪除角色 / Delete Role | id |

### 18. 結算管理 / Settlement Management (SettlementController)
**基礎路徑 / Base Path**: `/api/settlements`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| - | - | 尚未實作 / Not Implemented Yet | - |

### 19. 系統清單項目管理 / System List Item Management (SysListItemController)
**基礎路徑 / Base Path**: `/api/systemlists`, `/api/typies`, `/api/admin/systemlists`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/api/systemlists` | 取得所有系統清單項目 / Get All System List Items | - |
| GET | `/api/systemlists/{id}` | 取得系統清單項目詳情 / Get System List Item Details | id |
| GET | `/api/typies/{type}` | 依類型取得系統清單項目 / Get System List Items by Type | type |
| POST | `/api/admin/systemlists` | 建立系統清單項目(管理員) / Create System List Item (Admin) | sysListItemRequest |
| PUT | `/api/admin/systemlists/{id}` | 更新系統清單項目(管理員) / Update System List Item (Admin) | id, sysListItemRequest |

### 20. 精選推薦管理 / Top Pick Management (ToppickController)
**基礎路徑 / Base Path**: `/api/toppicks`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/{industry}` | 依行業取得精選推薦 / Get Top Picks by Industry | industry |
| GET | `/{industry}/{ordertemplatename}` | 依行業和模板取得精選推薦 / Get Top Picks by Industry and Template | industry, ordertemplatename |
| GET | `/{industry}/locations/{location}` | 依行業和地點取得精選推薦 / Get Top Picks by Industry and Location | industry, location |
| GET | `/{industry}/{ordertemplatename}/locations/{location}` | 依行業、模板和地點取得精選推薦 / Get Top Picks by Industry, Template and Location | industry, ordertemplatename, location |

### 21. 區塊管理 / Block Management (BlockController)
**基礎路徑 / Base Path**: `/api/blocks`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/{bId}` | 取得區塊詳情 / Get Block Details | bId |
| PUT | `/{bId}` | 更新區塊 / Update Block | bId, blockRequest |
| DELETE | `/{bId}` | 刪除區塊 / Delete Block | bId |

### 22. 佣金管理 / Commission Management (CommissionController)
**基礎路徑 / Base Path**: `/api/commissions`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 取得佣金清單 / Get Commission List | page, size |
| GET | `/{id}` | 取得佣金詳情 / Get Commission Details | id |
| GET | `/user/{userId}` | 取得用戶佣金 / Get User Commissions | userId |

### 23. 貨幣管理 / Currency Management (CurrencyController)
**基礎路徑 / Base Path**: `/api/currencies`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 取得所有貨幣 / Get All Currencies | - |
| GET | `/{id}` | 取得貨幣詳情 / Get Currency Details | id |
| POST | `/` | 建立新貨幣 / Create New Currency | currencyRequest |
| PUT | `/{id}` | 更新貨幣 / Update Currency | id, currencyRequest |
| DELETE | `/{id}` | 刪除貨幣 / Delete Currency | id |

---

## 管理員API端點 / Admin API Endpoints

### 1. 管理員認證管理 / Admin Certification Management (AdminCertificationController)
**基礎路徑 / Base Path**: `/api/admin/certifications`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 列出認證申請 / List Certification Applications | page, size |
| PUT | `/{certificationId}` | 更新認證狀態 / Update Certification Status | certificationId, request |

### 2. 管理員佣金管理 / Admin Commission Management (AdminCommissionController)
**基礎路徑 / Base Path**: `/api/admin/commissions`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 列出所有佣金 / List All Commissions | page, size |
| GET | `/{id}` | 取得佣金詳情 / Get Commission Details | id |
| PUT | `/{id}/status` | 更新佣金狀態 / Update Commission Status | id, request |
| GET | `/stats` | 取得佣金統計 / Get Commission Statistics | startDate, endDate |

### 3. 管理員邀請管理 / Admin Invitation Management (AdminInvitationController)
**基礎路徑 / Base Path**: `/api/admin/invitations`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 列出所有邀請 / List All Invitations | page, size |
| GET | `/{id}` | 取得邀請詳情 / Get Invitation Details | id |
| POST | `/` | 建立新邀請 / Create New Invitation | request |
| PUT | `/{id}/status` | 更新邀請狀態 / Update Invitation Status | id, request |
| DELETE | `/{id}` | 刪除邀請 / Delete Invitation | id |

### 4. 管理員訂單管理 / Admin Order Management (AdminOrderController)
**基礎路徑 / Base Path**: `/api/admin/orders`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 列出所有訂單 / List All Orders | page, size, status, startDate, endDate |
| GET | `/{id}` | 取得訂單詳情 / Get Order Details | id |
| PUT | `/{id}/status` | 更新訂單狀態 / Update Order Status | id, request |
| GET | `/stats` | 取得訂單統計 / Get Order Statistics | startDate, endDate |

### 5. 管理員營收分潤管理 / Admin Revenue Share Management (AdminRevenueShareController)
**基礎路徑 / Base Path**: `/api/admin/revenue-shares`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 列出所有營收分潤 / List All Revenue Shares | page, size |
| GET | `/{id}` | 取得營收分潤詳情 / Get Revenue Share Details | id |
| PUT | `/{id}/status` | 更新營收分潤狀態 / Update Revenue Share Status | id, request |
| GET | `/stats` | 取得營收分潤統計 / Get Revenue Share Statistics | startDate, endDate |

### 6. 管理員升級管理 / Admin Upgrade Management (AdminUpgradeController)
**基礎路徑 / Base Path**: `/api/admin/upgrades`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 列出所有升級請求 / List All Upgrade Requests | page, size |
| GET | `/{id}` | 取得升級請求詳情 / Get Upgrade Request Details | id |
| PUT | `/{id}/approve` | 批准升級請求 / Approve Upgrade Request | id |
| PUT | `/{id}/reject` | 拒絕升級請求 / Reject Upgrade Request | id, reason |

### 7. 管理員用戶管理 / Admin User Management (AdminUserController)
**基礎路徑 / Base Path**: `/api/admin/users`

| HTTP方法 / HTTP Method | 端點路徑 / Endpoint Path | 功能描述 / Description | 參數 / Parameters |
|---------|---------|---------|------|
| GET | `/` | 列出所有用戶 / List All Users | page, size, userType, status |
| GET | `/{id}` | 取得用戶詳情 / Get User Details | id |
| PUT | `/{id}/status` | 更新用戶狀態 / Update User Status | id, request |
| GET | `/stats` | 取得用戶統計 / Get User Statistics | - |
| POST | `/search` | 搜尋用戶 / Search Users | searchRequest |

---

## 安全配置 / Security Configuration

### 認證方式 / Authentication Methods
- **JWT Bearer Token**: 所有需要認證的端點都需要在Header中提供 `Authorization: Bearer <token>` / All authenticated endpoints require `Authorization: Bearer <token>` in header

### 權限控制 / Access Control
- **ROLE_ADMIN**: 管理員角色，可存取所有管理員端點 / Admin role, can access all admin endpoints
- **ROLE_MANAGER_ORDER**: 訂單管理員角色 / Order manager role
- **ROLE_USER**: 一般用戶角色 / Regular user role

### 公開端點 / Public Endpoints
以下端點不需要認證 / The following endpoints do not require authentication：
- `/api/auth/**` - 認證相關端點 / Authentication related endpoints
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - OpenAPI文檔 / OpenAPI documentation
- `/api/systemlists` - 系統清單查詢 / System list queries
- `/api/typies/**` - 系統類型查詢 / System type queries

---

## 資料格式 / Data Formats

### 請求格式 / Request Formats
- **Content-Type**: `application/json`
- **檔案上傳 / File Upload**: `multipart/form-data`

### 回應格式 / Response Formats
- **成功回應 / Success Response**: HTTP 200/201 + JSON資料 / JSON data
- **錯誤回應 / Error Response**: HTTP 4xx/5xx + 錯誤訊息 / Error message

### 分頁格式 / Pagination Format
```json
{
  "content": [...],
  "pageable": {
    "page": 0,
    "size": 20,
    "sort": "id,desc"
  },
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

---

## 錯誤代碼 / Error Codes

| HTTP狀態碼 / HTTP Status | 錯誤類型 / Error Type | 描述 / Description |
|---------|---------|---------|
| 400 | Bad Request | 請求參數錯誤 / Invalid request parameters |
| 401 | Unauthorized | 未認證或認證失效 / Unauthenticated or invalid authentication |
| 403 | Forbidden | 權限不足 / Insufficient permissions |
| 404 | Not Found | 資源不存在 / Resource not found |
| 409 | Conflict | 資源衝突 / Resource conflict |
| 500 | Internal Server Error | 伺服器內部錯誤 / Internal server error |

---

## API使用範例 / API Usage Examples

### 用戶登入 / User Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "password123"
}
```

### 取得用戶資料 / Get User Profile
```bash
GET /api/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 建立訂單 / Create Order
```bash
POST /api/orders
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "templateId": 123,
  "clientRequirements": "需要專業的網站設計服務",
  "budget": 50000
}
```

### 上傳檔案 / Upload File
```bash
POST /api/files/upload
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: multipart/form-data

file: [binary data]
```

---

## 開發注意事項 / Development Notes

### 版本控制 / Version Control
- API版本: v0.8.5
- 向後相容性: 主要版本更新時可能會有破壞性變更 / Backward compatibility: Breaking changes may occur in major version updates

### 效能考量 / Performance Considerations
- 分頁查詢建議使用適當的頁面大小 (建議20-50筆) / Use appropriate page sizes for pagination queries (recommended 20-50 items)
- 檔案上傳限制: 單檔最大50MB / File upload limit: 50MB per file
- API請求頻率限制: 每分鐘最多1000次請求 / API rate limit: Maximum 1000 requests per minute

### 測試環境 / Testing Environment
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- API文檔: `http://localhost:8080/v3/api-docs`

---

## 更新歷史 / Update History

| 版本 / Version | 日期 / Date | 更新內容 / Updates |
|---------|---------|---------|
| 0.8.5 | 2025-01-10 | 完整API清單建立 / Complete API inventory created |

---

*此文檔最後更新時間: 2025年1月10日 / Last updated: January 10, 2025*