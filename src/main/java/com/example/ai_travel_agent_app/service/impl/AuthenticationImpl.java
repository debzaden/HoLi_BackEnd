package com.example.ai_travel_agent_app.service.impl;

import com.example.ai_travel_agent_app.dto.LoginRequest;
import com.example.ai_travel_agent_app.dto.LoginResponse;
import com.example.ai_travel_agent_app.dto.UserRespone;
import com.example.ai_travel_agent_app.model.Role;
import com.example.ai_travel_agent_app.model.User;
import com.example.ai_travel_agent_app.model.Worker;
import com.example.ai_travel_agent_app.model.Admin;
import com.example.ai_travel_agent_app.model.WorkerStatus;
import com.example.ai_travel_agent_app.repository.UserRepository;
import com.example.ai_travel_agent_app.repository.worker.WorkerRepository;
import com.example.ai_travel_agent_app.service.admin.AdminRepository;
import com.example.ai_travel_agent_app.service.AuthenticationService;
import com.example.ai_travel_agent_app.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationImpl  implements AuthenticationService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse authenticate(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            // Nếu email đúng nhưng mật khẩu sai
            throw new BadCredentialsException("Email hoặc mật khẩu không chính xác");
        }

        var userOptional = repository.findByEmail(request.getEmail());
        var user = userOptional.get();
        var accessToken = jwtService.generateToken(user, user, "accessToken");
        var refreshToken = jwtService.generateToken(user, user, "refreshToken");
        UserRespone userRespone = new UserRespone();
        userRespone.setId(user.getUserId());
        userRespone.setFullName(user.getRealUserName());
        userRespone.setRole(user.getRole());

        userRespone.setEmail(user.getEmail());
        userRespone.setRole(user.getRole());
        userRespone.setAvatar(user.getAvatar());

       if(user.getRole() == Role.WORKER){
           Worker worker = workerRepository.findFirstByUser(user).orElseThrow();
           userRespone.setStatus(worker.getStatus());
       } else if(user.getRole() == Role.ADMIN){
           // Kiểm tra xem admin có tồn tại trong bảng admin không
           Admin admin = adminRepository.findByUser(user).orElse(null);
           if (admin == null) {
               // Tự động tạo admin record nếu chưa có
               admin = new Admin();
               admin.setUser(user);
               adminRepository.save(admin);
           }
           userRespone.setStatus(WorkerStatus.ACTIVE); // Admin luôn active
       }

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userRespone)
                .build();
    }
}
