package com.casemgr.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.DeliveryStatus;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_DELIVERY_ITEM")
@SQLDelete(sql = "UPDATE t_delivery_item SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
@ToString(exclude = {"order"})
public class DeliveryItem extends BaseEntity { // Assuming BaseEntity exists

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long diId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "DESCRIPTION", length = 1000) // Adjust length as needed
    private String description; // 交付項目描述

//    @OneToMany(mappedBy = "deliveryItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Block> deliveries;
    
    @OneToMany(mappedBy = "deliveryItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Filedata> deliveries;
    
    @Column(name = "IS_FINAL")
    @Builder.Default
    private Boolean isFinal = true;
    
//    @Column(name = "FILE_URL", length = 1000) // Adjust length as needed
//    private String fileUrl; // 交付檔案連結 (可選)

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private DeliveryStatus status; // 交付狀態

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DELIVERY_DATE")
    private Date deliveryDate; // 交付日期

    @Column(name = "MODIFICATION_REQUEST_COMMENT", length = 1000) // Adjust length as needed
    private String modificationRequestComment; // 修改請求說明 (可選)

    // Consider adding versioning or other relevant fields
}