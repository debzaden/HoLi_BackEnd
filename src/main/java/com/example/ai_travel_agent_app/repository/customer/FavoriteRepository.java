package com.example.ai_travel_agent_app.repository.customer;

import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.Favorite;
import com.example.ai_travel_agent_app.model.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    List<Favorite> findByCustomerOrderByCreatedAtDesc(Customer customer);
    
    Optional<Favorite> findByCustomerAndWorker(Customer customer, Worker worker);
    
    boolean existsByCustomerAndWorker(Customer customer, Worker worker);
    
    void deleteByCustomerAndWorker(Customer customer, Worker worker);
    
    int countByWorker(Worker worker);
}
