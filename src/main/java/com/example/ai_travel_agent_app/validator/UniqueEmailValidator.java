package com.example.ai_travel_agent_app.validator;

import com.example.ai_travel_agent_app.annotation.UniqueEmail;
import com.example.ai_travel_agent_app.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private UserRepository userRepository; // Repository kiểm tra DB

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) return true; // Để NotBlank xử lý
        return !userRepository.existsByEmail(email);
    }
}
