package com.casemgr.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.casemgr.enumtype.StatusType;
import com.casemgr.enumtype.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@ToString(exclude = {"showcases", "businessProfile", "personProfile", "favourite", "orderTemplates","pricePackages","feeCodies"})
@Table(name = "T_USER")
@SQLDelete(sql = "UPDATE t_user SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
public class User extends BaseEntity implements UserDetails{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long uId;
    @Column(name = "USERNAME", nullable = false, length = 45)
    private String username;
    @Column(name = "PASSWORD", nullable = false, length = 250 )
    private String password;
    @Column(name = "EMAIL", nullable = false, length = 100, unique = true)
    private String email;
    @Column(name = "VERIFY_CODE", nullable = true, length = 10 )
    private String verifyCode;
    @Column(name = "SEND_VERIFY_FLAG", nullable = false)
    private Boolean sendVerifyFlag=false;
    @Column(name = "RESET_CODE", nullable = true, length = 100 )
    private String resetCode;
    @Column(name = "SEND_RESET_FLAG", nullable = false)
    private Boolean sendResetFlag=false;
    @Column(name = "TITLE", nullable = true, length = 250 )
    private String title;
    @Column(name = "CONTENT", nullable = true, length = 2000 )
    private String content;
    @Column(name = "VEDIO_URL", nullable = true, length = 1000 )
    private String vedioUrl;
    
    @Column(name = "SIGNATURE_URL", nullable = true, length = 1000 )
    private String signatureUrl;
    
    @Column(name = "RESET_EXPIRY_DATE", nullable = true)
    private LocalDateTime resetExpiryDate;
    
    @Column(name = "CERTIFIED", nullable = false)
    private Boolean certified = false;
    
    @Column(name = "INIT_SCORE", nullable = true)
    private Double initScore;
    
    @Column(name = "COMMISSION_RATE", nullable = true)
    private Double commissionRate;
    
    @Column(name = "RANKING_SCORE", nullable = true)
    private Double rankingScore = 0.0;
    
    @Column(name = "DISPLAY_ORDER", nullable = false)
    private Integer displayOrder = 0;
//    @Column(name = "FIRST_NAME" , length = 45)
//    private String firstName;
//    @Column(name = "LAST_NAME", length = 45)
//    private String lastName;
//    @Column(name = "MIDDLE_NAME", length = 45)
//    private String middleName;
//    @Column(name = "NICKNAME", length = 45)
//    private String nickname;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "USER_TYPE", nullable = true, length = 10)
    private UserType userType = UserType.CLIENT;
    
    @Column(name = "STATUS", length = 45)
    private StatusType status;
    
    @Column(name = "LOCKED", nullable = false)
    private Boolean locked = false;
    
    @OneToOne(mappedBy = "user")
    private BusinessProfile businessProfile;
    
    @OneToOne(mappedBy = "user")
    private PersonProfile personProfile;
    
    @Column(name = "INVITATION_CODE", unique = true)
    private String invitationCode;
    
    @Column(name = "REGION", nullable = true, length = 10)
    private String region; // e.g., US, CA, UK, JP, TW

    @Column(name = "CURRENCY_SYMBOL", nullable = true, length = 5)
    private String currencySymbol; // e.g., $, C$, €, ¥, NT$
    
    @OneToOne
    @JoinColumn(name = "FAV_ID", referencedColumnName = "ID")
    private Favourite favourite;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "RECEIVING_ACCOUNT_ID", referencedColumnName = "ID" )
    private Account receivingAccount;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PAYMENT_ACCOUNT_ID", referencedColumnName = "ID")
    private Account paymentAccount;
    
    @JsonIgnore
    @OneToMany(mappedBy = "creator")
    private List<FeeCode> feeCodies;
    
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Showcase> showcases;
    
    @JsonIgnore
    @OneToMany(mappedBy = "provider")
    private List<OrderTemplate> orderTemplates;
    
    @JsonIgnore
    @OneToMany(mappedBy = "provider")
    private List<PricePackage> pricePackages;
//    @OneToOne(mappedBy = "user")
//    private IntroProfile introProfile;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "T_USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<Role> roles = new HashSet<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AdminRoleIndustryScope> adminRoleIndustryScopes = new ArrayList<>();
    

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private List<Bidder> bidders = new ArrayList<>();
    
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
		}
//		authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		return authorities;
	}

	public boolean isEmailVerified() {
		return verifyCode == null || verifyCode.isEmpty();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return getEnabled();
	}
}
