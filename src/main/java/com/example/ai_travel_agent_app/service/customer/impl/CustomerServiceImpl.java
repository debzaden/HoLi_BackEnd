package com.example.ai_travel_agent_app.service.customer.impl;

import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.repository.customer.CustomerRepository;
import com.example.ai_travel_agent_app.service.customer.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    @Override
    public Customer getCustomerByUser(User user) {
        return customerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Customer not found for user: " + user.getEmail()));
    }

    @Override
    public Customer updateCustomerInfo(Long customerId, Customer customerData) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        
        customer.setPhoneNumber(customerData.getPhoneNumber());
        customer.setAddress(customerData.getAddress());
        customer.setGender(customerData.getGender());
        customer.setDateOfBirth(customerData.getDateOfBirth());
        
        if (customerData.getUser() != null && customerData.getUser().getAvatar() != null) {
            customer.getUser().setAvatar(customerData.getUser().getAvatar());
        }

        return customerRepository.save(customer);
    }

    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}