package com.casemgr.entity;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.converter.ConverterJson; // Assuming this converter exists and is needed
import com.casemgr.enumtype.OrderTemplateSelectType;
// Removed unused import com.casemgr.enumtype.CEStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType; // Keep if CEStatus or other Enum is used
import jakarta.persistence.Enumerated; // Keep if CEStatus or other Enum is used
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_ORDER_TEMPLATE")
@SQLDelete(sql = "UPDATE t_order_template SET enabled=false WHERE id=?") // Assuming 'id' is the primary key column name in BaseEntity
@Where(clause = "enabled = true")
@ToString(exclude = {"discounts", "provider", "blocks", "industry", "contract"}) // Exclude collections and related entities
public class OrderTemplate extends BaseEntity { // Assuming BaseEntity provides 'id' and 'enabled'

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false) // Changed column name to ID
    private Long otId; // Keep field name otId if preferred

    @Column(name = "NAME", nullable = false, length = 45)
    private String name;

    // @Column(name = "STATUS", nullable = true) // Status might not be needed on template
    // @Enumerated(EnumType.STRING)
    // private CEStatus status;

    @Column(name = "IMAGE_URL", length = 1000)
    private String imageUrl;

    @Column(name = "HAS_DESC_REF")
    private Boolean hasDescRef; // Consider if this is still needed

    @Convert(converter = ConverterJson.class)
    @Column(name = "PAYMENT_METHOD", columnDefinition = "TEXT") // Use TEXT for potentially long JSON
    private List<String> paymentMethods; // Store as JSON string

    @Enumerated(EnumType.STRING)
    @Column(name = "DELIVERY_TYPE") // Consider using an Enum
    private OrderTemplateSelectType deliveryType;

    @Column(name = "BUSINESS_DAYS")
    private Integer businessDays;

    @Column(name = "STARTING_PRICE") // Consider BigDecimal if price can have decimals
    private Integer startingPrice;

    @OneToMany(mappedBy = "orderTemplate", fetch = FetchType.LAZY) // Changed to LAZY, added cascade/orphanRemoval
    private List<Discount> discounts; // Assuming Discount entity exists

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER) // Changed to LAZY
    @JoinColumn(name = "PROVIDER_ID", referencedColumnName = "ID")
    private User provider; // 專案負責人

    @OneToMany(mappedBy = "orderTemplate", fetch = FetchType.LAZY) // Changed to LAZY, added cascade/orphanRemoval
    private List<Block> blocks;

    @ManyToOne(fetch = FetchType.LAZY) // Changed to LAZY
    @JoinColumn(name = "INDUSTRY_ID", referencedColumnName = "ID")
    private Industry industry;

    // A template might have a default contract structure, but the active contract belongs to an Order.
    // If this represents the default structure, keep it. Otherwise, remove.
    @OneToOne(mappedBy = "orderTemplate", fetch = FetchType.LAZY) // Changed to LAZY, added cascade/orphanRemoval
    @OrderBy("createTime DESC") // Assuming createTime exists
    private Contract contract; // 合約模板結構
    
	@Column(name = "SKIP_CONTRACT")
	private Boolean skipContract = false;
}