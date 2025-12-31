package com.casemgr.entity;

import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.CEStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@ToString(exclude = {"applicant", "auditor", "identities"})
@Table(name = "T_CERTIFICATION")
@SQLDelete(sql = "UPDATE t_certification SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class Certification extends BaseEntity{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long cId;
    
    @Column(name = "CE_NO", unique = true, nullable = false)
    private String ceNo;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "APPLICANT_ID", referencedColumnName = "ID")
    private User applicant;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "AUDITOR_ID", referencedColumnName = "ID")
    private User auditor;
    
//    @Column(name = "UPLOAD_URL")
//    private String uploadUrl;
    
    @JsonIgnore
    @OneToMany(mappedBy = "identity")
    private List<Filedata> identities;
    
    @JsonIgnore
    @OneToMany(mappedBy = "deal")
    private List<Filedata> deals;
    
    @Column(name = "STATUS", nullable = true)
    @Enumerated(EnumType.STRING)
    private CEStatus status;
    
    @ManyToOne
    @JoinColumn(name = "FEECODE_ID", referencedColumnName = "ID")
    private FeeCode feeCode;
        
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "FEE_ID", referencedColumnName = "ID" )
    private Fee fee;
    
    @Column(name = "EXEMPTION_CODE", length = 100)
    private String exemptionCode;

    @Column(name = "REVIEW_DATA", columnDefinition = "TEXT")
    private String reviewData;

    @Column(name = "INITIAL_RATING")
    private Double initialRating;

    @Column(name = "REVIEW_HISTORY", columnDefinition = "TEXT")
    private String reviewHistory;
    
    private String signName;
    
    private Double score;
    
    private String rejectComment;
}
