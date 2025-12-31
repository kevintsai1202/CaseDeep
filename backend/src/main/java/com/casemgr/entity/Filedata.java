package com.casemgr.entity;

import java.util.List;

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
@Table(name = "T_FILEDATA")
@SQLDelete(sql = "UPDATE t_filedata SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
@ToString(exclude = {"owner","sharedUsers","showcase","deal","identity","deliveryItem"})
public class Filedata extends BaseEntity{
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
	private Long fId;
	
	@Column(name = "FILE_NAME", nullable = false, length = 256)
	private String fileName;
	
	@Column(name = "STORAGE_UUID", nullable = false, length = 45)
	private String storageUuid;
	
	@Column(name = "STORAGE_FOLDER", nullable = false, length = 10) //upload date
	private String storageFolder;
	
	@Column(name = "SIZE", nullable = false) //byte
	private Long size;
	
	@OneToOne
    @JoinColumn(name = "OWNER_ID", referencedColumnName = "ID", nullable=true)
	private User owner;
	
	@OneToMany
	private List<User> sharedUsers;
	
	@ManyToOne
	@JoinColumn(name = "SHOWCASE_ID", referencedColumnName = "ID", nullable=true)
	private Showcase showcase;
	
    @ManyToOne
    @JoinColumn(name = "DEAL_ID")
    private Certification deal;
    
    @ManyToOne
    @JoinColumn(name = "IDENTITY_ID")
    private Certification identity;
    
    @ManyToOne
    @JoinColumn(name = "DELIVERY_ITEM_ID")
    private DeliveryItem deliveryItem;
	
}
