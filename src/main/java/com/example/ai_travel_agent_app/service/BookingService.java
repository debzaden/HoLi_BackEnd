package com.example.ai_travel_agent_app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ai_travel_agent_app.dto.booking.BookingCreateRequestDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingDetailResponseDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingResponseDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingStatusUpdateDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingStatusUpdateRequestDTO;

@Service
public interface BookingService {
    List<BookingResponseDTO> getAllByWorkers(String userEmail);

    /**
     * Tạo booking mới từ customer
     */
    BookingDetailResponseDTO createBooking(String customerEmail, BookingCreateRequestDTO request);

    /**
     * Worker xác nhận hoặc từ chối booking
     */
    BookingDetailResponseDTO updateBookingStatus(String workerEmail, BookingStatusUpdateDTO request);

    /**
     * Lấy danh sách booking của customer
     */
    List<BookingDetailResponseDTO> getCustomerBookings(String customerEmail);

    /**
     * Lấy chi tiết booking
     */
    BookingDetailResponseDTO getBookingDetail(Long bookingId);

    /**
     * Đánh dấu booking hoàn thành
     */
    BookingDetailResponseDTO completeBooking(Long bookingId);

    /**
     * Lấy danh sách booking của worker
     */
    List<BookingDetailResponseDTO> getWorkerBookings(String workerEmail);

    /**
     * Lấy chi tiết booking cho worker
     */
    BookingDetailResponseDTO getWorkerBookingDetail(String workerEmail, Long bookingId);

    /**
     * Worker cập nhật trạng thái booking (confirm/reject)
     */
    BookingDetailResponseDTO updateBookingStatus(String workerEmail, Long bookingId,
            BookingStatusUpdateRequestDTO request);

    /**
     * Worker đánh dấu booking hoàn thành
     */
    BookingDetailResponseDTO completeBooking(String workerEmail, Long bookingId);

    /**
     * Customer hủy booking
     */
    BookingDetailResponseDTO cancelBooking(String customerEmail, Long bookingId);
}
