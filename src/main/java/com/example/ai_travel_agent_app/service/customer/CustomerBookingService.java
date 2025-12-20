package com.example.ai_travel_agent_app.service.customer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ai_travel_agent_app.dto.booking.BookingCreateRequestDTO;
import com.example.ai_travel_agent_app.dto.booking.BookingDetailResponseDTO;
import com.example.ai_travel_agent_app.model.Booking;
import com.example.ai_travel_agent_app.model.BookingStatus;
import com.example.ai_travel_agent_app.model.Category;
import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.repository.BookingRepository;
import com.example.ai_travel_agent_app.repository.CategoryRepository;
import com.example.ai_travel_agent_app.repository.ServiceRepository;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.repository.customer.CustomerRepository;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;

@Service
public class CustomerBookingService {

    private final com.example.ai_travel_agent_app.service.BookingService bookingService;
    private final WorkerRepository workerRepository;
    private final ServiceRepository serviceRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CustomerBookingService(com.example.ai_travel_agent_app.service.BookingService bookingService,
                                WorkerRepository workerRepository,
                                ServiceRepository serviceRepository,
                                CustomerRepository customerRepository,
                                BookingRepository bookingRepository,
                                CategoryRepository categoryRepository,
                                UserRepository userRepository) {
        this.bookingService = bookingService;
        this.workerRepository = workerRepository;
        this.serviceRepository = serviceRepository;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Booking createBooking(String customerEmail, Long workerId, Long serviceId, 
                               String address, LocalDateTime startTime, LocalDateTime endTime, 
                               String notes) {
        
        // Validate customer exists
        User user = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + customerEmail));
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + customerEmail));
        
        // Validate worker exists
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found with ID: " + workerId));
        
        // Validate service exists and belongs to worker
        com.example.ai_travel_agent_app.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with ID: " + serviceId));
        
        if (!service.getWorker().getId().equals(workerId)) {
            throw new RuntimeException("Service does not belong to the specified worker");
        }
        
        // Calculate duration in hours
        long durationHours = java.time.Duration.between(startTime, endTime).toHours();
        if (durationHours < 1 || durationHours > 4) {
            throw new RuntimeException("Duration must be between 1 and 4 hours");
        }
        
        // Create booking using DTO
        BookingCreateRequestDTO request = BookingCreateRequestDTO.builder()
                .workerId(workerId)
                .serviceId(serviceId)
                .workDate(startTime.toLocalDate())
                .timeSlot(getTimeSlot(startTime.toLocalTime()))
                .startTime(startTime.toLocalTime())
                .duration((int) durationHours)
                .address(address)
                .notes(notes)
                .build();
        
        BookingDetailResponseDTO response = bookingService.createBooking(customerEmail, request);
        
        // Return the actual booking entity for tool response
        return bookingRepository.findByBookingId(response.getBookingId())
                .orElseThrow(() -> new RuntimeException("Created booking not found"));
    }

    private String getTimeSlot(LocalTime time) {
        int hour = time.getHour();
        if (hour >= 6 && hour < 12) {
            return "morning";
        } else if (hour >= 12 && hour < 18) {
            return "afternoon";
        } else {
            return "evening";
        }
    }

    @Transactional
    public boolean cancelBooking(Long bookingId) {
        try {
            Optional<Booking> bookingOpt = bookingRepository.findByBookingId(bookingId);
            if (bookingOpt.isEmpty()) {
                return false;
            }
            
            Booking booking = bookingOpt.get();
            if (booking.getStatus() == BookingStatus.CANCELLED || 
                booking.getStatus() == BookingStatus.COMPLETED) {
                return false;
            }
            
            String customerEmail = booking.getCustomer().getUser().getEmail();
            bookingService.cancelBooking(customerEmail, bookingId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Booking> getBookingsByCustomerEmail(String customerEmail) {
        User user = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + customerEmail));
        Customer customer = customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + customerEmail));
        
        return bookingRepository.findAllByCustomer(customer);
    }

    public Booking findBookingById(Long bookingId) {
        return bookingRepository.findByBookingId(bookingId).orElse(null);
    }

    public List<String> getAllServiceCategories() {
        return categoryRepository.findAll().stream()
                .map(Category::getCategoryName)
                .collect(Collectors.toList());
    }
}