package com.example.ai_travel_agent_app.controller.admin;


import com.example.ai_travel_agent_app.dto.notification.NotificationDTO;
import com.example.ai_travel_agent_app.dto.worker.WorkerProfileResponse;
import com.example.ai_travel_agent_app.dto.worker.WorkerStatusUpdateRequest;
import com.example.ai_travel_agent_app.model.Booking;
import com.example.ai_travel_agent_app.model.NotificationType;
import com.example.ai_travel_agent_app.model.Review;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.repository.BookingRepository;
import com.example.ai_travel_agent_app.repository.ReviewRepository;
import com.example.ai_travel_agent_app.service.NotificationService;
import com.example.ai_travel_agent_app.service.ServiceService;
import com.example.ai_travel_agent_app.service.worker.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/workers")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminWorkerController {

    @Autowired
    private WorkerService workerService;

    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping()
    public ResponseEntity<?> getAllWorkers() {

        List<WorkerProfileResponse> list = workerService.getAll();

        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<?> updateWorker(@PathVariable("id") Long workerId, @RequestBody WorkerStatusUpdateRequest request) {
        workerService.updateWorkerStatus(workerId, request.getStatus(), request.getRejectionReason());
        return ResponseEntity.ok("success");
    }

    // update status service
    @PutMapping("/{id}/service")
    public ResponseEntity<?> updateWorkerService(@PathVariable("id") Long serviceId) {
        serviceService.updateStatusService(serviceId);
        // return updated service so frontend can refresh state
        return ResponseEntity.ok(serviceService.findByServiceId(serviceId));
    }

    @PostMapping("/{id}/notification")
    public ResponseEntity<?> sendNotificationToWorker(@PathVariable("id") Long workerId, @RequestBody NotificationDTO dto) {
        String message = dto.getContent();
        Worker worker = workerService.getWorkerById(workerId);
        String email = worker.getUser().getEmail();
        notificationService.createNotification(email, "Thông báo!", message, NotificationType.INFO);

        return ResponseEntity.ok("success");

    }

    // Admin: list services for a specific worker
    @GetMapping("/{id}/services")
    public ResponseEntity<?> getServicesByWorker(@PathVariable("id") Long workerId) {
        Worker worker = workerService.getWorkerById(workerId);
        String email = worker.getUser().getEmail();
        List<com.example.ai_travel_agent_app.dto.service.ServiceResponseDTO> services = serviceService.findAllByWorker(email);
        return ResponseEntity.ok(services);
    }

    // Admin: update a service (available at /admin/workers/services/{id})
    @PutMapping("/services/{id}")
    public ResponseEntity<?> updateServiceByAdmin(@PathVariable("id") Long serviceId, @ModelAttribute com.example.ai_travel_agent_app.dto.service.ServiceRequestDTO dto) {
        com.example.ai_travel_agent_app.dto.service.ServiceResponseDTO updated = serviceService.update(serviceId, dto);
        return ResponseEntity.ok(updated);
    }

    // Admin: delete a service (available at /admin/workers/services/{id})
    @DeleteMapping("/services/{id}")
    public ResponseEntity<?> deleteServiceByAdmin(@PathVariable("id") Long serviceId) {
        boolean deleted = serviceService.delete(serviceId);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }

    // Admin: get bookings for a specific worker
    @GetMapping("/{id}/bookings")
    public ResponseEntity<?> getBookingsByWorker(@PathVariable("id") Long workerId) {
        Worker worker = workerService.getWorkerById(workerId);
        List<Booking> bookings = bookingRepository.findAllByWorkerOrderByCreatedAtDesc(worker);
        
        // Convert to lightweight DTO to avoid lazy loading issues
        List<Map<String, Object>> bookingDTOs = bookings.stream().map(booking -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", booking.getBookingId());
            dto.put("bookingDate", booking.getCreatedAt());
            dto.put("startTime", booking.getStartTime());
            dto.put("endTime", booking.getEndTime());
            dto.put("status", booking.getStatus().name());
            dto.put("totalPrice", booking.getTotalPrice());
            dto.put("originalPrice", booking.getOriginalPrice());
            dto.put("discountAmount", booking.getDiscountAmount());
            dto.put("location", booking.getLocation());
            dto.put("specialRequest", booking.getSpecialRequest());
            
            // Customer info
            if (booking.getCustomer() != null && booking.getCustomer().getUser() != null) {
                dto.put("customerName", booking.getCustomer().getUser().getRealUserName());
                dto.put("customerId", booking.getCustomer().getId());
            }
            
            // Service info
            if (booking.getService() != null) {
                dto.put("serviceName", booking.getService().getServiceName());
                dto.put("serviceId", booking.getService().getServiceId());
            }
            
            // Review info - check if this booking has a review
            Optional<Review> reviewOpt = reviewRepository.findByBooking(booking);
            if (reviewOpt.isPresent()) {
                Review review = reviewOpt.get();
                Map<String, Object> reviewDto = new HashMap<>();
                reviewDto.put("reviewId", review.getReviewId());
                reviewDto.put("rating", review.getRating());
                reviewDto.put("comment", review.getComment());
                reviewDto.put("createdAt", review.getCreatedAt());
                dto.put("review", reviewDto);
            } else {
                dto.put("review", null);
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(bookingDTOs);
    }

}
