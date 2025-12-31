package com.casemgr.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.FeeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_FEE")
@SQLDelete(sql = "UPDATE t_fee SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class Fee extends BaseEntity {
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long fId;
	
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	private FeeType feeType;
}
