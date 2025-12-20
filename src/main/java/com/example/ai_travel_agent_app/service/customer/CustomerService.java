package com.example.ai_travel_agent_app.service.customer;

import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.User;

public interface CustomerService {
    Customer getCustomerByUser(User user);
    Customer getCustomerById(Long id); // Thêm dòng này

    Customer updateCustomerInfo(Long customerId, Customer customerData);
    Customer addCustomer(Customer customer);
}