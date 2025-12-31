package com.casemgr.entity;

import java.math.BigDecimal; // Added import for BigDecimal

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.CascadeType; // Keep if needed for other relations
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType; // Added import
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne; // Keep if needed for other relations
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString(exclude = {"pricePackage", "settlement", "block"})
@Table(name = "T_LISTITEM")
@SQLDelete(sql = "UPDATE t_listitem SET enabled=false WHERE id=?") // Assuming 'id' is the primary key column name in BaseEntity
@Where(clause = "enabled = true")
public class ListItem extends BaseEntity { // Assuming BaseEntity provides 'id' and 'enabled'

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false) // Changed column name to ID
    private Long lId; // Keep field name lId if preferred

    @Column(name = "NAME")
    private String name;

    @Column(name = "QUANTITY", precision = 19, scale = 4) // Use BigDecimal for quantity if decimals are possible
    private BigDecimal quantity;

    @Column(name = "UNIT_PRICE", precision = 19, scale = 4) // Use BigDecimal for price
    private BigDecimal unitPrice;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "SELECTED")
    private Boolean selected = false; // Default to false

    @Column(name = "BLOCK_SORT")
    private Integer blockSort; // sort on block

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BLOCK_ID", referencedColumnName = "ID")
    private Block block;
    
    @ManyToOne
    @JoinColumn(name = "BLOCK_SELECTED_ID", referencedColumnName = "ID")
    private Block blockSelected;

    @ManyToOne(fetch = FetchType.LAZY) // Added FetchType.LAZY
    @JoinColumn(name = "PRICE_PACKAGE_ID", referencedColumnName = "ID")
    private PricePackage pricePackage; // Assuming PricePackage entity exists

    @ManyToOne(fetch = FetchType.LAZY) // Added FetchType.LAZY
    @JoinColumn(name = "SETTLEMENT_ID", referencedColumnName = "ID")
    private Settlement settlement; // Assuming Settlement entity exists
}