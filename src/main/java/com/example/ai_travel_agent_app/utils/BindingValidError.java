package com.example.ai_travel_agent_app.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

public class BindingValidError {

    public static Map<String, String> getValidationErrors(BindingResult result) {
        return result.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

}
