package com.example.ai_travel_agent_app.controller.worker;


import com.example.ai_travel_agent_app.dto.worker.VerifyIdentityRequest;
import com.example.ai_travel_agent_app.service.worker.WorkerService;
import com.example.ai_travel_agent_app.utils.BindingValidError;
import com.example.ai_travel_agent_app.utils.UserFromAuth;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.twilio.type.PhoneNumber;
import com.twilio.Twilio;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class WorkerVerificationController {

    @Value("${twilio.accountSid}")
    private String accountSid;

    @Value("${twilio.authToken}")
    private String authToken;

    @Value("${twilio.phoneNumber}")
    private String twilioPhoneNumber;

    @Autowired
    private WorkerService workerService;

    private final Map<String, String> otpStore = new HashMap<>();



    // xác minh danh tính
    @PostMapping("/worker/verification/identity")
    public ResponseEntity<?> verificateIdentity(@ModelAttribute VerifyIdentityRequest workerData) {
        String userEmail = UserFromAuth.getUserEmail();
        workerService.verificateIdentity(workerData, userEmail);
        return ResponseEntity.status(200).body("success");
    }

    @PostMapping("/worker/verification/send-otp")
    public ResponseEntity<?> getOtp(@RequestBody Map<String, String> body) {
        String phoneNumber =  "+84"+ body.get("phoneNumber").trim().substring(1);
        String otp = generateOtp(phoneNumber);
        //sendSms(phoneNumber, "HoLI - Xác thực số điẹn thoại: " + otp);
        System.out.println("OTP: " + otp);
        return ResponseEntity.status(200).body(otp);
    }

    @PostMapping("/worker/verification/phone")
    public ResponseEntity<?> verificateOtp(@RequestBody Map<String, String> body) {
        String otp =  body.get("otp").trim();
        String phoneNumber =  "+84"+ body.get("phoneNumber").trim().substring(1);

        if (!otp.equals(otpStore.get(phoneNumber))) {
            return ResponseEntity.badRequest().body("OTP không chính xác");
        }
        String userEmail = UserFromAuth.getUserEmail();
        // update phone and active
        workerService.updatePhoneNumber(userEmail, phoneNumber);
        otpStore.remove(phoneNumber);
        return ResponseEntity.status(200).body("success");
    }

    public  String generateOtp(String phone) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        otpStore.put(phone, otp);
        System.out.println("OTP " + otp);
        return otp;
    }

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }
    public void sendSms(String toPhoneNumber, String messageBody) {
        Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber("+"+twilioPhoneNumber),
                messageBody
        ).create();
    }
}
