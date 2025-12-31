package com.casemgr.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

	@CreatedDate
	@Column(nullable = false)
	@JsonIgnore
	private LocalDateTime createTime;
	
	@LastModifiedDate
	@Column()
	@JsonIgnore
	private LocalDateTime updateTime;
	
	@Column(nullable = false, columnDefinition = "boolean default true")
	@JsonIgnore
	private Boolean enabled=true;
}
