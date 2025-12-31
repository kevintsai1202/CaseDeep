package com.casemgr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.entity.Message;
import com.casemgr.entity.SocketData;
import com.casemgr.response.ChatUser;
import com.casemgr.service.impl.ChatMessageService;
import com.casemgr.service.impl.UserServiceImpl;
import com.casemgr.utils.MapUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "User Chat Management", description = "APIs for real-time chat functionality including WebSocket messaging, video calling, and user connection management")
@RestController
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class UserChatController {
	
	private final UserServiceImpl userService;
	private final ChatMessageService chatMessageService;
	private final SimpMessagingTemplate messagingTemplate;
	
	@MessageMapping("/connect") 	// /app/connect
	@SendTo("/topic/public")			// Broadcast channel
	public SocketData connectUser(@Payload SocketData socketData) {
		
		Long uId = Long.valueOf(socketData.getData().get("uId").toString());
		
		log.info("User connecting to chat: {}", uId);
		ChatUser cUser = userService.connectUser(uId);	// Mark as ONLINE
		
		return socketData;
	}
	
	@MessageMapping("/disconnect")	// /app/disconnect
	@SendTo("/topic/public")				// Broadcast channel
	public SocketData disconnectUser(@Payload SocketData socketData) {
		Long uId = Long.valueOf( socketData.getData().get("uId").toString());
		
		log.info("User disconnecting from chat: {}", uId);
		ChatUser cUser = userService.disconnectUser(uId);
		return socketData;
	}
	
	@MessageMapping("/chat/message")	// /app/chat/message sendMessage
	public void sendMessage(@Payload SocketData socketData) {
		log.info("Sending chat message");
		log.info("Socket data: {}", socketData);
		Message message = new Message();
		message = MapUtils.mapToBean(socketData.getData(), message);
		log.info("Message object: {}", message);
		String receiverId = socketData.getData().get("receiverId").toString();
		// TODO: Check if user is online, if offline set message read = false and don't execute convertAndSend
		
		Message savedMsg = chatMessageService.save(message);
		messagingTemplate.convertAndSend(
				"/queue/messages/"+receiverId , 	// user/{userid}/messages
				socketData
				);
	}
	
	@MessageMapping("/candidate")	// /app/candidate - Send ICE candidates for WebRTC
	public void sendCandidate(@Payload SocketData socketData) {
		log.info("Sending WebRTC ICE candidate");
		log.info("Socket data: {}", socketData);
		String receiverId = socketData.getData().get("receiverId").toString();
		
		messagingTemplate.convertAndSend(
				"/queue/messages/"+receiverId , 	// user/{userid}/messages
				socketData
				);
	}
	
	@MessageMapping("/video/offer")	// /app/video/offer - Send WebRTC offer for video call
	public void offerProcess(@Payload SocketData socketData) {
		log.info("Processing video call offer");
		log.info("Socket data: {}", socketData);
		String receiverId = socketData.getData().get("receiverId").toString();
		
		messagingTemplate.convertAndSend(
				"/queue/messages/"+receiverId , 	// user/{userid}/messages
				socketData
				);
	}
	
	@MessageMapping("/video/answer")	// /app/video/answer - Send WebRTC answer for video call
	public void answerProcess(@Payload SocketData socketData) {
		log.info("Processing video call answer");
		log.info("Socket data: {}", socketData);
		String receiverId = socketData.getData().get("receiverId").toString();
		
		messagingTemplate.convertAndSend(
				"/queue/messages/"+receiverId , 	// user/{userid}/messages
				socketData
				);
	}
	
	@GetMapping("/chat/messages/{senderId}/{receiverId}")
	@Operation(
		summary = "Get chat message history between two users",
		description = "Retrieves the complete chat message history between a sender and receiver. " +
					  "This endpoint returns all messages exchanged between the two specified users in chronological order."
	)
	public ResponseEntity<List<Message>> findChatMessage(
		@Parameter(description = "ID of the message sender", required = true)
		@PathVariable("senderId") String senderId,
		@Parameter(description = "ID of the message receiver", required = true)
		@PathVariable("receiverId") String receiverId
	) {
		return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, receiverId));
	}
	
	@GetMapping("/chat/chatuser/{uId}")
	@Operation(
		summary = "Get chat user information",
		description = "Retrieves detailed information about a specific chat user including their online status, " +
					  "profile details, and chat-related settings."
	)
	public ResponseEntity<ChatUser> getChatUser(
		@Parameter(description = "User ID to retrieve chat information for", required = true)
		@PathVariable("uId") Long uId
	) {
		return ResponseEntity.ok(userService.getChatUser(uId));
	}
	
	@GetMapping("/chat/chatuser/online")
	@Operation(
		summary = "Get all online users",
		description = "Retrieves a list of all users currently connected to the WebSocket chat system. " +
					  "This endpoint helps identify which users are available for real-time communication."
	)
	public ResponseEntity<List<ChatUser>> listConnectedUsers(){
		return ResponseEntity.ok(userService.findConnectedUsers());
	}
	
	@GetMapping("/chat/chatuser/offline")
	@Operation(
		summary = "Get all offline users",
		description = "Retrieves a list of all users currently disconnected from the WebSocket chat system. " +
					  "This endpoint helps identify which users are not available for real-time communication."
	)
	public ResponseEntity<List<ChatUser>> listDisconnectedUsers(){
		return ResponseEntity.ok(userService.findDisconnectedUsers());
	}
	
	@GetMapping("/chat/chatuser/alluser")
	@Operation(
		summary = "Get all chat users",
		description = "Retrieves a complete list of all users in the chat system including their basic information " +
					  "such as user ID and username, regardless of their online/offline status."
	)
	public ResponseEntity<List<ChatUser>> listAllChatUser(){
		return ResponseEntity.ok(userService.listAllChatUsers());
	}
}
