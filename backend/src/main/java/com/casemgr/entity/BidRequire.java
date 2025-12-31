package com.casemgr.entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.BidType;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_BIDREQUIRE")
@SQLDelete(sql = "UPDATE t_bidrequire SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class BidRequire extends BaseEntity {
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long bId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ISSUER_ID", referencedColumnName = "ID")
	private User issuer;
	
	@OneToMany(mappedBy = "bidRequire", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<BidRecord> bidders;	//需限制只能100個,100個後狀態就會改為Full

	@ManyToOne
	@JoinColumn(name = "INDUSTRY_ID", referencedColumnName = "ID")
	private Industry industry;
	
	private String requirement;
	
	private Instant inquiryDate;
	
	private Instant dueDate;
	
	@ManyToOne
	@JoinColumn(name = "BLOCK_ID", referencedColumnName = "ID")
	private Block projectScale;
	
	private Long minBudget;
	
	private Long maxBudget;
	
	@Enumerated(EnumType.STRING)
	private BidType status;
}
