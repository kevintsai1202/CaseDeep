# API 說明（本次調整摘要）

- 範圍：Order 建單流程（`POST /api/orders` 使用 `createFromTemplate`）。
- 介面變更：無。所有路由、請求/回應格式維持不變。
- 服務端行為強化：
  - `paymentMethods` 為 null 時不再拋出 NPE，改以空清單處理。
  - `deliveryType` 為 null 或 `businessDays` 缺漏時，以提示字串填入 Delivery 區塊，並記錄警告 log。
  - 補強 log：建立訂單時輸出 `templateId`、`provider/client`、`paymentMethods`、`deliveryType`、`businessDays`、`startingPrice` 與初始總價，便於除錯。

建議仍透過下列既有 API 於建單前先行補齊模板資料：
- `PATCH /api/ordertemplates/{oId}/paymentmethod`
- `PATCH /api/ordertemplates/{oId}/deliverytype`
- `PATCH /api/ordertemplates/{oId}/startingprice`
- 補充：`PATCH /api/ordertemplates/{oId}/paymentmethod`
  - 請求：`{"paymentMethods":["FullPayment","Installment2_1",...]}`
  - 行為：服務端將枚舉轉為字串保存並回傳 `List<String>`，避免序列化錯誤。
