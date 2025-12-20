package com.example.ai_travel_agent_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ai_travel_agent_app.model.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    Optional<Discount> findByCodeAndIsActiveTrue(String code);

    Optional<Discount> findByCode(String code);

    boolean existsByCode(String code);
}
