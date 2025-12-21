package com.example.ai_travel_agent_app.service.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.config.SessionManager;
import com.example.ai_travel_agent_app.tools.CustomerAgentTools;

@Service
public class CustomerAgentService {

    private final ChatModel chatModel;
    private final SessionManager sessionManager;
    private final CustomerAgentTools customerAgentTools;
    
    @Autowired
    public CustomerAgentService(ChatModel chatModel,
                               SessionManager sessionManager,
                               CustomerAgentTools customerAgentTools) {
        this.chatModel = chatModel;
        this.sessionManager = sessionManager;
        this.customerAgentTools = customerAgentTools;
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
        
        // Check for direct tool calling patterns first (manual detection)
        String lowerMessage = message.toLowerCase();
        
        // 1. Check for service categories request
        if (lowerMessage.contains("dịch vụ") && (lowerMessage.contains("có") || lowerMessage.contains("danh sách") || lowerMessage.contains("loại"))) {
            System.out.println("🔧 [DIRECT CALL] getServiceCategories");
            return customerAgentTools.getServiceCategories().apply(null);
        }
        
        // 2. Check for worker search by category
        if (lowerMessage.contains("tìm") || lowerMessage.contains("worker") || lowerMessage.contains("người làm")) {
            // Extract potential category keywords
            String[] categories = {"dọn dẹp", "vệ sinh", "nấu ăn", "sửa chữa", "điện", "nước", "chăm sóc", "giúp việc"};
            for (String category : categories) {
                if (lowerMessage.contains(category)) {
                    System.out.println("🔧 [DIRECT CALL] searchWorkersByCategory: " + category);
                    return customerAgentTools.searchWorkersByCategory()
                            .apply(new CustomerAgentTools.SearchWorkersByCategoryRequest(category));
                }
            }
            
            // Check for location search
            if (lowerMessage.contains("ở") || lowerMessage.contains("tại") || lowerMessage.contains("khu vực")) {
                String[] words = message.split("\\s+");
                for (int i = 0; i < words.length; i++) {
                    if ((words[i].toLowerCase().equals("ở") || words[i].toLowerCase().equals("tại")) && i + 1 < words.length) {
                        String location = words[i + 1];
                        System.out.println("🔧 [DIRECT CALL] searchWorkersByLocation: " + location);
                        return customerAgentTools.searchWorkersByLocation()
                                .apply(new CustomerAgentTools.SearchWorkersByLocationRequest(location));
                    }
                }
            }
        }
        
        // Create system message for normal conversation
        SystemMessage systemMessage = new SystemMessage(
            "Bạn là trợ lý AI thân thiện cho nền tảng dịch vụ gia đình HoLi. " +
            "Nhiệm vụ của bạn là hỗ trợ khách hàng tìm kiếm worker và đặt lịch dịch vụ." +
            "\n\nKhi khách hỏi về dịch vụ hoặc worker, hãy gợi ý họ:" +
            "\n- Hỏi 'Có dịch vụ gì?' để xem danh sách dịch vụ" +
            "\n- Hoặc nói 'Tìm worker dọn dẹp' để tìm worker theo dịch vụ" +
            "\n- Hoặc nói 'Worker ở Quận 1' để tìm worker theo địa điểm" +
            "\n\nHãy trả lời thân thiện bằng tiếng Việt."
        );
        
        // Create a new messages list with system message first, then conversation history
        List<Message> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.addAll(conversationHistory);
        
        // Use ChatClient for normal conversation (no function calling)
        try {
            String response = ChatClient.builder(chatModel)
                    .build()
                    .prompt()
                    .messages(messages)
                    .call()
                    .content();
            
            // Add assistant response to session
            AssistantMessage assistantMessage = new AssistantMessage(response);
            sessionManager.addMessageToHistory(sessionId, assistantMessage);
            
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error calling ChatModel: " + e.getMessage());
            e.printStackTrace();
            return "Xin lỗi, tôi đang gặp sự cố kỹ thuật. Bạn có thể thử:\n" +
                   "- 'Có dịch vụ gì?' - Xem danh sách dịch vụ\n" +
                   "- 'Tìm worker dọn dẹp' - Tìm worker theo dịch vụ\n" +
                   "- 'Worker ở Quận 1' - Tìm worker theo địa điểm";
        }
    }
    
    public void clearSession(String sessionId) {
        sessionManager.removeSession(sessionId);
    }
}