package com.civiora.civiora.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String performedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ActivityLog() {}

    public ActivityLog(String action, String performedBy) {
        this.action      = action;
        this.performedBy = performedBy;
        this.createdAt   = LocalDateTime.now();
    }

    public Long          getId()          { return id; }
    public String        getAction()      { return action; }
    public String        getPerformedBy() { return performedBy; }
    public LocalDateTime getCreatedAt()   { return createdAt; }

    public void setId(Long id)                   { this.id          = id; }
    public void setAction(String action)         { this.action      = action; }
    public void setPerformedBy(String by)        { this.performedBy = by; }
    public void setCreatedAt(LocalDateTime time) { this.createdAt   = time; }
}
