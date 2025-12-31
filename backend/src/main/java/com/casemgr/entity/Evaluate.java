package com.casemgr.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.casemgr.enumtype.EvaluateType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "T_EVALUATE")
@SQLDelete(sql = "UPDATE t_evaluate SET enabled=false WHERE id=?")
@Where(clause = "enabled = true") 
public class Evaluate extends BaseEntity{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
    private Long eId;

    @ManyToOne
    @JoinColumn(name = "EVALUATOR_ID", referencedColumnName = "ID")
    @JsonIgnore
    private User evaluator; //給評人
    
    @ManyToOne
    @JoinColumn(name = "EVALUATEE_ID", referencedColumnName = "ID")
    @JsonIgnore
    private User evaluatee;
    
    private Integer item;//item1 0, item2 1, item3 2
    
    @Enumerated(EnumType.STRING)
    private EvaluateType type;//Client 0, Provider 1
    
    @ManyToOne
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID" )
    @JsonIgnore
    private Order order;  //針對哪個project給評，status須完成才能給評
    
    private int rating; //1-10
    
    private String comment; //評語，可以為空
}
