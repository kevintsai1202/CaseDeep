package com.casemgr.entity;

import java.time.LocalDateTime;
// Consider using BigDecimal for amount if precision is critical
// import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.PaymentStatus;

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
@Table(name = "T_COMMISSION")
@SQLDelete(sql = "UPDATE t_commission SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
@ToString(exclude = {"order"}) // Exclude related entities from toString
public class Commission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long cId;

    @Column(name = "COMMISSION_ID_STR", unique = true, nullable = false, length = 50) // Added based on screenshot 'CO2501232305'
    private String commissionIdStr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID", nullable = false)
    private Order order;

    @Column(name = "AMOUNT", nullable = false)
    private Double amount; // Consider BigDecimal for precision

    @Column(name = "RATE", nullable = false)
    private Double rate; // Commission rate at the time of calculation

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_STATUS", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate; // Nullable, set when payment is made
}