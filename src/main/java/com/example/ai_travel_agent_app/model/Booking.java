package com.example.ai_travel_agent_app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bookings")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String location;

    @Column(columnDefinition = "NTEXT")
    private String specialRequest;

    private float totalPrice;
    private float originalPrice; // giá gốc trước giảm giá
    private float discountAmount; // số tiền được giảm

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @OneToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Discount discount;

    @ManyToOne(fetch = FetchType.LAZY)
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    private Worker worker;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

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