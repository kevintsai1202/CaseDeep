package com.casemgr.entity;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_SETTLEMENT")
@SQLDelete(sql = "UPDATE t_settlement SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class Settlement extends BaseEntity{
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long sId;
	
	private Integer frequency; //0:custom 1:all 2 / 3 / 4 / 5 / 10
	private Long total;
	private Long sum;
	
	@OneToOne
	@JoinColumn(name = "MODEL_ID", referencedColumnName = "ID")
	private Contract model;
	
	@OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("sort")
	private List<ListItem> listItems;
	
	@OneToOne
	@JoinColumn(name = "CURRENCY_ID", referencedColumnName = "ID")
	private SysListItem currency;
}
