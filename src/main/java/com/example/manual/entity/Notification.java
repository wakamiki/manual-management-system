package com.example.manual.entity;

import java.time.LocalDateTime;

import com.example.manual.enums.NotificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "target_user_id")
    private User targetUser;
    @ManyToOne
    @JoinColumn(name = "manual_id")
    private Manual manual;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    private LocalDateTime createdAt;

    // getter
    public Long getId() {
        return this.id;
    }

    public User getTargetUser() {
        return this.targetUser;
    }

    public Manual getManual() {
        return this.manual;
    }

    public NotificationType getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    // setter

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public void setManual(Manual manual) {
        this.manual = manual;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void markCreatedNow() {
        this.createdAt = LocalDateTime.now();
    }

}
