package com.casemgr.entity;

import java.util.Map;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class SocketData {
	public enum SocketType{
		connect, disconnect, candidate, chat_message, video_offer, video_answer, video_reject, video_hangup, voice_offer, voice_answer, voice_reject, voice_hangup  
	}
	
	@Enumerated(EnumType.STRING)
	private SocketType type;
	
	private Map<String, Object> data;
}
