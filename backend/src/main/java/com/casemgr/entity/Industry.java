package com.casemgr.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString(exclude = {"contracts", "orderTemplates", "bidRequires"})
@Table(name = "T_INDUSTRY")
@SQLDelete(sql = "UPDATE t_industry SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class Industry extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;
    
    // 英文內容（主要語言）
    @Column(name = "NAME", unique = true, nullable = false, length = 45)
    private String name;
    
    @Column(name = "TITLE", length = 100)
    private String title;
    
    @Column(name = "DESCRIPTION", length = 1000)
    private String description;
    
    @Column(name = "META", length = 500)
    private String meta;
    
    @Column(name = "URL_PATH", length = 200)
    private String urlPath;
    
    @Column(name = "ICON", length = 1000)
    private String icon;
    
    @Column(name = "REVENUE_SHARE_RATE")
    private Float revenueShareRate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private Industry parentIndustry;
    
    @OneToMany(mappedBy = "parentIndustry", fetch = FetchType.LAZY)
    private List<Industry> childIndustries;
    
    @OneToMany(mappedBy = "industry", fetch = FetchType.LAZY)
    private List<Order> orders;
    
    @OneToMany(mappedBy = "industry", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Contract> contracts;
    
    @OneToMany(mappedBy = "industry", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderTemplate> orderTemplates;
    
    @OneToMany(mappedBy = "industry", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<BidRequire> bidRequires;
}
