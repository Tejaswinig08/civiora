package com.civiora.civiora.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String email;

    @Column(nullable = false)
    private String wing;

    @Column(nullable = false)
    private String flat;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String month;      // e.g. "2026-03"

    @Column(nullable = false)
    private String status;     // paid | pending | overdue

    @Column
    private LocalDateTime paidOn;

    public Payment() {}

    public Long          getId()     { return id; }
    public String        getName()   { return name; }
    public String        getEmail()  { return email; }
    public String        getWing()   { return wing; }
    public String        getFlat()   { return flat; }
    public double        getAmount() { return amount; }
    public String        getMonth()  { return month; }
    public String        getStatus() { return status; }
    public LocalDateTime getPaidOn() { return paidOn; }

    public void setId(Long id)                  { this.id     = id; }
    public void setName(String name)            { this.name   = name; }
    public void setEmail(String email)          { this.email  = email; }
    public void setWing(String wing)            { this.wing   = wing; }
    public void setFlat(String flat)            { this.flat   = flat; }
    public void setAmount(double amount)        { this.amount = amount; }
    public void setMonth(String month)          { this.month  = month; }
    public void setStatus(String status)        { this.status = status; }
    public void setPaidOn(LocalDateTime paidOn) { this.paidOn = paidOn; }
}
