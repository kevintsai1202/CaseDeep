package com.casemgr.config;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	@Override	
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOriginPatterns("*")
//				.setAllowedOrigins("*")
//				.setHandshakeHandler(new CustomHandshakeHandler())
				.withSockJS();
//		js StompJs.Client建立連線的入口	
//		const stompClient = new StompJs.Client({
//		    brokerURL: 'ws://localhost:8080/ws'
//		});
	}
	
	
	/**
     * 配置信息代理
     * /topic 代表廣播通道,即群發 
     * /user 代表點對點，用戶間私訊
     * /app MessageMapping前綴
     */
	
	/**
	* setUserDestinationPrefix
	* 配置用于标识用户目的地的前缀。用户目的地
	* 提供用户订阅对其会话唯一的队列名称的能力，
	* 同时也方便其他人发送消息到这些独特的、
	* 针对用户的队列。
	* <p>例如，当用户尝试订阅 "/user/queue/position-updates" 时，
	* 目的地可能被转换为 "/queue/position-updatesi9oqdfzo"，从而产生一个
	* 唯一的队列名称，不会与其他尝试做同样事情的用户冲突。
	* 随后当消息被发送到 "/user/{username}/queue/position-updates" 时，
	* 目的地被转换为 "/queue/position-updatesi9oqdfzo"。
	* <p>用于标识此类目的地的默认前缀是 "/user/"。
	* 
	* enableSimpleBroker
	* 启用一个简单的消息代理，并配置一个或多个前缀来过滤
	* 针对该代理的目的地（例如，以 "/topic" 为前缀的目的地）。
	* 
	* setApplicationDestinationPrefixes
	* 配置一个或多个前缀来过滤针对应用程序的目的地
	* 注解方法。例如，以 "/app" 为前缀的目的地可能会
	* 由注解方法处理，而其他目的地可能指向
	* 消息代理（例如 "/topic", "/queue"）。
	* <p>当消息被处理时，匹配的前缀会从目的地中移除，
	* 以形成查找路径。这意味着注解不应包含
	* 目的地前缀。
	* <p>没有尾随斜杠的前缀将会自动添加一个。
	* 
	*/
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic","/queue");		// 通道的 prefix, controller 的 @SendTo("/topic/XXX")
		registry.setApplicationDestinationPrefixes("/app");	// 加上 controller 的 @MessageMapping("/hello") => /app/hello
//		registry.setUserDestinationPrefix("/user");			// 預設就是 /user
	}
	

	
	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
		resolver.setDefaultMimeType(APPLICATION_JSON);
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setObjectMapper(new ObjectMapper());
		converter.setContentTypeResolver(resolver);
		messageConverters.add(converter);
		return false;	//不使用預設
	}
}
