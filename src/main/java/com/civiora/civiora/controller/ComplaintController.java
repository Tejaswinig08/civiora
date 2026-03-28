package com.civiora.civiora.controller;

import com.civiora.civiora.models.Complaint;
import com.civiora.civiora.models.User;
import com.civiora.civiora.repositories.ComplaintRepo;
import com.civiora.civiora.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ComplaintController {

    @Autowired
    ComplaintRepo complaintRepo;

    @Autowired
    UserRepo userRepo;

    // ── USER: submit a complaint ──────────────────────────────────────────────
    @PostMapping("/complaints")
    public ResponseEntity<String> submitComplaint(@RequestBody Complaint complaint) {
        if (complaint.getPriority() == null || complaint.getPriority().isBlank()) {
            complaint.setPriority("LOW");
        }
        complaint.setStatus("PENDING");

        // Resolve sender name from userId if not provided
        if (complaint.getSenderName() == null || complaint.getSenderName().isBlank()) {
            userRepo.findById(complaint.getUserId()).ifPresent(u -> complaint.setSenderName(u.getName()));
        }

        complaintRepo.save(complaint);
        return ResponseEntity.ok("Complaint submitted");
    }

    // ── USER: get own complaints ──────────────────────────────────────────────
    @GetMapping("/complaints/user/{userId}")
    public List<Complaint> getUserComplaints(@PathVariable int userId) {
        return complaintRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ── ADMIN: get all complaints ─────────────────────────────────────────────
    @GetMapping("/admin/complaints")
    public List<Complaint> getAllComplaints() {
        return complaintRepo.findAllByOrderByCreatedAtDesc();
    }

    // ── ADMIN: update status (APPROVED or RESOLVED) ───────────────────────────
    @PatchMapping("/admin/complaints/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable int id, @RequestBody Map<String, String> body) {
        Complaint c = complaintRepo.findById(id).orElse(null);
        if (c == null) return ResponseEntity.notFound().build();

        String newStatus = body.getOrDefault("status", "").toUpperCase();
        if (!newStatus.equals("APPROVED") && !newStatus.equals("RESOLVED")) {
            return ResponseEntity.badRequest().body("Status must be APPROVED or RESOLVED");
        }

        c.setStatus(newStatus);
        complaintRepo.save(c);
        return ResponseEntity.ok("Status updated to " + newStatus);
    }
}
