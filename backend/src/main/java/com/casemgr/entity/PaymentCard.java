package com.casemgr.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_PAYMENT_CARD")
@SQLDelete(sql = "UPDATE t_payment_card SET enabled=false WHERE id=?") // Assuming soft delete pattern
@Where(clause = "enabled = true")
public class PaymentCard extends BaseEntity { // Assuming BaseEntity exists

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long pcId;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetch is generally preferred for ManyToOne
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID", nullable = false)
    @JsonIgnore // Avoid infinite recursion during serialization
    private Order order;

    @Column(name = "INSTALLMENT_NUMBER", nullable = false)
    private Integer installmentNumber; // 分期期數 (e.g., 1, 2, 3...)

    @Column(name = "AMOUNT", nullable = false, precision = 19, scale = 4) // Use BigDecimal for currency
    private BigDecimal amount; // 該期金額

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private PaymentStatus status; // 支付狀態

    @Temporal(TemporalType.TIMESTAMP) // Use Temporal for Date types
    @Column(name = "DUE_DATE")
    private Date dueDate; // 支付截止日期 (可選)

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RECEIVING_ACCOUNT_ID", referencedColumnName = "ID" )
    private Account receivingAccount;

    @Column(name = "RECEIPT_URL", length = 1000)
    private String receiptUrl; // 支付畫面
    
    @Column(name = "INVOICE_URL", length = 1000)
    private String invoiceUrl; // 發票畫面
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PAYMENT_DATE")
    private Date paymentDate; // 實際支付日期 (可選)

}