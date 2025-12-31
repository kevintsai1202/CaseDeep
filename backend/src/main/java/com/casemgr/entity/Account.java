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
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "T_ACCOUNT")
@SQLDelete(sql = "UPDATE t_account SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
@ToString
public class Account extends BaseEntity{
		@Id
	    @GeneratedValue(strategy=GenerationType.IDENTITY)
	    @Column(name = "ID", unique = true, nullable = false)
		private Long aId;
		
		@Column(name = "BANK_NAME", nullable = false)
		private String bankName;
		
		@Column(name = "BRANCH", nullable = false)
		private String branch;
		
		@Column(name = "ACCOUNT_NO", nullable = false)
		private String accountNo;
		
		@Column(name = "SWIFT_CODE", nullable = false)
		private String swiftCode;
}
