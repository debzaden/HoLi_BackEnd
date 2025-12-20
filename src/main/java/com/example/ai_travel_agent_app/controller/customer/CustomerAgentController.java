package com.example.ai_travel_agent_app.controller.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ai_travel_agent_app.service.customer.CustomerAgentService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer-agent")
public class CustomerAgentController {

    private final CustomerAgentService customerAgentService;
    
    // Store chat history per sessionId (for UI display)
    private final Map<String, List<String>> chatHistory = new HashMap<>();

    public CustomerAgentController(CustomerAgentService customerAgentService) {
        this.customerAgentService = customerAgentService;
    }

    /** Customer Agent Home page - Assign unique sessionId per browser session */
    @GetMapping("/")
    public String home(Map<String, Object> model, HttpSession session) {
        String sessionId = (String) session.getAttribute("customerSessionId");
       
        if (sessionId == null) {
            sessionId = customerAgentService.generateSessionId();
            session.setAttribute("customerSessionId", sessionId);
        }

        chatHistory.putIfAbsent(sessionId, new ArrayList<>());
        System.out.println("========= Customer Agent Session: " + sessionId + " =============");
        model.put("sessionId", sessionId);
        model.put("chatHistory", chatHistory.get(sessionId));
        return "customer-agent"; // This will need a new template
    }

    /** AJAX endpoint for sending messages to the Customer AI agent */
    @PostMapping("/ask-ajax")
    @ResponseBody
    public Map<String, String> askAgentAjax(@RequestParam String sessionId,
                                           @RequestParam String message) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Get AI response
            String aiResponse = customerAgentService.handleUserRequest(sessionId, message);
            
            // Update chat history for UI
            List<String> history = chatHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());
            history.add("👤 Khách hàng: " + message);
            history.add("🤖 Assistant: " + aiResponse);
            
            response.put("status", "success");
            response.put("response", aiResponse);
            response.put("sessionId", sessionId);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("response", "Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu của bạn. Vui lòng thử lại!");
            System.err.println("Error in customer agent: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }
}