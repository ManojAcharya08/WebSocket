package com.example.websocketdemo.listener;

import com.example.websocketdemo.model.Message;
import com.example.websocketdemo.session.UserSessionRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
public class WebSocketEventListener {

    private final UserSessionRegistry userSessionRegistry;
    private final SimpMessagingTemplate messagingTemplate;
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm:ss");

    public WebSocketEventListener(UserSessionRegistry userSessionRegistry, 
                                  SimpMessagingTemplate messagingTemplate) {
        this.userSessionRegistry = userSessionRegistry;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String username = userSessionRegistry.removeUser(sessionId);

        if (username != null) {
            // Check if user has other active sessions
            boolean isUserStillOnline = userSessionRegistry.isUsernameTaken(username);
            
            // Broadcast updated user list
            Set<String> users = userSessionRegistry.getAllUsers();
            messagingTemplate.convertAndSend("/topic/userList", users);

            // Only send leave message if user is completely offline
            if (!isUserStillOnline) {
                Message systemMessage = new Message();
                systemMessage.setSender("System");
                systemMessage.setContent(username + " has left the chat");
                systemMessage.setTimestamp(LocalDateTime.now());
                systemMessage.setFormattedTimestamp(LocalDateTime.now().format(TIMESTAMP_FORMATTER));
                messagingTemplate.convertAndSend("/topic/messages", systemMessage);
            }
        }
    }
}
