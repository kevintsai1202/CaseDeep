package com.casemgr.request;

import com.casemgr.enumtype.OrderStatus;
import lombok.Data;

@Data
public class OrderListRequest {

    // 篩選條件 (可選)
    private OrderStatus status;
    private Long clientId;
    private Long providerId;
    private String keyword; // 可用於搜尋訂單編號或名稱等

    // 分頁參數 (可選)
    private Integer page = 0; // 預設第一頁
    private Integer size = 10; // 預設每頁 10 筆

    // 排序參數 (可選)
    private String sortBy = "createTime"; // 預設按創建時間排序
    private String sortDirection = "DESC"; // 預設降冪排序
}