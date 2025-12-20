package com.example.ai_travel_agent_app.repository.admin;


import com.example.ai_travel_agent_app.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyAdminRepository extends JpaRepository<Admin, Long> {
}
