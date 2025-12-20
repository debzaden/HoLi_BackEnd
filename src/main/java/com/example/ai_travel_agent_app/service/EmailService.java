package com.example.ai_travel_agent_app.service;

import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.dto.EmailDetails;
import com.example.ai_travel_agent_app.model.Booking;
import jakarta.mail.MessagingException;

@Service
public interface EmailService {

    String sendEmail(EmailDetails emailDetails) throws MessagingException;

    /**
     * Gửi email thông báo đặt lịch mới cho worker
     */
    void sendBookingNotificationToWorker(Booking booking);

    /**
     * Gửi email xác nhận đặt lịch cho customer
     */
    void sendBookingConfirmationToCustomer(Booking booking);

    /**
     * Gửi email thông báo worker đã xác nhận cho customer
     */
    void sendBookingAcceptedToCustomer(Booking booking);

    /**
     * Gửi email thông báo worker đã từ chối cho customer
     */
    void sendBookingRejectedToCustomer(Booking booking, String reason);

    /**
     * Gửi email thông báo hoàn thành công việc
     */
    void sendBookingCompletedToCustomer(Booking booking);
}
