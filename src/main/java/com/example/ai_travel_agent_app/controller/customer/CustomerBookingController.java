package com.example.ai_travel_agent_app.controller.customer;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_travel_agent_app.dto.booking.BookingCreateRequestDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingDetailResponseDTO;
import com.example.ai_travel_agent_app.service.BookingService;
import com.example.ai_travel_agent_app.utils.BindingValidError;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/customer")
public class CustomerBookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingCreateRequestDTO request,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = BindingValidError.getValidationErrors(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
            String customerEmail = UserFromAuth.getUserEmail();
            BookingDetailResponseDTO booking = bookingService.createBooking(customerEmail, request);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getMyBookings() {
        try {
            String customerEmail = UserFromAuth.getUserEmail();
            List<BookingDetailResponseDTO> bookings = bookingService.getCustomerBookings(customerEmail);
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<?> getBookingDetail(@PathVariable Long bookingId) {
        try {
            BookingDetailResponseDTO booking = bookingService.getBookingDetail(bookingId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            System.out.println("🚫 [CANCEL BOOKING] Received request to cancel booking ID: " + bookingId);
            String customerEmail = UserFromAuth.getUserEmail();
            System.out.println("👤 [CANCEL BOOKING] Customer email: " + customerEmail);
            BookingDetailResponseDTO booking = bookingService.cancelBooking(customerEmail, bookingId);
            System.out.println("✅ [CANCEL BOOKING] Successfully cancelled booking ID: " + bookingId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            System.err.println("❌ [CANCEL BOOKING] Error cancelling booking ID " + bookingId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
