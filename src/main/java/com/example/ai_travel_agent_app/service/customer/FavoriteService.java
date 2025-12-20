package com.example.ai_travel_agent_app.service.customer;

import com.example.ai_travel_agent_app.dto.customer.FavoriteDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FavoriteService {
    
    FavoriteDTO addFavorite(String customerEmail, Long workerId);
    
    void removeFavorite(String customerEmail, Long workerId);
    
    List<FavoriteDTO> getFavorites(String customerEmail);
    
    boolean isFavorite(String customerEmail, Long workerId);
    
    int getFavoriteCount(Long workerId);
}
