package com.civiora.civiora.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send OTP email with styled HTML content.
     */
    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Civiora - Your Login OTP");

        String htmlContent = """
                <div style="font-family: 'Segoe UI', sans-serif; max-width: 480px; margin: 0 auto;
                            border: 1px solid #e0d5c1; border-radius: 12px; overflow: hidden;">
                    <div style="background: linear-gradient(135deg, #bd9537, #f9f295, #b8860b);
                                padding: 25px; text-align: center; border-bottom: 3px solid #a67c00;">
                        <h1 style="color: #1a1a1a; font-size: 28px; letter-spacing: 2px; margin: 0;">Civiora</h1>
                        <p style="color: #333; font-size: 11px; text-transform: uppercase; letter-spacing: 1px; margin: 5px 0 0;">
                            Built for Better Society Living
                        </p>
                    </div>
                    <div style="padding: 30px; background: #ffffff; text-align: center;">
                        <h2 style="color: #333; margin-bottom: 10px;">Your Login OTP</h2>
                        <p style="color: #666; font-size: 14px; margin-bottom: 25px;">
                            Use the code below to complete your login. This code is valid for <strong>5 minutes</strong>.
                        </p>
                        <div style="background: #fffdf5; border: 2px dashed #bd9537; border-radius: 8px;
                                    padding: 20px; margin: 0 auto; display: inline-block;">
                            <span style="font-size: 36px; font-weight: 800; letter-spacing: 10px; color: #1a1a1a;">
                                %s
                            </span>
                        </div>
                        <p style="color: #999; font-size: 12px; margin-top: 25px;">
                            If you didn't request this, please ignore this email.
                        </p>
                    </div>
                </div>
                """.formatted(otp);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    /**
     * Send payment due reminder to a resident.
     */
    public void sendPaymentReminder(String toEmail, String name, double amount, String month) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("Civiora - Maintenance Due Reminder for " + month);
            String html = """
                    <div style="font-family:'Segoe UI',sans-serif;max-width:480px;margin:0 auto;border:1px solid #e0d5c1;border-radius:12px;overflow:hidden;">
                        <div style="background:linear-gradient(135deg,#bd9537,#f9f295,#b8860b);padding:20px;text-align:center;">
                            <h1 style="color:#1a1a1a;margin:0;font-size:24px;">Civiora</h1>
                        </div>
                        <div style="padding:25px;background:#fff;">
                            <p style="font-size:15px;color:#333;">Dear <strong>%s</strong>,</p>
                            <p style="color:#666;margin-top:10px;">Your maintenance payment of <strong>₹%.0f</strong> for <strong>%s</strong> is due.</p>
                            <p style="color:#666;margin-top:10px;">Please login to Civiora and make the payment at the earliest.</p>
                            <p style="color:#999;font-size:12px;margin-top:20px;">— Civiora Society Management</p>
                        </div>
                    </div>
                    """.formatted(name, amount, month);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Failed to send reminder to " + toEmail + ": " + e.getMessage());
        }
    }
}
