package com.casemgr.entity;

import java.time.LocalDateTime;
// Consider using BigDecimal for amount if precision is critical
// import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.InvitationStatus;

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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "T_INVITATION")
@SQLDelete(sql = "UPDATE t_invitation SET enabled=false WHERE id=?")
@Where(clause = "enabled = true")
@ToString(exclude = {"invitingUser", "invitedUser", "payer"}) // Exclude related entities
public class Invitation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "INVITATION_ID_STR", unique = true, nullable = false, length = 50) // Added based on screenshot 'IN2501032340'
    private String invitationIdStr;

    @Column(name = "INVITATION_CODE", nullable = false, length = 100) // Assuming max length for code
    private String invitationCode;

    @Column(name = "REWARD_AMOUNT", nullable = false)
    private Double rewardAmount; // Consider BigDecimal for precision

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private InvitationStatus status;

    @Column(name = "CREATION_TIME", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "REWARD_PAYMENT_TIME")
    private LocalDateTime rewardPaymentTime; // Nullable

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVITING_USER_ID", referencedColumnName = "ID", nullable = false)
    private User invitingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVITED_USER_ID", referencedColumnName = "ID", nullable = false)
    private User invitedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAYER_ID", referencedColumnName = "ID") // Nullable
    private User payer; // The admin who paid the reward, if applicable
}