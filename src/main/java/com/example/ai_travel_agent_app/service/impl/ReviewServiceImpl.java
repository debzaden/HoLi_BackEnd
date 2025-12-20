package com.example.ai_travel_agent_app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.example.ai_travel_agent_app.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ai_travel_agent_app.dto.review.ReviewCreateRequestDTO;
import com.example.ai_travel_agent_app.dto.review.ReviewResponseDTO;
import com.example.ai_travel_agent_app.model.Booking;
import com.example.ai_travel_agent_app.model.BookingStatus;
import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.Review;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.Worker;

import com.example.ai_travel_agent_app.repository.ReviewRepository;
import com.example.ai_travel_agent_app.repository.customer.CustomerRepository;
import com.example.ai_travel_agent_app.service.ReviewService;
import com.example.ai_travel_agent_app.service.UserService;
import com.example.ai_travel_agent_app.service.worker.WorkerService;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public ReviewResponseDTO createReview(String customerEmail, ReviewCreateRequestDTO request) {
        // Validate customer
        User customerUser = userService.findByEmail(customerEmail);
        Customer customer = customerRepository.findByUser(customerUser)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Validate booking
        Booking booking = bookingRepository.findByBookingId(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Kiểm tra booking có thuộc về customer này không
        if (!booking.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("You don't have permission to review this booking");
        }

        // Kiểm tra booking đã hoàn thành chưa
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("Can only review completed bookings");
        }

        // Kiểm tra đã đánh giá chưa
        if (reviewRepository.existsByBooking(booking)) {
            throw new RuntimeException("This booking has already been reviewed");
        }

        // Tạo review mới
        Review review = new Review(
                booking,
                customer,
                booking.getWorker(),
                request.getRating(),
                request.getComment());

        review = reviewRepository.save(review);

        return toReviewResponseDTO(review);
    }

    @Override
    public List<ReviewResponseDTO> getWorkerReviews(Long workerId) {
        Worker worker = workerService.getWorkerById(workerId);
        List<Review> reviews = reviewRepository.findAllByWorkerOrderByCreatedAtDesc(worker);

        return reviews.stream()
                .map(this::toReviewResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDTO> getCustomerReviews(String customerEmail) {
        User customerUser = userService.findByEmail(customerEmail);
        Customer customer = customerRepository.findByUser(customerUser)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Review> reviews = reviewRepository.findAllByCustomerOrderByCreatedAtDesc(customer);

        return reviews.stream()
                .map(this::toReviewResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewStatsDTO getWorkerReviewStats(Long workerId) {
        Worker worker = workerService.getWorkerById(workerId);

        Double averageRating = reviewRepository.getAverageRatingByWorker(worker);
        Long totalReviews = reviewRepository.getReviewCountByWorker(worker);

        return new ReviewStatsDTO(
                averageRating != null ? averageRating : 0.0,
                totalReviews != null ? totalReviews : 0L);
    }

    @Override
    public boolean isBookingReviewed(Long bookingId) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return reviewRepository.existsByBooking(booking);
    }

    private ReviewResponseDTO toReviewResponseDTO(Review review) {
        ReviewResponseDTO.Builder builder = ReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt());

        // Booking info
        if (review.getBooking() != null) {
            builder.bookingId(review.getBooking().getBookingId());

            // Service info
            if (review.getBooking().getService() != null) {
                builder.serviceName(review.getBooking().getService().getServiceName());
            }
        }

        // Customer info
        if (review.getCustomer() != null) {
            builder.customerId(review.getCustomer().getId())
                    .customerName(review.getCustomer().getUser().getUsername())
                    .customerAvatar(review.getCustomer().getUser().getAvatar());
        }

        // Worker info
        if (review.getWorker() != null) {
            builder.workerId(review.getWorker().getId())
                    .workerName(review.getWorker().getFullName());
        }

        return builder.build();
    }
}
