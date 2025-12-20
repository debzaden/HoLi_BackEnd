package com.example.ai_travel_agent_app.controller.worker;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_travel_agent_app.dto.booking.BookingDetailResponseDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingStatusUpdateRequestDTO;
import com.example.ai_travel_agent_app.service.BookingService;
import com.example.ai_travel_agent_app.utils.UserFromAuth;

@RestController
@RequestMapping("/worker")
public class WorkerBookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/bookings")
    public ResponseEntity<?> getWorkerBookings() {
        try {
            String workerEmail = UserFromAuth.getUserEmail();
            List<BookingDetailResponseDTO> bookings = bookingService.getWorkerBookings(workerEmail);
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<?> getBookingDetail(@PathVariable Long bookingId) {
        try {
            String workerEmail = UserFromAuth.getUserEmail();
            BookingDetailResponseDTO booking = bookingService.getWorkerBookingDetail(workerEmail, bookingId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/bookings/{bookingId}/status")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestBody BookingStatusUpdateRequestDTO request) {
        try {
            String workerEmail = UserFromAuth.getUserEmail();
            BookingDetailResponseDTO booking = bookingService.updateBookingStatus(workerEmail, bookingId, request);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/bookings/{bookingId}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable Long bookingId) {
        try {
            String workerEmail = UserFromAuth.getUserEmail();
            BookingDetailResponseDTO booking = bookingService.completeBooking(workerEmail, bookingId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
