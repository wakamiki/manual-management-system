package com.example.manual.entity;

import java.time.LocalDateTime;

import com.example.manual.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 50)
  private String loginId;

  @Setter
  @Column(nullable = false, length = 255)
  private String password;

  @Setter
  @Column(nullable = false, length = 50)
  private String displayName;
  public String getDisplayName() {
    return this.displayName;
  }

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role;
  public void markRoleAdmin() {
    this.role = UserRole.ADMIN;
  }
    public void markRoleApprover() {
    this.role = UserRole.APPROVER;
  }
  public void markRoleUser() {
    this.role = UserRole.USER;
  }

  @Column(nullable = false)
  private boolean isActive;
  public void activate() {
    this.isActive = true;
  }
  public void deactivate() {
    this.isActive = false;
  }

  private LocalDateTime lastLoginAt;
  public void markLastLoginNow() {
    this.lastLoginAt = LocalDateTime.now();
  }

  private LocalDateTime createdAt;
  public void markCreatedNow() {
    this.createdAt = LocalDateTime.now();
  }

  private LocalDateTime updatedAt;
  public void markUpdatedNow() {
    this.updatedAt = LocalDateTime.now();
  }

  public Long getId() {
    return this.id;
  }

  public String getLoginId() {
    return this.loginId;
  }

  public String getPassword() {
    return this.password;
  }

  public UserRole getRole() {
    return this.role;
  }

  public boolean isActive() {
    return this.isActive;
  }

  public LocalDateTime getLastLoginAt() {
    return this.lastLoginAt;
  }

  public LocalDateTime getCreatedAt() {
    return this.createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return this.updatedAt;
  }

}
