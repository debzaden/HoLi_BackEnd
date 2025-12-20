package com.example.ai_travel_agent_app.config;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {private static final long SESSION_TIMEOUT_SECONDS = 1800; // 30 min
private static final int MAX_HISTORY = 20;

private final Map<String, Instant> sessionLastActive = new ConcurrentHashMap<>();
private final Map<String, List<Message>> sessionHistory = new ConcurrentHashMap<>();

public void updateSession(String sessionId) {
    sessionLastActive.put(sessionId, Instant.now());
}

public boolean isSessionExpired(String sessionId) {
    Instant lastActive = sessionLastActive.get(sessionId);
    return lastActive == null || Instant.now().minusSeconds(SESSION_TIMEOUT_SECONDS).isAfter(lastActive);
}

public void removeSession(String sessionId) {
    sessionLastActive.remove(sessionId);
    sessionHistory.remove(sessionId);
}

public List<Message> getHistory(String sessionId) {
    return sessionHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());
}

public void addMessageToHistory(String sessionId, Message message) {
    List<Message> history = getHistory(sessionId);
    history.add(message);

    // Trim to last 20 messages
    if (history.size() > MAX_HISTORY) {
        int start = history.size() - MAX_HISTORY;
        sessionHistory.put(sessionId, new ArrayList<>(history.subList(start, history.size())));
    }
}}
