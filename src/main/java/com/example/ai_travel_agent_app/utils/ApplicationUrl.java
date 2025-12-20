package com.example.ai_travel_agent_app.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;

public class ApplicationUrl {

    public static String getApplicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" + request.getServerPort() +
                request.getContextPath();
    }

    public static String getUrlImage(String imageName, HttpServletRequest request) {

        return ApplicationUrl.getApplicationUrl(request)
                + "/public/image/"
                + imageName;
    }
}
