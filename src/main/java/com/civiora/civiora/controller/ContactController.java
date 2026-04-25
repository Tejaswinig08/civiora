package com.civiora.civiora.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    /**
     * Receives contact form data and sends it as an email to the admin.
     * POST /send-email
     * Body: { "name": "...", "phone": "...", "email": "...", "doubt": "..." }
     */
    @PostMapping("/send-email")
    public ResponseEntity<Map<String, String>> sendContactEmail(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();

        String name  = request.get("name");
        String phone = request.get("phone");
        String email = request.get("email");
        String doubt = request.get("doubt");

        // Server-side validation
        if (name == null || name.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            doubt == null || doubt.trim().isEmpty()) {
            response.put("status", "error");
            response.put("message", "All fields are required.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(adminEmail);
            helper.setReplyTo(email.trim());
            helper.setSubject("Civiora - New Contact Query from " + name.trim());

            String htmlContent = """
                    <div style="font-family: 'Segoe UI', sans-serif; max-width: 520px; margin: 0 auto;
                                border: 1px solid #e0d5c1; border-radius: 12px; overflow: hidden;">
                        <div style="background: linear-gradient(135deg, #bd9537, #f9f295, #b8860b);
                                    padding: 22px; text-align: center; border-bottom: 3px solid #a67c00;">
                            <h1 style="color: #1a1a1a; font-size: 24px; letter-spacing: 2px; margin: 0;">Civiora</h1>
                            <p style="color: #333; font-size: 11px; text-transform: uppercase; letter-spacing: 1px; margin: 5px 0 0;">
                                New Contact Form Submission
                            </p>
                        </div>
                        <div style="padding: 28px; background: #ffffff;">
                            <table style="width: 100%%; border-collapse: collapse; font-size: 14px;">
                                <tr>
                                    <td style="padding: 10px 12px; font-weight: 700; color: #666; width: 100px; vertical-align: top;">Name</td>
                                    <td style="padding: 10px 12px; color: #1a1a1a;">%s</td>
                                </tr>
                                <tr style="background: #fffdf5;">
                                    <td style="padding: 10px 12px; font-weight: 700; color: #666; vertical-align: top;">Phone</td>
                                    <td style="padding: 10px 12px; color: #1a1a1a;">%s</td>
                                </tr>
                                <tr>
                                    <td style="padding: 10px 12px; font-weight: 700; color: #666; vertical-align: top;">Email</td>
                                    <td style="padding: 10px 12px; color: #1a1a1a;">%s</td>
                                </tr>
                                <tr style="background: #fffdf5;">
                                    <td style="padding: 10px 12px; font-weight: 700; color: #666; vertical-align: top;">Query</td>
                                    <td style="padding: 10px 12px; color: #1a1a1a; line-height: 1.5;">%s</td>
                                </tr>
                            </table>
                        </div>
                        <div style="padding: 14px; background: #f9f9f9; text-align: center; font-size: 11px; color: #999;">
                            Sent via Civiora Contact Form
                        </div>
                    </div>
                    """.formatted(
                        escapeHtml(name.trim()),
                        escapeHtml(phone.trim()),
                        escapeHtml(email.trim()),
                        escapeHtml(doubt.trim())
                    );

            helper.setText(htmlContent, true);
            mailSender.send(message);

            response.put("status", "success");
            response.put("message", "Message sent successfully.");
            return ResponseEntity.ok(response);

        } catch (MessagingException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Failed to send message. Please try again.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Basic HTML escaping to prevent XSS in email content.
     */
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
