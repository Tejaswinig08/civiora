package com.civiora.civiora.controller;

import com.civiora.civiora.models.User;
import com.civiora.civiora.repositories.UserRepo;
import com.civiora.civiora.service.EmailService;
import com.civiora.civiora.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepo userRepo;

    /**
     * Step 1: Send OTP to the user's registered email.
     * Request body: { "email": "user@example.com" }
     */
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "Email is required");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if user exists with this email
        User user = userRepo.findByEmail(email.trim());
        if (user == null) {
            response.put("status", "error");
            response.put("message", "No account found with this email");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String otp = otpService.generateOtp(email.trim());
            emailService.sendOtpEmail(email.trim(), otp);
            response.put("status", "success");
            response.put("message", "OTP sent to " + email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to send OTP. Please try again.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Step 2: Verify OTP and log the user in.
     * Request body: { "email": "user@example.com", "otp": "123456" }
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");
        String otp = request.get("otp");

        if (email == null || otp == null) {
            response.put("status", "error");
            response.put("message", "Email and OTP are required");
            return ResponseEntity.badRequest().body(response);
        }

        boolean isValid = otpService.validateOtp(email.trim(), otp.trim());

        if (isValid) {
            User user = userRepo.findByEmail(email.trim());
            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("userId", user.getId());
            response.put("role", user.getRole());
            response.put("name", user.getName());
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Invalid or expired OTP");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
