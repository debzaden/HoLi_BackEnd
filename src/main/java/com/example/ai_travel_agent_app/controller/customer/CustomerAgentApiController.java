package com.example.ai_travel_agent_app.controller.customer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.ai_travel_agent_app.service.customer.CustomerAgentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/customer/agent")
@Tag(name = "Customer Agent API", description = "RESTful API endpoints for Customer AI Agent")
@CrossOrigin(origins = "*")
public class CustomerAgentApiController {
    
    private final CustomerAgentService customerAgentService;

    public CustomerAgentApiController(CustomerAgentService customerAgentService) {
        this.customerAgentService = customerAgentService;
    }

    @PostMapping("/ask")
    @Operation(
        summary = "Send message to Customer AI Agent",
        description = "Send a customer query to the AI agent and receive a response with optional HTML content"
    )
    public ResponseEntity<Map<String, Object>> askAgent(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String sessionId = request.get("sessionId");

        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Message must not be empty"));
        }

        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = customerAgentService.generateSessionId();
        }

        // Get username from authentication context (if authenticated)
        String username = null;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getName())) {
                username = authentication.getName();
                System.out.println("🔑 [API Controller] Authenticated user: " + username);
            } else {
                System.out.println("🔓 [API Controller] Anonymous user");
            }
        } catch (Exception e) {
            System.err.println("⚠️ [API Controller] Could not get authentication: " + e.getMessage());
        }

        try {
            String response = customerAgentService.handleUserRequest(sessionId, message, username);
            
            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("success", true);
            
            // Check if response contains HTML
            if (response != null && response.contains("<")) {
                result.put("message", "");
                result.put("htmlContent", response);
            } else {
                result.put("message", response);
                result.put("htmlContent", null);
            }
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("sessionId", sessionId);
            error.put("success", false);
            error.put("message", "Đã có lỗi xảy ra: " + e.getMessage());
            error.put("htmlContent", null);
            
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/reset")
    @Operation(
        summary = "Reset chat session",
        description = "Clear the conversation history for a session"
    )
    public ResponseEntity<Map<String, Object>> resetSession(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Session ID is required"));
        }

        try {
//            customerAgentService.clearSession(sessionId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Session cleared successfully"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "Failed to clear session: " + e.getMessage()
            ));
        }
    }
}
