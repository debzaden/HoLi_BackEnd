package com.example.ai_travel_agent_app.controller.customer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_travel_agent_app.dto.customer.CustomerAgentRequest;
import com.example.ai_travel_agent_app.service.customer.CustomerAgentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/customer")
@Tag(name = "Customer Agent", description = "AI Agent endpoints for customer service")
public class CustomerAgentRestController {
    
    private final CustomerAgentService customerAgentService;

    public CustomerAgentRestController(CustomerAgentService customerAgentService) {
        this.customerAgentService = customerAgentService;
    }

    @PostMapping("/ask")
    @Operation(summary = "Ask the customer agent", 
    description = "Send a query to the customer AI agent to search workers, book services, manage bookings, etc.")
    public Map<String, String> handleAgentRequest(@RequestBody CustomerAgentRequest request) {
        String sessionId = request.getSessionId();
        String query = request.getQuery();

        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Query must not be empty");
        }

        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = customerAgentService.generateSessionId();
        }
        
        // Get username from authentication context (if authenticated)
        String username = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getName())) {
                username = authentication.getName();
                System.out.println("🔑 [Controller] Authenticated user: " + username);
            } else {
                System.out.println("🔓 [Controller] Anonymous user");
            }
        } catch (Exception e) {
            System.err.println("⚠️ [Controller] Could not get authentication: " + e.getMessage());
        }

        String response = customerAgentService.handleUserRequest(sessionId, query, username);

        Map<String, String> result = new HashMap<>();
        result.put("sessionId", sessionId);
        result.put("response", response);
        return result;
    }
}