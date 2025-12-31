package com.casemgr.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.ContractStatus;
import com.casemgr.enumtype.UserType;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_CONTRACT")
@SQLDelete(sql = "UPDATE t_contract SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
@ToString(exclude = { "order", "orderTemplate", "copyFrom", "provider", "client" })
public class Contract extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	private Long cId;

	@Column(name = "CONTRACT_NUMBER", nullable = false, length = 45, unique = true)
	private String contractNo;

	@Column(name = "NAME", nullable = false, length = 45)
	private String name;

	@Column(name = "DESCRIPTION", length = 1000)
	private String description;

	@Column(name = "CONTRACT_PRICE", precision = 19, scale = 4)
	private BigDecimal contractPrice; // 合約金額

	@Column(name = "TYPE", nullable = false, length = 1) // 0:template 1:active
	private String type;

	@OneToOne
	@JoinColumn(name = "ORDER_ID", referencedColumnName = "ID")
	private Order order;

	// A contract template might be associated with an OrderTemplate, but a specific
	// Contract instance belongs to an Order
	@ManyToOne(fetch = FetchType.LAZY) // Changed to ManyToOne if template can be reused
	@JoinColumn(name = "ORDER_TEMPLATE_ID", referencedColumnName = "ID")
	private OrderTemplate orderTemplate; // Link to the template it was based on (optional)

	@ManyToOne
	@JoinColumn(name = "COPYFROM_ID", referencedColumnName = "ID")
	private Contract copyFrom;

	@OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Block> blocks;

//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	private List<SysListItem> sysValues;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "RECEIVING_ACCOUNT_ID", referencedColumnName = "ID")
	private Account receivingAccount;
	
	@ManyToOne
	@JoinColumn(name = "INDUSTRY_ID", referencedColumnName = "ID")
	private Industry industry;

	@OneToOne
	@JoinColumn(name = "SETTLEMENT_ID", referencedColumnName = "ID")
	private Settlement settlement;

	@ManyToOne
	@JoinColumn(name = "PROVIDER_ID", referencedColumnName = "ID")
	private User provider; // 專案負責人

	@ManyToOne
	@JoinColumn(name = "CLIENT_ID", referencedColumnName = "ID")
	private User client; // 發案人

	@Column(name = "CLIENT_SIGNED")
	private Boolean clientSigned = false; // Default to not signed

	@Column(name = "CLIENT_SIGNATURE_URL", length = 1000)
	private String clientSignatureUrl;
	
	@Column(name = "CLIENT_SIGNATURE_TIME")
	private Instant clientSignatureTime ;

	@Column(name = "PROVIDER_SIGNED")
	private Boolean providerSigned = false; // Default to not signed

	@Column(name = "PROVIDER_SIGNATURE_URL", length = 1000)
	private String providerSignatureUrl;

	@Column(name = "PROVIDER_SIGNATURE_TIME")
	private Instant providerSignatureTime ;
	
	@Column(name = "REVISION_DATE")
//	   @Temporal(TemporalType.TIMESTAMP) // Added Temporal annotation
	private Date revisionDate; // Last modification date

	@Column(name = "CONTRACT_STATUS")
	@Enumerated(EnumType.STRING)
	private ContractStatus contractStatus = ContractStatus.Active;

	@Column(name = "CHANGE_REASON", length = 500)
	private String changeReason;

	@Column(name = "PROPOSED_CHANGES_TEXT", columnDefinition = "TEXT") // Use TEXT for potentially long content
	private String proposedChangesText;

	@Column(name = "CHANGE_REQUESTER_ROLE")
	@Enumerated(EnumType.STRING) // Store enum name as string
	private UserType changeRequesterRole;
}
