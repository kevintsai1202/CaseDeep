package com.casemgr.entity;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString(exclude = {"user"})
@Table(name = "T_SHOWCASE")
@SQLDelete(sql = "UPDATE t_showcase SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class Showcase extends BaseEntity{
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long sId;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	private User user;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "ORDER_TEMPLATE_ID", referencedColumnName = "ID")
	private OrderTemplate orderTemplate;
	
	@OneToMany(mappedBy = "showcase")
	private List<Filedata> filedatas;
	
	private String title;
	
}
