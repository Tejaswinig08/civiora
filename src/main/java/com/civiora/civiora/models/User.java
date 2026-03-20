package com.civiora.civiora.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name; // Captured from signupName

    @Column(nullable = false, unique = true)
    private String email; // Captured from signupEmail

    @Column(nullable = false)
    private String password; // Captured from signupPassword

    @Column(nullable = false)
    private String wing; // Captured from signupWing (e.g., "A")

    @Column(nullable = false)
    private String flat; // Captured from signupFlat (e.g., "101")

    @Column(nullable = false)
    private String role; // Captured from signupRole (e.g., "owner")

    @Column(nullable = false)
    private double balance = 0.0; // Defaulted to 0.0 for new society members
}