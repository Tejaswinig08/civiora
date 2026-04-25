package com.civiora.civiora.controller;

import com.civiora.civiora.models.Complaint;
import com.civiora.civiora.models.User;
import com.civiora.civiora.repositories.ComplaintRepo;
import com.civiora.civiora.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
