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

@Entity
public class User {

  public User() {
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false, length = 50)
  private String loginId;
  @Column(nullable = false, length = 255)
  private String password;
  @Column(nullable = false, length = 50)
  private String displayName;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private UserRole role;
  @Column(nullable = false)
  private boolean isActive;
  private LocalDateTime lastLoginAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;



  //ゲッター
  public Long getId() {
    return this.id;
  }

  public String getLoginId() {
    return this.loginId;
  }

  public String getPassword() {
    return this.password;
  }

  public String getDisplayName() {
    return this.displayName;
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

  //セッター
  public void setPassword(String password) {
    this.password = password;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  //メソッド
  public void markRoleAdmin() {
    this.role = UserRole.ADMIN;
  }

  public void markRoleApprover() {
    this.role = UserRole.APPROVER;
  }

  public void markRoleUser() {
    this.role = UserRole.USER;
  }
  public void activate() {
    this.isActive = true;
  }

  public void deactivate() {
    this.isActive = false;
  }
  public void markLastLoginNow() {
    this.lastLoginAt = LocalDateTime.now();
  }
  public void markCreatedNow() {
    this.createdAt = LocalDateTime.now();
  }
  public void markUpdatedNow() {
    this.updatedAt = LocalDateTime.now();
  }
}
