package com.casemgr.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_ROLE")
@SQLDelete(sql = "UPDATE t_role SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class Role extends BaseEntity{
	@Id
	   @GeneratedValue(strategy=GenerationType.IDENTITY)
	   @Column(name = "ID", unique = true, nullable = false)
	   private Long id;
	
	@Column(name = "ROLENAME", unique = true)
	   private String roleName;
	
	@Column(name = "DESCRIPTION", length = 200)
	   private String description;
	
	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "T_USER_ROLE",
            joinColumns = @JoinColumn(name = "ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID")
    )
    private Set<User> users = new HashSet<User>();
}
