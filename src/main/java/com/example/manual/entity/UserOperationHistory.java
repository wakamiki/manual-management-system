package com.example.manual.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class UserOperationHistory {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Setter
@ManyToOne    
@Column(nullable = false)
@JoinColumn(name = "target_user_id")
private User targetUser;

@Column(nullable = false)
private User operatedByUser;

@Column(nullable = false,length = 30)
private String operationType;

@Column(nullable = false,length = 100)
private String operationDetail;

@Column(nullable = false)
private LocalDateTime createdAt;
public void markCreatedNow() {
    this.createdAt = LocalDateTime.now();
}
}
