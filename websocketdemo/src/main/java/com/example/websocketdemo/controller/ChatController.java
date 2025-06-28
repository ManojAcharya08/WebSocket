package com.example.websocketdemo.controller;

import com.example.websocketdemo.model.Message;
import com.example.websocketdemo.repository.MessageRepository;
import com.example.websocketdemo.session.UserSessionRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.Set;

@Controller
public class ChatController {

    @Autowired
    private MessageRepository repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserSessionRegistry userSessionRegistry;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    @MessageMapping("/register")
    public void registerUser(Message message, StompHeaderAccessor headerAccessor) {
        String username = clean(message.getSender());
        if (!StringUtils.hasText(username)) return;

        try {
            userSessionRegistry.registerUser(headerAccessor.getSessionId(), username);
            broadcastUserList();
        } catch (IllegalArgumentException e) {
            sendUsernameError(headerAccessor.getSessionId(), username);
        }
    }

    @MessageMapping("/chat")
    public void handleChatMessage(Message message) {
        message.setSender(clean(message.getSender()));
        message.setContent(clean(message.getContent()));
        
        // Resolve receiver case-insensitively
        String receiverInput = clean(message.getReceiver());
        if (StringUtils.hasText(receiverInput)) {
            String resolvedReceiver = userSessionRegistry.findExistingUsername(receiverInput);
            if (resolvedReceiver == null) {
                sendUserNotFoundError(message.getSender(), receiverInput);
                return;
            }
            message.setReceiver(resolvedReceiver);
        } else {
            message.setReceiver(null);
        }

        if (!isValid(message)) return;

        repository.save(message);

        // Format timestamp
        if (message.getTimestamp() != null) {
            message.setFormattedTimestamp(message.getTimestamp().format(TIMESTAMP_FORMATTER));
        } else {
            message.setFormattedTimestamp("");
        }

        if (isPrivate(message)) {
            sendPrivateMessage(message);
        } else {
            messagingTemplate.convertAndSend("/topic/messages", message);
        }
    }

    private void sendPrivateMessage(Message message) {
        // Send to receiver
        messagingTemplate.convertAndSendToUser(
            message.getReceiver(), "/queue/private", message);
        
        // Send copy to sender
        messagingTemplate.convertAndSendToUser(
            message.getSender(), "/queue/private", message);
    }

    private void sendUsernameError(String sessionId, String username) {
        Message error = new Message();
        error.setSender("System");
        error.setContent("Username '" + username + "' is already taken");
        messagingTemplate.convertAndSendToUser(
            sessionId, "/queue/errors", error);
    }

    private void sendUserNotFoundError(String sender, String invalidUsername) {
        Message error = new Message();
        error.setSender("System");
        error.setContent("User '" + invalidUsername + "' not found");
        messagingTemplate.convertAndSendToUser(
            sender, "/queue/errors", error);
    }

    // Helper methods
    private void broadcastUserList() {
        Set<String> users = userSessionRegistry.getAllUsers();
        messagingTemplate.convertAndSend("/topic/userList", users);
    }

    private String clean(String input) {
        return input != null ? input.trim() : null;
    }

    private boolean isValid(Message message) {
        return StringUtils.hasText(message.getSender()) &&
                StringUtils.hasText(message.getContent()) &&
                message.getContent().length() <= 255;
    }

    private boolean isPrivate(Message message) {
        return StringUtils.hasText(message.getReceiver());
    }
}
