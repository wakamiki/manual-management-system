package com.example.manual.dto;

import com.example.manual.enums.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserFormDto {
  public UserFormDto() {
  }

  private Long id;
  @NotBlank(message = "userIDは必須です。")
  private String loginId;
  @NotBlank(message = "氏名は必須です。")
  @Size(max = 50)
  private String displayName;
  @NotNull(message = "ROLEは必須です。")
  private UserRole role;
  private boolean isActive = true;

  // getter
  public Long getId() {
    return id;
  }

  public String getLoginId() {
    return this.loginId;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public UserRole getRole() {
    return this.role;
  }

  public boolean getIsActive() {

    return this.isActive;
  }

  // setter
  public void setId(Long id) {
    this.id = id;
  }

  public void setLoginId(String loginId) {
    this.loginId = loginId;
  }

  public void setDisplayName(String displayName) {

    this.displayName = displayName;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }

}
