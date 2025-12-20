package com.example.ai_travel_agent_app.repository;

import com.example.ai_travel_agent_app.model.Role;
import com.example.ai_travel_agent_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Return first matching user to avoid NonUniqueResultException when duplicates exist
    Optional<User> findFirstByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByUserId(Long userId);

    List<User> findAllByRole(Role role);
}
