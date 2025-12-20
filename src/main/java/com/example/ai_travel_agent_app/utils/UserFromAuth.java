package com.example.ai_travel_agent_app.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserFromAuth {

    public static String getUserEmail() {
        String userEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userEmail= authentication.getName();
        return userEmail;
    }
}
