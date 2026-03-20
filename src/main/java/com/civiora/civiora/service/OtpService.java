package com.civiora.civiora.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private final SecureRandom random = new SecureRandom();

    // In-memory store: email -> OtpEntry
    private final ConcurrentHashMap<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    /**
     * Generate a 6-digit OTP for the given email.
     */
    public String generateOtp(String email) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        String otpString = otp.toString();
        otpStore.put(email.toLowerCase(), new OtpEntry(otpString, LocalDateTime.now()));
        return otpString;
    }

    /**
     * Validate the OTP for the given email.
     * Returns true if valid and not expired, then removes it.
     */
    public boolean validateOtp(String email, String otp) {
        String key = email.toLowerCase();
        OtpEntry entry = otpStore.get(key);

        if (entry == null) {
            return false;
        }

        // Check expiry
        if (entry.createdAt.plusMinutes(OTP_EXPIRY_MINUTES).isBefore(LocalDateTime.now())) {
            otpStore.remove(key);
            return false;
        }

        // Check OTP match
        if (entry.otp.equals(otp)) {
            otpStore.remove(key); // One-time use
            return true;
        }

        return false;
    }

    /**
     * Inner class to hold OTP and its creation time.
     */
    private static class OtpEntry {
        String otp;
        LocalDateTime createdAt;

        OtpEntry(String otp, LocalDateTime createdAt) {
            this.otp = otp;
            this.createdAt = createdAt;
        }
    }
}
