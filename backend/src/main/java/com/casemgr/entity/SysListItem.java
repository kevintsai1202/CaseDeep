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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_SYSLISTITEM")
@SQLDelete(sql = "UPDATE t_syslistitem SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
//system options
public class SysListItem extends BaseEntity{
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long slId;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "SORT")
	private Integer sort;//sort on menu
	
	@Column(name = "TYPE")
	private String type; //currency, language, area, unit
	
	@Column(name = "ICON_URL", length = 1000)
	private String iconUrl;
	
//	@OneToMany(mappedBy = "sysListItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	private List<Model> models;
//	
//	@OneToMany(mappedBy = "sysListItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	private List<Project> projects;
//	
//	@OneToMany(mappedBy = "sysListItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	private List<UserProfile> userProfiles;
}
