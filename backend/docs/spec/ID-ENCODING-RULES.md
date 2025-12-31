# ID 編碼規則文件

**文件版本：** 1.0.0  
**更新日期：** 2025/6/15  
**作者：** Kilo Code  

---

## 📋 編碼規則總覽

### 表格說明
| ID類型 | 前綴 | 格式規則 | 範例 | 用途說明 | 實作狀態 | 實作位置 |
|--------|------|----------|------|----------|----------|----------|
| **Order ID** | CO | `CO` + yyMM + 6位隨機數字 + Base62編碼 | `aBcD123XyZ` | 訂單編號（編碼後） | ✅已實作 | [`OrderServiceImpl.createOrder()`](src/main/java/com/casemgr/service/impl/OrderServiceImpl.java:217) |
| **Contract ID** | EC | `EC` + yyMM + 6位隨機數字 | `EC2501123456` | 合約編號 | ✅已實作 | [`OrderServiceImpl`](src/main/java/com/casemgr/service/impl/OrderServiceImpl.java:439,622) |
| **Certification ID** | CE | `CE` + yyMM + 6位隨機數字 | `CE2501234567` | 認證編號 | ✅已實作 | [`CertificationServiceImpl.createCertification()`](src/main/java/com/casemgr/service/impl/CertificationServiceImpl.java:66) |
| **Commission ID** | CO | `CO` + yyMM + 6位隨機數字 | `CO2501232305` | 佣金編號 | ✅已實作 | [`CommissionServiceImpl.createCommission()`](src/main/java/com/casemgr/service/impl/CommissionServiceImpl.java:121) |
| **Upgrade ID** | UP | 未定義 | `UP2501062582` | 升級記錄編號 | ❌未實作 | 資料庫有欄位但無生成邏輯 |
| **Invitation ID** | IN | 未定義 | `IN2501032340` | 邀請記錄編號 | ❌未實作 | 資料庫有欄位但無生成邏輯 |
| **Invitation Code** | IC | `IC` + yyMM + 6位隨機數字 | `IC2501789012` | 邀請碼 | ⚠️部分實作 | [`InvitationServiceImpl`](src/main/java/com/casemgr/service/impl/InvitationServiceImpl.java:39) |
| **User ID** | - | 自增主鍵 | `1234` | 用戶ID | ✅已實作 | JPA自動生成 |
| **其他實體ID** | - | 自增主鍵 | `5678` | 各實體主鍵 | ✅已實作 | JPA自動生成 |

---

## 🔧 詳細規則說明

### 1. 通用編碼格式（除Order ID外）
大部分業務ID採用以下格式：
```
{前綴}{年月}{6位隨機數}
```

**組成部分：**
- **前綴**：2個大寫字母，表示業務類型
- **年月**：yyMM格式（例如2025年1月為2501）
- **隨機數**：6位數字（000001-999999）

**實作方法：** [`NumberUtils.generateFormNumber(String prefix)`](src/main/java/com/casemgr/utils/NumberUtils.java:8)

### 2. Order ID 的特殊Base62編碼規則

Order ID 採用兩階段編碼：
1. **第一階段**：生成原始ID `CO + yyMM + 6位隨機數`
2. **第二階段**：使用Base62編碼轉換為較短的字串

**編碼流程：**
```java
String orderNo = NumberUtils.generateFormNumber("CO");  // 例如：CO2501123456
String encodedOrderNo = Base62Utils.encode(orderNo);    // 例如：aBcD123XyZ
```

**Base62字符集：** `0-9A-Za-z` (62個字符)

**實作方法：** [`Base62Utils`](src/main/java/com/casemgr/utils/Base62Utils.java)

### 3. Commission Order 的遞減佣金率規則

根據程式碼分析，Commission相關的佣金計算規則應該是：
- 訂單金額越高，佣金率可能遞減
- 具體規則需要查看業務邏輯文件

---

## 📊 現況分析

### ✅ 已實作的ID類型

1. **Order ID**
   - 生成位置：[`OrderServiceImpl.createOrder()`](src/main/java/com/casemgr/service/impl/OrderServiceImpl.java:217)
   - 特點：使用Base62編碼，增加安全性和簡潔性
   - 用途：對外顯示的訂單編號

2. **Contract ID**
   - 生成位置：[`OrderServiceImpl`](src/main/java/com/casemgr/service/impl/OrderServiceImpl.java:439,622)的多處
   - 格式：EC + yyMM + 6位隨機數
   - 用途：合約識別

3. **Certification ID**
   - 生成位置：[`CertificationServiceImpl.createCertification()`](src/main/java/com/casemgr/service/impl/CertificationServiceImpl.java:66)
   - 格式：CE + yyMM + 6位隨機數
   - 用途：認證申請識別

4. **Commission ID**
   - 生成位置：[`CommissionServiceImpl.createCommission()`](src/main/java/com/casemgr/service/impl/CommissionServiceImpl.java:121)
   - 格式：CO + yyMM + 6位隨機數
   - 用途：佣金記錄識別
   - ⚠️ 注意：與Order ID使用相同前綴"CO"，可能造成混淆

### ❌ 未實作的ID類型

1. **Upgrade ID**
   - 資料庫欄位：`UPGRADE_ID_STR`
   - 範例格式：`UP2501062582`

2. **Invitation ID**
   - 資料庫欄位：`INVITATION_ID_STR`
   - 範例格式：`IN2501032340`

### ⚠️ 存在的問題

1. **前綴衝突**
   - Commission ID 使用 "CO" 前綴，與 Order ID 相同
   - 可能造成業務混淆（此問題仍然存在）

2. **編碼不一致**
   - 只有 Order ID 使用 Base62 編碼
   - 其他ID直接使用原始格式

3. **隨機數碰撞風險**
   - 6位隨機數在高併發下可能重複
   - 建議加入時間戳或序列號

4. **未實作的ID生成**
   - 多個實體有ID字串欄位但無生成邏輯
   - 可能導致資料不完整

---

## 💡 改進建議

### 1. 實作缺失的ID生成邏輯

**Upgrade ID 生成**：
```java
// 在 UpgradeServiceImpl 中加入
Upgrade upgrade = new Upgrade();
upgrade.setUpgradeIdStr(NumberUtils.generateFormNumber("UP"));
```

**Invitation ID 生成**：
```java
// 在 InvitationServiceImpl 中加入
Invitation invitation = new Invitation();
invitation.setInvitationIdStr(NumberUtils.generateFormNumber("IN"));
```

### 2. 解決前綴衝突

將 Commission ID 的前綴從 "CO" 改為 "CM"，避免與 Order ID 混淆。（Commission ID 已實作，但仍使用 "CO" 前綴，建議在下個版本中改為 "CM"）

### 3. 增強隨機數生成

改進 [`NumberUtils.generateFormNumber()`](src/main/java/com/casemgr/utils/NumberUtils.java:8)：
```java
// 加入毫秒時間戳或使用 AtomicInteger 序列
String timestamp = String.valueOf(System.currentTimeMillis()).substring(7);
String formNumber = prefix + midfix + timestamp + randomThree;
```

### 4. 統一編碼策略

考慮是否所有對外顯示的ID都使用Base62編碼：
- 優點：更短、更安全
- 缺點：需要解碼才能查看原始資訊

### 5. 建立ID生成服務

建議建立統一的ID生成服務：
```java
@Service
public class IdGenerationService {
    public String generateBusinessId(BusinessType type) {
        // 統一的ID生成邏輯
    }
}
```

---

## 📝 實作優先級

1. **高優先級**（立即修復）
   - 修正 Commission ID 前綴衝突（建議將前綴從 "CO" 改為 "CM"）
   - 實作缺失的ID生成邏輯（Upgrade ID、Invitation ID）

2. **中優先級**（計劃改進）
   - 增強隨機數生成機制
   - 建立統一ID生成服務

3. **低優先級**（持續優化）
   - 評估是否統一使用Base62編碼
   - 性能優化和監控

---

**文件結束**  
*此文件將隨著系統發展持續更新*