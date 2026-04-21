package com.example.manual.dto;

import java.time.LocalDateTime;

import com.example.manual.enums.UserRole;

public class UserDetailDto {
  public UserDetailDto() {

  }

  private Long id;
  private String loginId;
  private String displayName;
  private UserRole role;
  private boolean isActive;
  private LocalDateTime lastLoginAt;
  private String activeLabel;

  // getter
  public String getLoginId() {
    return this.loginId;
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

  public String getActiveLabel() {
    return activeLabel;
  }

  public Long getId() {
    return id;
  }

  // setter
  public void setLoginId(String loginId) {
    this.loginId = loginId;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setRole(UserRole role) {

    this.role = role;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  public void setLastLoginAt(LocalDateTime lastLoginAt) {
    this.lastLoginAt = lastLoginAt;
  }

  public void setActiveLabel(String statusLabel) {
    this.activeLabel = statusLabel;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
