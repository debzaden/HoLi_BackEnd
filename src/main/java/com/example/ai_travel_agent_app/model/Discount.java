package com.example.ai_travel_agent_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "discounts")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long discountId;

    @Column(columnDefinition = "NVARCHAR(50)", unique = true)
    private String code;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountType type; // PERCENTAGE, FIXED_AMOUNT

    private float value; // giá trị giảm giá

    private float minOrderAmount; // giá trị đơn hàng tối thiểu

    private float maxDiscountAmount; // số tiền giảm giá tối đa

    private int usageLimit; // số lần sử dụng tối đa

    private int usedCount; // số lần đã sử dụng

    private boolean isActive;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
