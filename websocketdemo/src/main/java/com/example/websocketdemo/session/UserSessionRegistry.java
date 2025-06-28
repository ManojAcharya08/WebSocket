package com.example.websocketdemo.session;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionRegistry {
    private final ConcurrentHashMap<String, String> sessionIdToUsername = new ConcurrentHashMap<>();

    public synchronized void registerUser(String sessionId, String username) {
        if (isUsernameTaken(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken");
        }
        sessionIdToUsername.put(sessionId, username);
    }

    public String removeUser(String sessionId) {
        return sessionIdToUsername.remove(sessionId);
    }

    public Set<String> getAllUsers() {
        return Collections.unmodifiableSet(new HashSet<>(sessionIdToUsername.values()));
    }

    public String getUsernameForSession(String sessionId) {
        return sessionIdToUsername.get(sessionId);
    }

    public boolean isUsernameTaken(String username) {
        return sessionIdToUsername.values().stream()
            .anyMatch(existing -> existing.equalsIgnoreCase(username));
    }

    // ADD THIS METHOD:
    /**
     * Finds the registered username that matches the input (case-insensitive).
     * Returns the actual registered username, or null if not found.
     */
    public String findExistingUsername(String inputUsername) {
        if (inputUsername == null) return null;
        return sessionIdToUsername.values().stream()
            .filter(existing -> existing.equalsIgnoreCase(inputUsername))
            .findFirst()
            .orElse(null);
    }
}
