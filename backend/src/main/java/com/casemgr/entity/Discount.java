package com.casemgr.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.DiscountType; // Assuming DiscountType enum exists

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
@Table(name = "T_DISCOUNT")
@SQLDelete(sql = "UPDATE t_discount SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
@ToString(exclude = {"orderTemplate"}) // Exclude relationships
public class Discount extends BaseEntity { // Assuming BaseEntity provides 'id' and 'enabled'

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long dId; // Keep field name dId if preferred

    @Enumerated(EnumType.STRING)
    @Column(name = "DISCOUNT_TYPE", nullable = false)
    private DiscountType discountType; // e.g., PERCENTAGE, FIXED_AMOUNT

    @Column(name = "DISCOUNT_VALUE", nullable = false, precision = 19, scale = 4)
    private Long discount; // The actual discount value (percentage or amount)

    @Column(name = "SPEND_THRESHOLD", precision = 19, scale = 4)
    private Long spend; // Minimum spend required to trigger the discount (optional)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_TEMPLATE_ID", referencedColumnName = "ID")
    private OrderTemplate orderTemplate; // Link to the template this discount applies to

    // Add other fields if necessary, e.g., description, validity dates, usage limits
    @Column(name = "DESCRIPTION", length = 255)
    private String description;

}