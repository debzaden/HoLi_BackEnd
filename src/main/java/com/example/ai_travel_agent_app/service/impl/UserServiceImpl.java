package com.example.ai_travel_agent_app.service.impl;

import com.example.ai_travel_agent_app.dto.UserRequest;
import com.example.ai_travel_agent_app.model.*;
import com.example.ai_travel_agent_app.repository.PasswordResetTokenRepository;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.repository.VerificationTokenRepository;
import com.example.ai_travel_agent_app.repository.admin.MyAdminRepository;
import com.example.ai_travel_agent_app.repository.customer.CustomerRepository;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;
import com.example.ai_travel_agent_app.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {

    @Value("${avartar-default}")
    private String avartarDefault;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MyAdminRepository adminRepository;

    @Override
    public User registerUser(UserRequest userModel) {

        User user = new User();
        user.setUserName(userModel.getUserName());
        user.setEmail(userModel.getEmail());
        user.setAvatar(avartarDefault);
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setCreatedAt(LocalDate.now());

        Role role;
        if ("CUSTOMER".equalsIgnoreCase(userModel.getRole())) {
            role = Role.CUSTOMER;
        } else if ("ADMIN".equalsIgnoreCase(userModel.getRole())) {
            role = Role.ADMIN;
        } else {
            role = Role.WORKER;
        }
        user.setRole(role);

        User savedUser = userRepository.save(user);

        Worker worker = null;
        if (role == Role.WORKER) {
            worker = new Worker();
            worker.setUser(savedUser);
            worker.setStatus(WorkerStatus.CCCD_VERIFING);
            workerRepository.save(worker);
        } else if (role == Role.CUSTOMER) {
            Customer customer = new Customer();
            customer.setUser(user);
            customerRepository.save(customer);
        } else {
            Admin admin = new Admin();
            admin.setUser(user);
            adminRepository.save(admin);
        }

        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String verificationToken(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if(verificationToken == null) {
            return "invalid";
        }

        User user = verificationToken.getUser();

        Calendar cal = Calendar.getInstance();

        if(0 >= (verificationToken.getExpirationTime().getTime() - cal.getTime().getTime())) {

            return "expired";
        }

        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public  Optional<User> findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user;
    }

    @Override
    public void savePasswordResetToken(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        passwordResetTokenRepository.save(passwordResetToken);
    }
  
}
