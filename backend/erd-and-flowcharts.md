# CaseDeep ERD 與流程圖

> **文件版本**: 1.0  
> **建立日期**: 2025-12-31

---

## 1. 完整實體關係圖 (ERD)

### 1.1 核心業務實體

```mermaid
erDiagram
    %% ==================== 用戶模組 ====================
    User ||--o{ Role : "has (M:N)"
    User ||--|| PersonProfile : "has"
    User ||--|| BusinessProfile : "has"
    User ||--o| Account : "receivingAccount"
    User ||--o| Account : "paymentAccount"
    User ||--o| Favourite : "has"
    User ||--o{ FeeCode : "creates"
    User ||--o{ Showcase : "owns"
    User ||--o{ OrderTemplate : "provides"
    User ||--o{ PricePackage : "owns"
    User ||--o{ AdminRoleIndustryScope : "has"

    %% ==================== 訂單模組 ====================
    User ||--o{ Order : "client"
    User ||--o{ Order : "provider"
    Order }o--|| OrderTemplate : "from"
    Order }o--|| Industry : "belongs"
    Order ||--o{ Contract : "has"
    Order ||--o{ PaymentCard : "has"
    Order ||--o{ DeliveryItem : "has"
    Order ||--o{ Evaluate : "has"
    Order ||--o{ Block : "confirmations"
    Order ||--|| Commission : "generates"
    Order ||--|| RevenueShare : "generates"

    %% ==================== 模板模組 ====================
    OrderTemplate }o--|| Industry : "belongs"
    OrderTemplate }o--|| User : "provider"
    OrderTemplate ||--o{ Block : "has"
    OrderTemplate ||--o{ Discount : "has"
    OrderTemplate ||--o| Contract : "template"

    %% ==================== 產業模組 ====================
    Industry ||--o{ Industry : "parent-child"
    Industry ||--o{ OrderTemplate : "has"
    Industry ||--o{ Contract : "has"
    Industry ||--o{ RevenueShare : "has"

    %% ==================== 合約模組 ====================
    Contract }o--|| Order : "belongs"
    Contract }o--o| OrderTemplate : "from"
    Contract }o--o| Contract : "copyFrom"
    Contract ||--o{ Block : "has"
    Contract ||--o| Account : "receivingAccount"
    Contract ||--o| Settlement : "has"
    Contract }o--|| User : "provider"
    Contract }o--|| User : "client"

    %% ==================== 區塊與清單項目 ====================
    Block ||--o{ ListItem : "has"
    Block }o--o| OrderTemplate : "belongs"
    Block }o--o| Contract : "belongs"
    Block }o--o| Order : "confirmation"

    %% ==================== 認證與邀請 ====================
    User ||--o{ Certification : "applies"
    User ||--o{ Invitation : "invites"

    %% ==================== 實體定義 ====================
    User {
        Long uId PK
        String username
        String email
        String password
        UserType userType
        Boolean certified
        Double rankingScore
        String region
        Boolean enabled
    }

    Order {
        Long oId PK
        String orderNo UK
        String name
        OrderStatus status
        BigDecimal totalPrice
        Long providerId FK
        Long clientId FK
        Long industryId FK
    }

    OrderTemplate {
        Long otId PK
        String name
        String imageUrl
        List paymentMethods
        OrderTemplateSelectType deliveryType
        Integer businessDays
        Integer startingPrice
        Boolean skipContract
    }

    Contract {
        Long cId PK
        String contractNo UK
        String name
        BigDecimal contractPrice
        ContractStatus contractStatus
        Boolean clientSigned
        Boolean providerSigned
    }

    Industry {
        Long id PK
        String name UK
        String title
        Float revenueShareRate
        Long parentId FK
    }

    Commission {
        Long cId PK
        String commissionIdStr UK
        Double amount
        Double rate
        PaymentStatus paymentStatus
    }

    RevenueShare {
        Long id PK
        String revenueShareNo UK
        Float revenueShareRate
        BigDecimal orderAmount
        BigDecimal revenueShareAmount
        RevenueShareStatus status
    }

    PaymentCard {
        Long id PK
        BigDecimal amount
        PaymentStatus status
    }

    DeliveryItem {
        Long id PK
        String title
        DeliveryStatus status
    }

    Evaluate {
        Long id PK
        Integer rating
        String comment
        EvaluateType evaluateType
    }

    Block {
        Long bId PK
        BlockType blockType
        String title
    }

    ListItem {
        Long liId PK
        String title
        String content
        Boolean selected
    }
```

### 1.2 支援實體

```mermaid
erDiagram
    Role {
        Long id PK
        String roleName UK
        String description
    }

    PersonProfile {
        Long id PK
        String firstName
        String lastName
        String phone
    }

    BusinessProfile {
        Long id PK
        String companyName
        String taxId
        String address
    }

    Account {
        Long id PK
        String bankName
        String accountNumber
        String accountHolder
    }

    Showcase {
        Long id PK
        String title
        Long userId FK
    }

    PricePackage {
        Long id PK
        String name
        BigDecimal price
    }

    Certification {
        Long id PK
        String certNo
        CEStatus status
    }

    Invitation {
        Long id PK
        String code
        InvitationStatus status
    }

    Settlement {
        Long id PK
        BigDecimal amount
    }

    Discount {
        Long id PK
        DiscountType discountType
        Integer value
    }

    Currency {
        Long id PK
        String code
        String symbol
        String name
    }

    Location {
        Long id PK
        String name
        String country
    }

    FeeCode {
        Long fcId PK
        String feeCode UK
    }
```

---

## 2. 訂單狀態流程圖

### 2.1 主要狀態轉換

```mermaid
stateDiagram-v2
    [*] --> inquiry: 建立訂單

    inquiry --> quote_request: 客戶請求報價
    inquiry --> cancelled: 取消

    quote_request --> quote_sent: 服務商發送報價
    quote_request --> cancelled: 取消

    quote_sent --> quote_accept: 客戶接受報價
    quote_sent --> quote_request: 客戶拒絕/重新報價
    quote_sent --> cancelled: 取消

    quote_accept --> awaiting_payment: 進入付款階段
    quote_accept --> cancelled: 取消

    awaiting_payment --> in_progress: 付款完成
    awaiting_payment --> cancelled: 取消

    in_progress --> delivered: 服務商交付
    in_progress --> cancelled: 取消

    delivered --> in_revision: 客戶請求修改
    delivered --> completed: 客戶確認完成

    in_revision --> delivered: 修改完成重新交付
    in_revision --> cancelled: 取消

    completed --> [*]
    cancelled --> [*]
```

### 2.2 狀態說明表

| 狀態               | 中文名稱   | 說明                        |
| ------------------ | ---------- | --------------------------- |
| `inquiry`          | 詢價中     | 訂單初始狀態，等待客戶行動  |
| `quote_request`    | 請求報價   | 客戶已請求服務商報價        |
| `quote_sent`       | 報價已發送 | 服務商已發送報價            |
| `quote_accept`     | 報價已接受 | 客戶接受報價，準備簽約/付款 |
| `awaiting_payment` | 等待付款   | 合約已簽署，等待客戶付款    |
| `in_progress`      | 進行中     | 付款完成，服務進行中        |
| `delivered`        | 已交付     | 服務商已交付成果            |
| `in_revision`      | 修改中     | 客戶請求修改                |
| `completed`        | 已完成     | 訂單成功完成                |
| `cancelled`        | 已取消     | 訂單被取消                  |

---

## 3. 合約狀態流程

```mermaid
stateDiagram-v2
    [*] --> Active: 建立合約

    Active --> PendingChange: 請求變更
    Active --> Signed: 雙方簽署

    PendingChange --> Active: 變更被拒絕
    PendingChange --> Active: 變更被接受

    Signed --> [*]
```

| 狀態            | 說明             |
| --------------- | ---------------- |
| `Active`        | 合約有效，可編輯 |
| `PendingChange` | 有變更請求待審核 |
| `Signed`        | 雙方已簽署       |

---

## 4. 付款狀態流程

```mermaid
stateDiagram-v2
    [*] --> Pending: 建立付款項目

    Pending --> Paid: 客戶付款
    Paid --> Completed: 確認收款

    Completed --> [*]
```

---

## 5. 交付狀態流程

```mermaid
stateDiagram-v2
    [*] --> Pending: 建立交付項目

    Pending --> InProgress: 開始處理
    InProgress --> Delivered: 提交交付
    Delivered --> Accepted: 客戶驗收通過
    Delivered --> Revision: 客戶請求修改

    Revision --> InProgress: 重新處理

    Accepted --> [*]
```

---

## 6. 分潤計算流程

```mermaid
flowchart TD
    A[訂單完成] --> B[計算訂單總額]
    B --> C[取得產業分潤比例]
    C --> D[計算分潤金額]
    D --> E[建立 RevenueShare 記錄]
    E --> F{管理員確認}
    F -->|確認| G[標記為已支付]
    F -->|待處理| H[保持 Pending 狀態]
    G --> I[結束]
    H --> F
```

---

## 7. 資料表命名對照

| Entity 類別   | 資料表名稱       |
| ------------- | ---------------- |
| User          | T_USER           |
| Order         | T_ORDER          |
| OrderTemplate | T_ORDER_TEMPLATE |
| Contract      | T_CONTRACT       |
| Industry      | T_INDUSTRY       |
| Commission    | T_COMMISSION     |
| RevenueShare  | T_REVENUE_SHARE  |
| PaymentCard   | T_PAYMENT_CARD   |
| DeliveryItem  | T_DELIVERY_ITEM  |
| Evaluate      | T_EVALUATE       |
| Block         | T_BLOCK          |
| ListItem      | T_LIST_ITEM      |
| Role          | T_ROLE           |
| Certification | T_CERTIFICATION  |
| Invitation    | T_INVITATION     |

---

*此文檔提供系統完整的資料模型視覺化與狀態流程說明*
