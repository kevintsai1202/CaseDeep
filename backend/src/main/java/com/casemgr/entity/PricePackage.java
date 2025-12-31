package com.casemgr.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "T_PRICE_PACKAGE")
@SQLDelete(sql = "UPDATE t_price_package SET enabled = false WHERE id=?")
@Where(clause = "enabled = true")
@ToString(exclude = {"provider", "orderTemplate"})
public class PricePackage extends BaseEntity {
	
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long pId;
    
    @Column(name = "PACKAGE_NAME", nullable = false)
    private String packageName;
    
    private String packageDesc;
    
    private Double price;
    
    private String fileUrl;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "PROVIDER_ID", referencedColumnName = "ID")
    private User provider;
    
    @OneToMany(mappedBy = "pricePackage")
    @OrderBy("BLOCK_SORT")
    private List<ListItem> listItems = new ArrayList<>();
    
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "ORDER_TEMPLATE_ID", referencedColumnName = "ID")
	private OrderTemplate orderTemplate;
	
}
