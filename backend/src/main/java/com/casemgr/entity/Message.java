package com.casemgr.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "T_MESSAGE")
@Entity
public class Message {
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long id;
	
	@Column
	private String senderId;
	
	@Column
	private String receiverId;
	
	@Column
	private String chatCode;
	
	@Column(name = "CONTENT", length = 1000)
	private String content;
	
	@Column
	private Boolean readed;
	
	@CreationTimestamp
	@Column(name = "TIMESTAMP")
	private Date timestamp;
}
