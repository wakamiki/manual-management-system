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
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
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

  // getter
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

  // setter
  public void setPassword(String password) {
    this.password = password;
  }

  public void setDisplayName(String displayName) {

    this.displayName = displayName;
  }

  private void setLoginId(String loginId) {
    this.loginId = loginId;
  }

  private void setUserRole(UserRole role) {
    this.role = role;
  }

  // メソッド

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

  public static User createNew(String loginId, String displayName, UserRole role) {
    User user = new User();
    user.setLoginId(loginId);
    user.setDisplayName(displayName);
    user.setUserRole(role);
    user.activate();
    user.markCreatedNow();
    user.markUpdatedNow();
    return user;
  }

  public static UserRole applyRole(UserRole role) {
    User targetUser = new User();
    targetUser.setUserRole(role);
    return targetUser.getRole();
  }

}
