package com.example.ai_travel_agent_app.dto.customer;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkerCardDTO {
    private Long workerId;
    private String name;
    private String title;
    private String location;
    private String image;
    private Integer birthYear;
    private String schedule;
    private Float price;
    private String service;
    private String avatar;
    private String description;
    private String otherSkill;
    private List<String> services;
    private Float rating;
    private Integer reviewCount;
    private Boolean isPro;
    private Integer jobsDone;
    private String gender;
    private String phoneNumber;
    private String email;
    private LocalDate createdAt;
    private List<String> categories;
    private Boolean isActive;
    private String experience;
    private Boolean available; // Trạng thái có sẵn
    private String priceRange; // Khoảng giá dạng string
    private Float minPrice; // Giá tối thiểu
    private Float maxPrice; // Giá tối đa
    private List<String> skills; // Danh sách kỹ năng
    private String verificationStatus; // Trạng thái xác minh
    private LocalDate lastActive; // Lần cuối hoạt động
}
