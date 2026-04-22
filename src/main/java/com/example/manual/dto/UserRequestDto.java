package com.example.manual.dto;

import com.example.manual.enums.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRequestDto {
  public UserRequestDto() {
  }

  private Long id;
  @NotBlank
  private String loginId;

  @NotBlank
  @Size(max = 50)
  private String displayName;

  @NotNull
  private UserRole role;

  @NotNull
  private Boolean isActive;

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

  public Boolean getIsActive() {

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
}
