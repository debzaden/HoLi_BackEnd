package com.example.ai_travel_agent_app.dto.customer;

public class CustomerAgentRequest {
    private String sessionId;
    private String query;
    private String message; // Add message field for frontend compatibility

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getQuery() {
        return query != null ? query : message; // Return query or fallback to message
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}