package com.casemgr.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.converter.ConverterJson;
import com.casemgr.enumtype.BlockType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import jakarta.persistence.OneToOne;
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
@Table(name = "T_BLOCK")
@SQLDelete(sql = "UPDATE t_block SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
@ToString(exclude = {"orderTemplate","order","contract"})
public class Block extends BaseEntity{
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long bId;
	
	@Column(name = "NAME", length = 45)
	private String name;
	
	@Column(name = "CONTEXT", length = 1000)
	private String context;
	
	@Column(name = "SORT")
	private Integer sort;//sort on model
	
	@Column(name = "MULTIPLE", nullable = false)
	private Boolean multiple = false;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE")
	private BlockType type; //list/file
		
	@OneToOne
	@JoinColumn(name = "FILE_ID", referencedColumnName = "ID")
	private Filedata fileData;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "ORDER_TEMPLATE_ID", referencedColumnName = "ID")
	private OrderTemplate orderTemplate;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "ORDER_ID", referencedColumnName = "ID")
	private Order order;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "CONTRACT_ID", referencedColumnName = "ID")
	private Contract contract;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "DELIVERY_ITEM_ID", referencedColumnName = "ID")
	private DeliveryItem deliveryItem;
	
	@OneToMany(mappedBy = "block", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
	@OrderBy("blockSort")
	private List<ListItem> listItems= new ArrayList<>();
	
	@OneToMany(mappedBy = "blockSelected", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
	@OrderBy("blockSort")
	private List<ListItem> selectedListItems = new ArrayList<>();
}
