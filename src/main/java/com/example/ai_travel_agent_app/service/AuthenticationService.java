package com.example.ai_travel_agent_app.service;


import com.example.ai_travel_agent_app.dto.LoginRequest;
import com.example.ai_travel_agent_app.dto.LoginResponse;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {
    LoginResponse authenticate(LoginRequest loginRequest);
}
