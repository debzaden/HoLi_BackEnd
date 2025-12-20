package com.example.ai_travel_agent_app.controller.admin;

import com.example.ai_travel_agent_app.model.Booking;
import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.repository.BookingRepository;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.repository.customer.CustomerRepository;
import com.example.ai_travel_agent_app.service.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/customers")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminCustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping
    public ResponseEntity<List<Customer>> listAll() {
        List<Customer> list = customerRepository.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        try {
            Customer c = customerService.getCustomerById(id);
            return ResponseEntity.ok(c);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody Customer customer) {
        Customer created = customerService.addCustomer(customer);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody Customer customer) {
        try {
            Customer updated = customerService.updateCustomerInfo(id, customer);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!customerRepository.existsById(id)) return ResponseEntity.notFound().build();
        customerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/{id}/orders")
    public ResponseEntity<List<Booking>> getOrders(@PathVariable Long id) {
        try {
            Customer c = customerService.getCustomerById(id);
            List<Booking> orders = bookingRepository.findAllByCustomer(c);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }


}
