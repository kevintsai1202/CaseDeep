package com.casemgr.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {
	@Override
    protected Principal determineUser(
        ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String uri = request.getURI().toString();
        String uid = uri.substring(uri.lastIndexOf("/") + 1);
        return () -> uid;
    }
}
