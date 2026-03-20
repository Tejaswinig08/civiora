package com.civiora.civiora.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private String facilityName;

    @Column(nullable = false)
    private String bookingDate;

    @Column(nullable = false)
    private String bookingTime;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String status = "confirmed";   // confirmed | cancelled

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "confirmed";
    }
}

