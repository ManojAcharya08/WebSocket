package com.example.websocketdemo.config;

import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.http.server.ServerHttpRequest;
import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        // Get username from handshake attributes
        String username = (String) attributes.get("username");
        
        // Validate and sanitize username
        if (username == null || username.isBlank()) {
            return null; // Reject connection if no username
        }
        
        return new StompPrincipal(username.trim());
    }
}
