package com.example.ai_travel_agent_app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_travel_agent_app.dto.booking.BookingDetailResponseDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingResponseDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingStatusUpdateDTO;
import com.example.ai_travel_agent_app.service.BookingService;
import com.example.ai_travel_agent_app.utils.BindingValidError;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import jakarta.validation.Valid;

@RestController
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Booking worker role - Lấy danh sách booking (Legacy - sẽ chuyển sang
    // WorkerBookingController)
    @GetMapping("/worker/bookings/legacy")
    public ResponseEntity<?> getBookingByWorkerLegacy() {
        String userEmail = UserFromAuth.getUserEmail();
        List<BookingResponseDTO> list = bookingService.getAllByWorkers(userEmail);
        return ResponseEntity.ok(list);
    }

    // Worker xác nhận hoặc từ chối booking (Legacy - sẽ chuyển sang
    // WorkerBookingController)
    @PutMapping("/worker/bookings/status/legacy")
    public ResponseEntity<?> updateBookingStatusLegacy(@Valid @RequestBody BookingStatusUpdateDTO request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = BindingValidError.getValidationErrors(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            String workerEmail = UserFromAuth.getUserEmail();
            BookingDetailResponseDTO booking = bookingService.updateBookingStatus(workerEmail, request);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Đánh dấu booking hoàn thành (Legacy - sẽ chuyển sang WorkerBookingController)
    @PutMapping("/worker/bookings/{bookingId}/complete/legacy")
    public ResponseEntity<?> completeBookingLegacy(@PathVariable Long bookingId) {
        try {
            BookingDetailResponseDTO booking = bookingService.completeBooking(bookingId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
