package com.example.ai_travel_agent_app.controller.customer;

import com.example.ai_travel_agent_app.dto.customer.FavoriteDTO;
import com.example.ai_travel_agent_app.service.customer.FavoriteService;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer/favorites")
@PreAuthorize("hasRole('CUSTOMER')")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @PostMapping("/{workerId}")
    public ResponseEntity<FavoriteDTO> addFavorite(@PathVariable Long workerId) {
        String customerEmail = UserFromAuth.getUserEmail();
        FavoriteDTO favorite = favoriteService.addFavorite(customerEmail, workerId);
        return ResponseEntity.ok(favorite);
    }
    
    @DeleteMapping("/{workerId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long workerId) {
        String customerEmail = UserFromAuth.getUserEmail();
        favoriteService.removeFavorite(customerEmail, workerId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getFavorites() {
        String customerEmail = UserFromAuth.getUserEmail();
        List<FavoriteDTO> favorites = favoriteService.getFavorites(customerEmail);
        return ResponseEntity.ok(favorites);
    }
    
    @GetMapping("/check/{workerId}")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(@PathVariable Long workerId) {
        String customerEmail = UserFromAuth.getUserEmail();
        boolean isFavorite = favoriteService.isFavorite(customerEmail, workerId);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }
    
    @GetMapping("/count/{workerId}")
    public ResponseEntity<Map<String, Integer>> getFavoriteCount(@PathVariable Long workerId) {
        int count = favoriteService.getFavoriteCount(workerId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
