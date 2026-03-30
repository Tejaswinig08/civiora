package com.civiora.civiora.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private String role; // USER or ADMIN

    @Column(nullable = false, length = 500)
    private String message;

    /**
     * NULL  = public message (visible to everyone in "All" channel)
     * non-NULL = private DM (visible only to sender & receiver)
     */
    @Column
    private Integer receiverId;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    @PrePersist
    public void prePersist() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }
}
