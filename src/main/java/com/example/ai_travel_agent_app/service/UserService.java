package com.example.ai_travel_agent_app.service;


import com.example.ai_travel_agent_app.dto.UserRequest;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.VerificationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public interface UserService {
    User registerUser(UserRequest userRequest);

    void saveVerificationTokenForUser(String token, User user);

    String verificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);


    Optional<User> findUserByEmail(String email);

    void savePasswordResetToken(User user, String token);

    User findByEmail(String email);
}
