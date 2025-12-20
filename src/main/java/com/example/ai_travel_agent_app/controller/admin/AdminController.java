package com.example.ai_travel_agent_app.controller.admin;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;
// import com.example.ai_travel_agent_app.repository.WorkerRepository;
import com.example.ai_travel_agent_app.repository.customer.CustomerRepository;
import com.example.ai_travel_agent_app.repository.ServiceRepository;
import com.example.ai_travel_agent_app.repository.BookingRepository;
import com.example.ai_travel_agent_app.repository.CategoryRepository;
import com.example.ai_travel_agent_app.repository.ReviewRepository;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.model.WorkerStatus;
import com.example.ai_travel_agent_app.model.BookingStatus;
import com.example.ai_travel_agent_app.model.Worker;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    @Autowired
    private com.example.ai_travel_agent_app.repository.worker.WorkerRepository workerRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/admin/home")
    public String home() {
        return "Hello World";
    }
    
    @GetMapping("/admin/test-auth")
    public ResponseEntity<Map<String, Object>> testAuth() {
        Map<String, Object> response = new HashMap<>();
        
        // Get current authentication
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            response.put("authenticated", true);
            response.put("name", auth.getName());
            response.put("authorities", auth.getAuthorities());
            response.put("principal", auth.getPrincipal().getClass().getSimpleName());
        } else {
            response.put("authenticated", false);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Worker statistics
        long totalWorkers = workerRepository.count();
        long activeWorkers = workerRepository.countByStatus(WorkerStatus.ACTIVE);
        long pendingWorkers = workerRepository.countByStatus(WorkerStatus.PENDING);
        long inactiveWorkers = workerRepository.countByStatus(WorkerStatus.INACTIVE) 
                             ;
        
        stats.put("totalWorkers", totalWorkers);
        stats.put("activeWorkers", activeWorkers);
        stats.put("pendingWorkers", pendingWorkers);
        stats.put("inactiveWorkers", inactiveWorkers);
        
        // Customer statistics
        long totalCustomers = customerRepository.count();
        stats.put("totalCustomers", totalCustomers);
        
        // Booking statistics
        long totalBookings = bookingRepository.count();
        long completedBookings = bookingRepository.findAll().stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .count();
        long ongoingBookings = bookingRepository.findAll().stream()
            .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.PENDING)
            .count();
        
        stats.put("totalBookings", totalBookings);
        stats.put("completedBookings", completedBookings);
        stats.put("ongoingBookings", ongoingBookings);
        
        // Calculate total revenue from completed bookings
        double totalRevenue = bookingRepository.findAll().stream()
            .filter(b -> b.getStatus() == BookingStatus.COMPLETED)
            .mapToDouble(b -> b.getTotalPrice())
            .sum();
        stats.put("totalRevenue", totalRevenue);
        
        // Category statistics
        long totalCategories = categoryRepository.count();
        stats.put("totalCategories", totalCategories);
        
        // Service statistics
        long totalServices = serviceRepository.count();
        stats.put("totalServices", totalServices);
        
        // Review statistics
        long totalReviews = reviewRepository.count();
        stats.put("totalReviews", totalReviews);
        
        // User statistics
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/admin/dashboard/detailed")
    public ResponseEntity<Map<String, Object>> getDetailedStats() {
        Map<String, Object> detailedStats = new HashMap<>();
        
        // Thống kê chi tiết theo tháng, tuần, etc.
        Map<String, Object> monthlyStats = new HashMap<>();
        monthlyStats.put("newUsers", 523);
        monthlyStats.put("newBookings", 234);
        monthlyStats.put("revenue", 125000);
        
        detailedStats.put("monthly", monthlyStats);
        
        return ResponseEntity.ok(detailedStats);
    }
}
