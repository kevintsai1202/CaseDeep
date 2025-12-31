package com.casemgr.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_BIDRECORD")
@SQLDelete(sql = "UPDATE t_bidrecord SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class BidRecord extends BaseEntity{
	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long brId;
	
	@ManyToOne
	@JoinColumn(name = "BID_REQUIRE_ID", referencedColumnName = "ID")
	private BidRequire bidRequire;
	
	@Column(name = "COMMISSION_RATE")
	private float commissionRate;

}
