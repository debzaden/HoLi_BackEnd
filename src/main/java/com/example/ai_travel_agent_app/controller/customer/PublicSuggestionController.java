package com.example.ai_travel_agent_app.controller.customer;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class PublicSuggestionController {

    @GetMapping("/suggestions/keywords")
    public ResponseEntity<List<String>> getSuggestedKeywords() {
        // Có thể lấy từ database dựa trên các service phổ biến
        List<String> suggestions = Arrays.asList(
                "dọn dẹp nhà cửa theo giờ",
                "nấu ăn gia đình",
                "trông trẻ cuối tuần",
                "chăm sóc người già",
                "gia sư tiếng Anh",
                "giặt ủi quần áo",
                "đi chợ mua sắm",
                "sửa chữa nhỏ");

        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/suggestions/popular-services")
    public ResponseEntity<List<String>> getPopularServices() {
        List<String> popularServices = Arrays.asList(
                "Dọn dẹp nhà cửa",
                "Nấu ăn",
                "Trông trẻ",
                "Chăm sóc người già",
                "Gia sư",
                "Giặt ủi",
                "Đi chợ",
                "Sửa chữa nhỏ");

        return ResponseEntity.ok(popularServices);
    }
}
