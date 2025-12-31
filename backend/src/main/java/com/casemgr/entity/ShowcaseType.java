package com.casemgr.entity;

import java.time.Instant;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_SHOWCASE_TYPE")
@SQLDelete(sql = "UPDATE t_showcase_type SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class ShowcaseType extends BaseEntity {
	public ShowcaseType(String title) {
		this.title = title;
	}
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long stId;
	
    @Column(name = "TITLE", nullable = false)
	private String title;
	
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
            name = "T_STYPE_SHOWCASE",
            joinColumns = @JoinColumn(name = "STYPE_ID"),
            inverseJoinColumns = @JoinColumn(name = "SHOWCASE_ID")
    )
	private Set<Showcase> showcases;
    
    @ManyToOne
    @JoinColumn(name = "FAVOURITE_ID", referencedColumnName = "ID")
    private Favourite favourite;
}
