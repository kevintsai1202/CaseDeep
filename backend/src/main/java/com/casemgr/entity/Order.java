package com.casemgr.entity;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

// Removed unused import com.casemgr.enumtype.CEStatus;
import com.casemgr.enumtype.OrderStatus;
import com.casemgr.enumtype.OrderType;

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
import jakarta.persistence.OneToMany;
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
@Table(name = "T_ORDER")
@SQLDelete(sql = "UPDATE t_order SET enabled=false WHERE id=?") // Assuming 'id' is the primary key column name in BaseEntity
@Where(clause = "enabled = true")
@ToString(exclude = {"provider", "client"}) // Exclude collections from toString to avoid issues
public class Order extends BaseEntity { // Assuming BaseEntity provides 'id' and 'enabled' fields

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false) // Changed column name to ID to match convention and BaseEntity likely
    private Long oId; // Keep field name oId if preferred, but column is ID

    @Column(name = "ORDER_NUMBER", nullable = false, length = 45, unique = true) // Added unique constraint if applicable
    private String orderNo;
    
    @Column(name = "NAME", nullable = false, length = 45)
    private String name;

    @Column(name = "IMAGE_URL", length = 1000)
    private String imageUrl;
    
    @Column(name = "ORDER_TYPE") // Removed nullable = true, status should ideally not be null
    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Column(name = "STATUS") // Removed nullable = true, status should ideally not be null
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 訂單狀態

    @Column(name = "TOTAL_PRICE", precision = 19, scale = 4)
    private BigDecimal totalPrice; // 訂單總價

    @ManyToOne(fetch = FetchType.LAZY) // Changed to LAZY
    @JoinColumn(name = "PROVIDER_ID", referencedColumnName = "ID")
    private User provider; // 專案負責人

    @ManyToOne(fetch = FetchType.LAZY) // Changed to LAZY
    @JoinColumn(name = "CLIENT_ID", referencedColumnName = "ID")
    private User client; // 發案人

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true) // Added orphanRemoval
    private List<Evaluate> evaluates; // 評價列表

    @ManyToOne(fetch = FetchType.LAZY) // Changed to LAZY
    @JoinColumn(name = "ORDERTEMPLATE_ID", referencedColumnName = "ID")
    private OrderTemplate orderTemplate;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true) // Added orphanRemoval
    private List<Block> confirmations;

    @ManyToOne(fetch = FetchType.LAZY) // Changed to LAZY
    @JoinColumn(name = "INDUSTRY_ID", referencedColumnName = "ID")
    private Industry industry;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true) // Added orphanRemoval
    @OrderBy("createTime DESC") // Assuming createTime exists in Contract or BaseEntity
    private List<Contract> contracts; // 合約履歷

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true) // Added orphanRemoval
    private List<PaymentCard> payments; // 支付卡片列表

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true) // Added orphanRemoval
    private List<DeliveryItem> deliveries; // 交付項目列表
}