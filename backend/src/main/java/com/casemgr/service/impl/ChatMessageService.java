package com.casemgr.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.casemgr.entity.Message;
import com.casemgr.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

	private final ChatMessageRepository chatMessageRepository;
//	private final ChatRoomService chatRoomService;
	
	public String getChatCode(String uId1, String uId2) {
		String chatCode = "";
		String[] uIds = {uId1, uId2};
		Arrays.sort(uIds);
		chatCode=uIds[0]+"_"+uIds[1];
		return chatCode;
	}
	
	public Message save(Message chatMessage) {
//		var chatRoom = chatRoomService.getChatRoom(chatMessage.getSender(), chatMessage.getRecipient());
		chatMessage.setChatCode( getChatCode(chatMessage.getSenderId(), chatMessage.getReceiverId()) ); 
		return chatMessageRepository.save(chatMessage);
	}
	
	public List<Message> findChatMessages(String senderId, String receiverId){
		log.info("findChatMessages");
		return chatMessageRepository.findByChatCode(getChatCode(senderId, receiverId));
	}
	
}
