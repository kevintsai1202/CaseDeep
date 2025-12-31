package com.casemgr.enumtype;

public enum PaymentStatus {
    Pending, // 待支付
    Paid,    // 已支付
    Complete
//    Failed   // 支付失敗
    // 可以根據需要添加更多狀態，例如 Processing (處理中), Refunded (已退款) 等
}