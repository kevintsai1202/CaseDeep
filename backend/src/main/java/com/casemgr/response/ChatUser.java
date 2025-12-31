package com.casemgr.response;

import com.casemgr.enumtype.StatusType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatUser {
	private Long uId;
	private String username;
    private StatusType status;
}
