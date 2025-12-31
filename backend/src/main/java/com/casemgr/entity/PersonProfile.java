package com.casemgr.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringExclude;
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
@ToString(exclude = {"user"})
@Table(name = "T_PERSON_PROFILE")
@SQLDelete(sql = "UPDATE t_person_profile SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class PersonProfile extends BaseEntity{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long ppId;
    
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;
    
    private String legalFullName;
    
    private String idNumber;
    
    private LocalDate birthDate;
    
    private String jobTitle;
    
    private String industry;
    
    private String phone;
    
    private String currency;
    
    private String language;
    
    private String country;
    
    private String state;
    
    private String Address;
    
    private String City;
    
    private String zipCode;
    
    @ManyToOne
    @JoinColumn(name = "LOCATION_ID", referencedColumnName = "ID")
    private Location location;
}
