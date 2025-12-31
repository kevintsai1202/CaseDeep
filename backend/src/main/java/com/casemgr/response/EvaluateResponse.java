package com.casemgr.response;

import java.io.Serializable;
import java.util.Date;

import com.casemgr.enumtype.EvaluateType;

// Assuming UserResponse exists
// import com.casemgr.response.UserResponse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EvaluateResponse implements Serializable {

    private Long eId;
    // private String base62No;
//    private Long orderId;
    // private UserResponse evaluator; // Might expose too much user info
    // private UserResponse evaluatee; // Might expose too much user info
    private Long evaluatorId; // Safer to expose IDs
    private Long evaluateeId;
    private Integer item; // Which aspect was rated (0, 1, 2)
    private EvaluateType type; // Who rated whom (0: Client->Provider, 1: Provider->Client)
    private int rating; // 1-10
    private String comment;
    private Date createTime;
    private Date updateTime;

    // Constructor or Mapper needed
}