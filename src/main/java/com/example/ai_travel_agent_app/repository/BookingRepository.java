package com.example.ai_travel_agent_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ai_travel_agent_app.model.Booking;
import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.Worker;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @EntityGraph(attributePaths = { "worker", "worker.user", "customer", "customer.user", "service" })
    Optional<Booking> findByBookingId(Long bookingId);

    List<Booking> findAllByWorker(Worker worker);

    List<Booking> findAllByWorkerOrderByCreatedAtDesc(Worker worker);

    List<Booking> findAllByCustomer(Customer customer);

}
