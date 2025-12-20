package com.example.ai_travel_agent_app.repository.worker;

import com.example.ai_travel_agent_app.model.Wallet;
import com.example.ai_travel_agent_app.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet getByWorker(Worker worker);
}
