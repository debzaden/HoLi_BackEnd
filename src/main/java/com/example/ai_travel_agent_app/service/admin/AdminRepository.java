package com.example.ai_travel_agent_app.service.admin;


import com.example.ai_travel_agent_app.model.Admin;
import com.example.ai_travel_agent_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository  extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUser(User user);
}
