package com.example.ai_travel_agent_app.service.customer.impl;

import com.example.ai_travel_agent_app.dto.customer.FavoriteDTO;
import com.example.ai_travel_agent_app.dto.customer.WorkerCardDTO;
import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.Favorite;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.repository.customer.CustomerRepository;
import com.example.ai_travel_agent_app.repository.customer.FavoriteRepository;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;
import com.example.ai_travel_agent_app.service.customer.FavoriteService;
import com.example.ai_travel_agent_app.service.customer.PublicWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private WorkerRepository workerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PublicWorkerService publicWorkerService;
    
    @Override
    @Transactional
    public FavoriteDTO addFavorite(String customerEmail, Long workerId) {
        User user = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        
        // Check if already favorite
        if (favoriteRepository.existsByCustomerAndWorker(customer, worker)) {
            throw new RuntimeException("Worker already in favorites");
        }
        
        Favorite favorite = new Favorite();
        favorite.setCustomer(customer);
        favorite.setWorker(worker);
        
        Favorite savedFavorite = favoriteRepository.save(favorite);
        
        return convertToDTO(savedFavorite);
    }
    
    @Override
    @Transactional
    public void removeFavorite(String customerEmail, Long workerId) {
        User user = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        
        favoriteRepository.deleteByCustomerAndWorker(customer, worker);
    }
    
    @Override
    public List<FavoriteDTO> getFavorites(String customerEmail) {
        User user = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        List<Favorite> favorites = favoriteRepository.findByCustomerOrderByCreatedAtDesc(customer);
        
        return favorites.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isFavorite(String customerEmail, Long workerId) {
        try {
            User user = userRepository.findByEmail(customerEmail)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            
            Customer customer = customerRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            
            Worker worker = workerRepository.findById(workerId)
                    .orElseThrow(() -> new RuntimeException("Worker not found"));
            
            return favoriteRepository.existsByCustomerAndWorker(customer, worker);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public int getFavoriteCount(Long workerId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        
        return favoriteRepository.countByWorker(worker);
    }
    
    private FavoriteDTO convertToDTO(Favorite favorite) {
        WorkerCardDTO workerDTO = null;
        try {
            workerDTO = publicWorkerService.getWorkerDetail(favorite.getWorker().getId());
        } catch (Exception e) {
            // Handle error silently
        }
        
        return FavoriteDTO.builder()
                .favoriteId(favorite.getFavoriteId())
                .customerId(favorite.getCustomer().getId())
                .workerId(favorite.getWorker().getId())
                .worker(workerDTO)
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
