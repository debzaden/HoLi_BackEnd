package com.example.ai_travel_agent_app.controller;


import com.example.ai_travel_agent_app.dto.LoginRequest;
import com.example.ai_travel_agent_app.dto.LoginResponse;
import com.example.ai_travel_agent_app.dto.UserRequest;
import com.example.ai_travel_agent_app.dto.UserRespone;
import com.example.ai_travel_agent_app.event.RegistrationCompleteEvent;
import com.example.ai_travel_agent_app.model.Customer;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.VerificationToken;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.service.AuthenticationService;
import com.example.ai_travel_agent_app.service.EmailService;
import com.example.ai_travel_agent_app.service.JwtService;
import com.example.ai_travel_agent_app.service.UserService;
import com.example.ai_travel_agent_app.service.customer.CustomerService;
import com.example.ai_travel_agent_app.service.worker.WorkerService;
import com.example.ai_travel_agent_app.utils.BindingValidError;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired(required = false)
    private UserService userService;


    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationEventPublisher publisher; // create other thread

    // Đăng nhập
    @Autowired
    private final AuthenticationService authenticationService;
    private final UserDetailsService userDetailsService;

    @Autowired
    private JwtService  jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private CustomerService customerService;


    @PostMapping("/me/worker")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Worker worker = workerService.getWorkerByUser(user);

        UserRespone userRespone =  UserRespone.builder()
                .id(worker.getUser().getUserId())
                .avatar(user.getAvatar())
                .fullName(user.getRealUserName())
                .email(user.getEmail())
                .status(worker.getStatus())
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(userRespone);
    }

    @PostMapping("/me/customer")
    public ResponseEntity<?> getCurrentUserCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerService.getCustomerByUser(user);

        UserRespone userRespone =  UserRespone.builder()
                .id(customer.getUser().getUserId())
                .avatar(user.getAvatar())
                .fullName(user.getRealUserName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(userRespone);
    }

    @PostMapping("/me/admin")
    public ResponseEntity<?> getCurrentUserAdmin() {
        String userEmail = UserFromAuth.getUserEmail();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserRespone userRespone =  UserRespone.builder()
                .id(user.getUserId())
                .avatar(user.getAvatar())
                .fullName(user.getRealUserName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(userRespone);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = BindingValidError.getValidationErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }
        LoginResponse response = authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("RefreshToken");

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body("Refresh token is missing");
        }
        String userEmail;
        String jwt = refreshToken.substring(7).trim();
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Invalid refresh token");
        }

        // Lấy userDetails từ username
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        User user = userService.findUserByEmail(userEmail).orElse(null);
        if (user == null) {
            return ResponseEntity.status(403).body("User not found");
        }

        // Xác thực token hợp lệ
        if (!jwtService.isTokenValid(jwt, userDetails)) {
            return ResponseEntity.status(403).body("Refresh token expired or invalid");
        }
        // Tạo access token mới
        String newAccessToken = jwtService.generateToken(userDetails, user, "accessToken");
        String newRefreshToken = jwtService.generateToken(userDetails, user, "refreshToken");

        return ResponseEntity.ok(LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build());
    }

    // Đăng ký
    @PostMapping("/register")
    public ResponseEntity<?>  registerUser(@Valid @RequestBody UserRequest userRequest, BindingResult result, final HttpServletRequest request) {

        if (result.hasErrors()) {
            Map<String, String> errors = BindingValidError.getValidationErrors(result);
            return ResponseEntity.badRequest().body(errors);
        }
        User user = userService.registerUser(userRequest);
        publisher.publishEvent( new RegistrationCompleteEvent(
                user,
                applicationUrl(request)
        ));

        return ResponseEntity.ok("Register success");
    }

    // xác thực người dùng
    @GetMapping("/verifyRegistration")
    public ResponseEntity<String> verificationToken(@RequestParam("token") String token) throws IOException {
        String result = userService.verificationToken(token);

        String fileName;
        if (result.equalsIgnoreCase("valid")) {
            fileName = "static/verified-success.html";
        } else if (result.equalsIgnoreCase("")) {
            fileName = "static/verified-expired.html";
        } else {
            fileName = "static/verified-failed.html";
        }

        ClassPathResource htmlFile = new ClassPathResource(fileName);
        String htmlContent = StreamUtils.copyToString(htmlFile.getInputStream(), StandardCharsets.UTF_8);

        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlContent);
    }

    // resend xác thực người dùng
    @GetMapping("/resendVerifyToken")
    public String resendVerifyToken(@RequestParam("token") String oldToken, HttpServletRequest request) {

        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerifyTokenMail(user, applicationUrl(request), verificationToken);
        return "Bad user";
    }

    // thay đổi mật khẩu
    @GetMapping("/resetPassword")
    public String resetPassword(@RequestParam("email") String email, HttpServletRequest request) {
        User user = userService.findUserByEmail(email).get();
        if(user == null) return "Email incorrect";

        // create token
        String token = UUID.randomUUID().toString();
        userService.savePasswordResetToken(user, token);

        // send to email
        sendMaiChangePasswordToken(user, applicationUrl(request), token);
        return "Please check mail !";

    }

    // gửi lại token thay đổi mật khẩu
    private void sendMaiChangePasswordToken(User user, String applicationUrl, String token) {
        String url = applicationUrl
                + "/verifyTokenChangePassword?token="
                + token;
        // sendVerificationEmail()
        log.info("Click the link to change your password {}",
                url);
    }


    private void resendVerifyTokenMail(User user, String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl
                + "/verifyRegistration?token="
                + verificationToken.getToken();
        // sendVerificationEmail()
        log.info("Click the link to verify your account: {}",
                url);
    }

    // get Url server
    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" + request.getServerPort() +
                request.getContextPath();
    }
}
