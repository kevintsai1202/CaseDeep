# Admin API 測試計畫（完整測試數據＋自動化）

> 目標：針對後台 Admin API 制定可執行的端到端測試計畫，涵蓋測試數據、授權/權限、成功與錯誤路徑、以及以 PowerShell 驅動的 Postman 自動化流程與報告。

## 範圍與來源
- 範圍：`/api/admin/**` 下的 Users/Orders/Commissions/RevenueShares/Upgrades/Certifications/Invitations/Roles/Currencies/SystemLists。
- 程式來源：`src/main/java/com/casemgr/controller/` 內 `Admin*Controller`、`RoleController`、`CurrencyController`、`SysListItemController`。
- 參考文件：`api-docs/admin-user-manager-api.md`、`spec.md`、`api.md`。

## 測試前置與權限
- 作業系統：Windows + PowerShell。
- 認證：以 `/api/auth/login` 取得 JWT（預設使用 `admin` 使用者）。
- 權限：`hasRole('ADMIN')` 與各模組專屬權限：
  - `ROLE_ORDER_MANAGE`、`ROLE_COMMISSION_MANAGE`、`ROLE_UPGRADE_MANAGE`、`ROLE_CERTIFICATION_MANAGE`、`ROLE_INVITE_MANAGE`。
- 測試數據來源：
  - 基礎種子：`src/main/resources/data.sql`（已建立 admin/角色等）。
  - Admin 測試專用：`test-data/admin-api-test-data.sql`（本計畫新增；可重複執行）。

## 覆蓋重點與案例類型
- 正向案例：最小必要欄位、成功建立/查詢/更新/刪除。
- 反向案例：格式錯誤/權限不足(401/403)/資源不存在(404)/狀態不合法(400)。
- 權限驗證：需具備 `ADMIN` 與相對應 `ROLE_*_MANAGE` 的請求通過；否則 403。
- 效能/穩定：清單查詢、分頁參數、批次操作基本可用性。

## 完整測試數據
- 檔案：`test-data/admin-api-test-data.sql`
- 內容摘要：
  - 角色：補齊 `ROLE_ORDER_MANAGE`、`ROLE_COMMISSION_MANAGE`、`ROLE_UPGRADE_MANAGE`、`ROLE_INVITE_MANAGE` 並授予 `admin`、`qa_admin`。
  - 使用者：`qa_admin`、`qa_provider`、`qa_client`。
  - 產業：Industry `IT`。
  - 訂單：`ORD-TEST-0001`（provider=qa_provider、client=qa_client、status=CREATED）。
  - 佣金：`CO2501010001`（對應上列訂單、UNPAID）。
  - 分潤：`RS250101000001`（對應上列訂單、UNPAID）。
  - 升等：`UP2501010001`（PENDING，對應 qa_client）。
  - 認證：`CE2501010001`（PENDING，對應 qa_provider）。
  - 邀請：`IN2501010001`（PENDING，inviting=admin、invited=qa_client）。
  - 貨幣：`TWD`（NT$）。
  - 系統清單：`Category-A`（TYPE=CATEGORY）。

### 匯入數據（PowerShell）
- 指令：`scripts/seed-admin-api-test-data.ps1`
- 預設參數：`127.0.0.1:3306 / project / root / Abc123`
- 範例：
```
powershell -ExecutionPolicy Bypass -File scripts/seed-admin-api-test-data.ps1 -SqlFilePath "test-data/admin-api-test-data.sql" -DbHost "162.43.92.30" -DbPort 3307 -DbName "project" -DbUser "root" -DbPass "Abc123"
```

## 自動化測試
- 集合：
  - 核心：`postman-collections/P1-Admin-API-Tests.postman_collection.json`
  - 擴充：`postman-collections/P1-Admin-API-Tests-Extended.postman_collection.json`
  - 環境：`postman-collections/environments/Admin-Local.postman_environment.json`
- 腳本：`scripts/run-admin-api-tests.ps1`
- 產出：報告於 `postman-collections/reports/`（junit.xml、html）
- 執行：
```
powershell -ExecutionPolicy Bypass -File scripts/run-admin-api-tests.ps1 -Collection "postman-collections/P1-Admin-API-Tests.postman_collection.json" -EnvFile "postman-collections/environments/Admin-Local.postman_environment.json"

# 連同擴充集合
powershell -ExecutionPolicy Bypass -File scripts/run-admin-api-tests.ps1 -SkipExtended:$false
```

## 覆蓋分解（端點與斷言）
- Users：列表(200)、建立(201)、更新(200)、啟用/停用(200)、重設密碼(200)、移動/排序(200)、刪除(200/204)。
- Orders：列表(200)、明細(200/404)、狀態更新(200/400/404)。
- Commissions：列表(200)、以字串查詢(200/404)、狀態更新(200/400/404)。
- RevenueShares：列表(200)、更新狀態/付款時間(200)。
- Upgrades：列表(200)。
- Certifications：列表(200)、更新狀態/評論/分數(200/400/404)。
- Invitations：列表(200)、更新狀態(200/400/404)。
- Roles：CRUD(200/201/204/404/409)。
- Currencies：CRUD + 搜尋(200/201/204/404)。
- SystemLists：建立/更新(200/201/404)。

## 執行順序建議
1) 匯入測試數據（SQL）。
2) 啟動服務（確保 DB 指向測試庫）。
3) 執行核心集合；若需財務/升等/認證/邀請覆蓋，追加執行擴充集合。
4) 檢查報告並修補測試或資料。

## 注意事項與風險
- 測試數據影響：請勿於正式資料庫執行 `admin-api-test-data.sql`。
- 權限名稱差異：已補入 `ROLE_ORDER_MANAGE` 等測試所需角色與授權。
- 報告匯出：新環境首次執行會自動安裝 `newman`（需網路）。

## 交付物
- `test-data/admin-api-test-data.sql`：完整種子數據（可重複執行）。
- `scripts/seed-admin-api-test-data.ps1`：一鍵匯入測試數據。
- `postman-collections/P1-Admin-API-Tests*.json`：Postman 集合（核心＋擴充）。
- `scripts/run-admin-api-tests.ps1`：一鍵執行＋產報告。
