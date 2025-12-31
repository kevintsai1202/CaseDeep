package com.casemgr.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.RevenueShareStatus;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_REVENUE_SHARE")
@SQLDelete(sql = "UPDATE t_revenue_share SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
@ToString(exclude = {"order", "client", "industry"})
public class RevenueShare extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;
    
    @Column(name = "REVENUE_SHARE_NO", nullable = false, unique = true, length = 20)
    private String revenueShareNo; // RS + yyMM + 6位隨機數
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID")
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID", referencedColumnName = "ID")
    private User client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROVIDER_ID", referencedColumnName = "ID")
    private User provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INDUSTRY_ID", referencedColumnName = "ID")
    private Industry industry;
    
    @Column(name = "REVENUE_SHARE_RATE", nullable = false)
    private Float revenueShareRate; // 計算後的實際費率
    
    @Column(name = "ORDER_AMOUNT", precision = 19, scale = 4, nullable = false)
    private BigDecimal orderAmount; // 訂單總金額
    
    @Column(name = "REVENUE_SHARE_AMOUNT", precision = 19, scale = 4, nullable = false)
    private BigDecimal revenueShareAmount; // 分潤金額
    
    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private RevenueShareStatus status; // Unpaid/Paid
    
    @Column(name = "PAYMENT_TIME")
    private LocalDateTime paymentTime;
}