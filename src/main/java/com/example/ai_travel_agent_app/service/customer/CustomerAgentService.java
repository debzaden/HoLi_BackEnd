package com.example.ai_travel_agent_app.service.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.config.SessionManager;
import com.example.ai_travel_agent_app.tools.CustomerAgentTools;

@Service
public class CustomerAgentService {

    private final ChatClient chatClient;
    private final SessionManager sessionManager;
    private final CustomerAgentTools customerAgentTools;
    
    public CustomerAgentService(ChatClient.Builder chatClientBuilder, 
                               SessionManager sessionManager,
                               CustomerAgentTools customerAgentTools) {
        this.sessionManager = sessionManager;
        this.customerAgentTools = customerAgentTools;
        this.chatClient = chatClientBuilder.build();
    }
    
    public String generateSessionId() {
        return "customer-session-" + UUID.randomUUID().toString();
    }
    
    public String handleUserRequest(String sessionId, String message) {
        
        // Check if session exists and is not expired
        if (sessionManager.isSessionExpired(sessionId)) {
            sessionManager.removeSession(sessionId);
        }
        sessionManager.updateSession(sessionId);
        
        // Create user message
        UserMessage userMessage = new UserMessage(message);
        
        // Add to session history
        sessionManager.addMessageToHistory(sessionId, userMessage);
        
        // Get conversation history (only user and assistant messages)
        List<Message> conversationHistory = sessionManager.getHistory(sessionId);
        
        // Create system message for customer service
        SystemMessage systemMessage = new SystemMessage(
            "You are a helpful AI customer service assistant for a home service platform. " +
            "Your role is to help customers find workers, book services, manage their bookings, and answer questions. " +
            "\n\nYour capabilities include:" +
            "\n- Search for workers by service category (cleaning, cooking, repair, etc.)" +
            "\n- Search for workers by location/address" +
            "\n- Get detailed information about specific workers" +
            "\n- Help customers book services with workers" +
            "\n- Cancel existing bookings" +
            "\n- Show customer's booking history" +
            "\n- Get information about available service categories" +
            "\n- Show services offered by specific workers" +
            "\n\nWhen booking services, remember:" +
            "\n- Always ask for customer email if not provided" +
            "\n- Booking time format should be yyyy-MM-dd HH:mm (e.g., 2024-12-25 14:30)" +
            "\n- Duration should be between 1-4 hours" +
            "\n- Always confirm booking details before proceeding" +
            "\n\nBe friendly, helpful, and provide clear information. If you don't understand something, ask for clarification." +
            "\n\nAlways respond in Vietnamese language."
        );
        
        // Create a new messages list with system message first, then conversation history
        List<Message> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.addAll(conversationHistory);
        
        // Check if user is asking for worker search or services and call tools directly
        String lowerMessage = message.toLowerCase();
        
        // Direct tool calling based on user intent
        if (lowerMessage.contains("danh mục") || lowerMessage.contains("dịch vụ") || lowerMessage.contains("category") || lowerMessage.contains("service")) {
            if (lowerMessage.contains("có sẵn") || lowerMessage.contains("all") || lowerMessage.contains("list")) {
                // User wants to see all categories
                return customerAgentTools.getServiceCategories().apply(null);
            }
        }
        
        if (lowerMessage.contains("tìm") || lowerMessage.contains("search") || lowerMessage.contains("worker")) {
            // Extract category name from message
            String[] words = message.split("\\s+");
            for (int i = 0; i < words.length; i++) {
                if (words[i].toLowerCase().contains("danh mục") && i + 1 < words.length) {
                    String categoryName = words[i + 1];
                    return customerAgentTools.searchWorkersByCategory()
                        .apply(new CustomerAgentTools.SearchWorkersByCategoryRequest(categoryName));
                }
            }
            
            // Check for location search
            if (lowerMessage.contains("ở") || lowerMessage.contains("tại") || lowerMessage.contains("location")) {
                for (int i = 0; i < words.length; i++) {
                    if ((words[i].toLowerCase().equals("ở") || words[i].toLowerCase().equals("tại")) && i + 1 < words.length) {
                        String location = words[i + 1];
                        return customerAgentTools.searchWorkersByLocation()
                            .apply(new CustomerAgentTools.SearchWorkersByLocationRequest(location));
                    }
                }
            }
        }

        // Get AI response normally for other queries
        String response = chatClient.prompt()
                .messages(messages)
                .call()
                .content();
        
        // Add assistant response to session
        AssistantMessage assistantMessage = new AssistantMessage(response);
        sessionManager.addMessageToHistory(sessionId, assistantMessage);
        
        return response;
    }
    
    public void clearSession(String sessionId) {
        sessionManager.removeSession(sessionId);
    }
}