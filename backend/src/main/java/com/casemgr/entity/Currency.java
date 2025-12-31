package com.casemgr.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString
@Table(name = "T_CURRENCY")
@SQLDelete(sql = "UPDATE t_currency SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class Currency extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;
    
    @Column(name = "CURRENCY", unique = true, nullable = false, length = 10)
    private String currency;
    
    @Column(name = "CURRENCY_SYMBOL", nullable = false, length = 10)
    private String currencySymbol;
}