package com.casemgr.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import com.casemgr.entity.Account;
import com.casemgr.enumtype.PaymentStatus;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentCardResponse implements Serializable {

    private Long pcId;
    private Integer installmentNumber;
    private BigDecimal amount;
    private PaymentStatus status;
    private Date dueDate;
    private Account receivingAccount;
    private Date paymentDate;
    private String receiptUrl; // 支付畫面
    private String invoiceUrl; // 發票畫面
}