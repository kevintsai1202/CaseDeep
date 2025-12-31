package com.casemgr.entity;

import java.util.List;
import java.util.Set;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_FAVOURITE")
@SQLDelete(sql = "UPDATE t_favourite SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class Favourite extends BaseEntity {
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long fId;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "T_FAVOURITE_PARTNER",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "FAV_ID")
    )
	private Set<User> partners;
	
	@OneToMany(mappedBy = "favourite", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<ShowcaseType> showcaseTypies;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
            name = "T_FAVOURITE_PRICE",
            joinColumns = @JoinColumn(name = "PRICE_ID"),
            inverseJoinColumns = @JoinColumn(name = "FAV_ID")
    )
	private Set<PricePackage> prices;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
            name = "T_FAVOURITE_ORDERTEMPLATE",
            joinColumns = @JoinColumn(name = "ORDER_TEMPLATE_ID"),
            inverseJoinColumns = @JoinColumn(name = "FAV_ID")
    )
	private Set<OrderTemplate> orderTemplates;
}
