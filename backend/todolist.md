## 新增：修復 Template 付款方式 PATCH 500
- [x] 規格/API 更新：說明將 `PaymentMethod` 轉字串保存，避免序列化錯誤
- [x] 實作：`OrderTemplateServiceImpl.updateTemplatePayment` 將 Enum 映射為字串清單後保存
- [ ] 覆驗：新建模板後呼叫 `PATCH /api/ordertemplates/{oId}/paymentmethod` 應回 200 並回傳字串清單
## 新增：Order 建單容錯強化（B 路線延伸）
- [x] 規格與 API 更新：在 `spec.md`、`api.md` 補充 `createFromTemplate` Null 防護與不變更 API 的說明
- [x] 實作：`OrderServiceImpl.createFromTemplate` 加上 `paymentMethods`、`deliveryType`、`businessDays` 之 Null 防護與 log
- [x] 測試：
  - `scripts\run-customer-order-flow-tests.ps1 -BaseUrl http://localhost:8080 -TemplateId 5` 成功跑通（含建單與狀態更新）
  - 新增 `scripts\run-null-guard-check.ps1`：建立未設定付款方式模板，建單成功、PAYMENT 空清單、Delivery 預設提示字串
  - `scripts\run-customer-order-flow-tests.ps1 -BaseUrl http://localhost:8080 -TemplateId 9` 覆驗成功（以無付款方式模板建單）
  - 備註：`PATCH /api/ordertemplates/{oId}/paymentmethod` 於新建模板時仍可能 500，需另案追查（不影響建單 Null 防護驗證）
## 新增：Order 建單容錯強化（B 路線延伸）
- [x] 規格與 API 更新：在 `spec.md`、`api.md` 補充 `createFromTemplate` Null 防護與不變更 API 的說明
- [x] 實作：`OrderServiceImpl.createFromTemplate` 加上 `paymentMethods`、`deliveryType`、`businessDays` 之 Null 防護與 log
- [ ] 測試：
  - 使用 `scripts\run-customer-order-flow-tests.ps1 -BaseUrl http://localhost:8080 -TemplateId 5`
  - 驗證 `PATCH /api/ordertemplates/{oId}/deliverytype`、`PATCH /api/ordertemplates/{oId}/startingprice`、`POST /api/orders`、`PATCH /api/orders/{oId}/status`
  - 若模板仍為不完整資料，確認服務端不會 500/NPE，且流程可繼續

- [ ] 對齊 OrderServiceImpl 測試：
  - listOrders/OrderCreateResponse 使用明文訂單號
  - getOrderByOrderNo 接受明文參數
  - deleteOrder 使用 findById + delete(entity)
  - updateOrderStatus 放寬 inquiry->completed
  - 新增 cancelOrder(Long oId) 與測試一致
- [ ] 對齊 OrderTemplateServiceImpl 測試：
  - getReferenceById 改 findById + 404
  - 變更後均呼叫 save(entity)
  - 回傳一律使用 OrderTemplateConverter.INSTANCE.entityToResponse
  - 刪除/明細/新增折扣與輸入項路徑補齊例外處理
- [ ] 重跑單元測試並調整



