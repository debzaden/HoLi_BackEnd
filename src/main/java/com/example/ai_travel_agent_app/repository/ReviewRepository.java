package com.example.ai_travel_agent_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ai_travel_agent_app.model.Booking;
import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.Review;
import com.example.ai_travel_agent_app.model.Worker;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByBooking(Booking booking);

    List<Review> findAllByWorkerOrderByCreatedAtDesc(Worker worker);

    List<Review> findAllByCustomerOrderByCreatedAtDesc(Customer customer);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.worker = :worker")
    Double getAverageRatingByWorker(@Param("worker") Worker worker);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.worker = :worker")
    Long getReviewCountByWorker(@Param("worker") Worker worker);

    boolean existsByBooking(Booking booking);
}
