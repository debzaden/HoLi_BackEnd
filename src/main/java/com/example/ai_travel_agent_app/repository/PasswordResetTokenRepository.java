package com.example.ai_travel_agent_app.repository;

import com.example.ai_travel_agent_app.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
}
