package com.example.ai_travel_agent_app.repository;


import com.example.ai_travel_agent_app.model.Transaction;
import com.example.ai_travel_agent_app.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWallet(Wallet wallet);
}
