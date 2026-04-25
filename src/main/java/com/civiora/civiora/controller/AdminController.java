package com.civiora.civiora.controller;

import com.civiora.civiora.models.*;
import com.civiora.civiora.repositories.*;
import java.util.Map;
import com.civiora.civiora.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired UserRepo         userRepo;
    @Autowired NoticeRepo       noticeRepo;
    @Autowired PaymentRepo      paymentRepo;
    @Autowired TransactionRepo  transactionRepo;
    @Autowired BookingRepo      bookingRepo;
    @Autowired ActivityLogRepo  logRepo;
    @Autowired EmailService     emailService;
    @Autowired ComplaintRepo    complaintRepo;

    // ── USERS ──────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @PostMapping("/users")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("owner");
        }
        userRepo.save(user);
        logRepo.save(new ActivityLog("Added resident/admin: " + user.getName(), "Admin"));
        return ResponseEntity.ok("User added successfully");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        userRepo.deleteById(id);
        logRepo.save(new ActivityLog("Deleted user: " + user.getName(), "Admin"));
        return ResponseEntity.ok("User deleted");
    }

    // ── NOTICES ────────────────────────────────────────────────────────────────

    @GetMapping("/notices")
    public List<Notice> getAllNotices() {
        return noticeRepo.findAllByOrderByDateDesc();
    }

    @PostMapping("/notices")
    public ResponseEntity<String> addNotice(@RequestBody Notice notice) {
        notice.setDate(LocalDateTime.now());
        if (notice.getAuthor() == null || notice.getAuthor().isBlank()) {
            notice.setAuthor("Admin");
        }
        noticeRepo.save(notice);
        logRepo.save(new ActivityLog("Posted notice: " + notice.getTitle(), "Admin"));
        return ResponseEntity.ok("Notice posted successfully");
    }

    @DeleteMapping("/notices/{id}")
    public ResponseEntity<String> deleteNotice(@PathVariable Long id) {
        Notice n = noticeRepo.findById(id).orElse(null);
        if (n == null) return ResponseEntity.notFound().build();
        noticeRepo.deleteById(id);
        logRepo.save(new ActivityLog("Deleted notice: " + n.getTitle(), "Admin"));
        return ResponseEntity.ok("Notice deleted");
    }

    // ── MAINTENANCE & PAYMENTS ───────────────────────────────────────────────────

    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    @GetMapping("/payments")
    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    @PostMapping("/payments")
    public ResponseEntity<String> addPayment(@RequestBody Payment payment) {
        if (payment.getStatus() == null || payment.getStatus().isBlank()) {
            payment.setStatus("pending");
        }
        paymentRepo.save(payment);
        logRepo.save(new ActivityLog("Added maintenance record for " + payment.getName() + " (" + payment.getWing() + "-" + payment.getFlat() + ")", "Admin"));
        return ResponseEntity.ok("Payment record saved");
    }

    @PatchMapping("/payments/{id}/mark-paid")
    public ResponseEntity<String> markPaid(@PathVariable Long id) {
        Payment p = paymentRepo.findById(id).orElse(null);
        if (p == null) return ResponseEntity.notFound().build();
        p.setStatus("paid");
        p.setPaidOn(LocalDateTime.now());
        paymentRepo.save(p);
        logRepo.save(new ActivityLog("Marked " + p.getName() + " as paid for " + p.getMonth(), "Admin"));
        return ResponseEntity.ok("Marked as paid");
    }

    @DeleteMapping("/payments/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable Long id) {
        paymentRepo.deleteById(id);
        return ResponseEntity.ok("Payment record deleted");
    }

    @PostMapping("/send-reminders")
    public ResponseEntity<String> sendDueReminders() {
        List<Payment> due = paymentRepo.findByStatusNot("paid");
        for (Payment p : due) {
            if (p.getEmail() != null && !p.getEmail().isBlank()) {
                emailService.sendPaymentReminder(p.getEmail(), p.getName(), p.getAmount(), p.getMonth());
            }
        }
        logRepo.save(new ActivityLog("Sent payment reminders to " + due.size() + " resident(s)", "Admin"));
        return ResponseEntity.ok("Reminders sent to " + due.size() + " residents");
    }

    // ── BOOKINGS ───────────────────────────────────────────────────────────────

    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }

    @PatchMapping("/bookings/{id}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable int id) {
        Booking b = bookingRepo.findById(id).orElse(null);
        if (b == null) return ResponseEntity.notFound().build();
        b.setStatus("cancelled");
        bookingRepo.save(b);
        logRepo.save(new ActivityLog("Cancelled booking #" + id + " for " + b.getFacilityName(), "Admin"));
        return ResponseEntity.ok("Booking cancelled");
    }

    @PatchMapping("/bookings/{id}/confirm")
    public ResponseEntity<String> confirmBooking(@PathVariable int id) {
        Booking b = bookingRepo.findById(id).orElse(null);
        if (b == null) return ResponseEntity.notFound().build();
        b.setStatus("confirmed");
        bookingRepo.save(b);
        logRepo.save(new ActivityLog("Confirmed booking #" + id + " for " + b.getFacilityName(), "Admin"));
        return ResponseEntity.ok("Booking confirmed");
    }

    // ── COMPLAINTS ─────────────────────────────────────────────────────────────

    @GetMapping("/complaints")
    public List<Complaint> getAllComplaints() {
        return complaintRepo.findAllByOrderByCreatedAtDesc();
    }

    @PatchMapping("/complaints/{id}/status")
    public ResponseEntity<String> updateComplaintStatus(@PathVariable int id, @RequestBody Map<String, String> body) {
        Complaint c = complaintRepo.findById(id).orElse(null);
        if (c == null) return ResponseEntity.notFound().build();

        String newStatus = body.getOrDefault("status", "").toUpperCase();
        if (!newStatus.equals("APPROVED") && !newStatus.equals("RESOLVED")) {
            return ResponseEntity.badRequest().body("Status must be APPROVED or RESOLVED");
        }

        c.setStatus(newStatus);
        complaintRepo.save(c);
        logRepo.save(new ActivityLog("Updated complaint #" + id + " to " + newStatus, "Admin"));
        return ResponseEntity.ok("Status updated to " + newStatus);
    }

    // ── ACTIVITY LOG ───────────────────────────────────────────────────────────

    @GetMapping("/logs")
    public List<ActivityLog> getLogs() {
        return logRepo.findAllByOrderByCreatedAtDesc();
    }

    @DeleteMapping("/logs")
    public ResponseEntity<String> clearLogs() {
        logRepo.deleteAll();
        return ResponseEntity.ok("Logs cleared");
    }
}
