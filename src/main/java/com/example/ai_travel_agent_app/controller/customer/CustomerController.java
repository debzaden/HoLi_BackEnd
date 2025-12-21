package com.example.ai_travel_agent_app.controller.customer;

import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.service.customer.CustomerService;
import com.example.ai_travel_agent_app.service.UserService;
import com.example.ai_travel_agent_app.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    // Thêm mới customer
    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        return customerService.addCustomer(customer);
    }

    // Cập nhật thông tin customer
    @PutMapping("/account/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Customer updateCustomer(
        @PathVariable Long id,
        @RequestParam("fullName") String fullName,
        @RequestParam("phoneNumber") String phoneNumber,
        @RequestParam("address") String address,
        @RequestParam("gender") String gender,
        @RequestParam("dateOfBirth") String dateOfBirth,
        @RequestParam(value = "avatar", required = false) MultipartFile avatar
    ) {
        // Kiểm tra quyền truy cập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();
        
        Customer existing = customerService.getCustomerById(id);
        
        // Kiểm tra xem customer này có thuộc về user hiện tại không
        if (!existing.getUser().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Access denied: You can only update your own profile");
        }

        String avatarUrl = existing.getUser().getAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            try {
                // Upload avatar lên Cloudinary
                avatarUrl = cloudinaryService.uploadFile(avatar, "/Customer");
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload avatar: " + e.getMessage());
            }
        }
        
        // Cập nhật thông tin
        existing.getUser().setUserName(fullName);   
        existing.setPhoneNumber(phoneNumber);
        existing.setAddress(address);
        existing.setGender(gender);
        existing.setDateOfBirth(dateOfBirth);
        existing.getUser().setAvatar(avatarUrl);
        
        return customerService.updateCustomerInfo(id, existing);
    }

    @GetMapping("/account/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Customer getCustomerProfile(@PathVariable Long id) {
        // Kiểm tra quyền truy cập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();
        
        Customer customer = customerService.getCustomerById(id);
        
        // Kiểm tra xem customer này có thuộc về user hiện tại không
        if (!customer.getUser().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Access denied: You can only view your own profile");
        }
        
        return customer;
    }
    
    // Thêm endpoint để lấy customer profile theo current user
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public Customer getCurrentCustomerProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName();
        
        User user = userService.findByEmail(currentUserEmail);
        return customerService.getCustomerByUser(user);
    }
}