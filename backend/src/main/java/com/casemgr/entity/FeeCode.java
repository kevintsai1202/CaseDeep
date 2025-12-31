package com.casemgr.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.DiscountType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_FEECODE")
@SQLDelete(sql = "UPDATE t_feecode SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class FeeCode extends BaseEntity{
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long fcId;
	
	@Column(name = "FEE_CODE", unique = true, nullable = false)
	private String feeCode;
	
	@ManyToOne
	@JoinColumn(name = "CREATOR_ID", referencedColumnName = "ID")
	private User creator;
}
