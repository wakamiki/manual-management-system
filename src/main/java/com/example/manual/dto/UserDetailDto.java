package com.example.manual.dto;

import java.time.LocalDateTime;

import com.example.manual.enums.UserRole;

public class UserDetailDto {
  public UserDetailDto() {

  }
private String loginId;
private String displayName;
private UserRole role;
private boolean isActive;
private LocalDateTime LastLoginAt;

  //getter
public  String getLoginId() {
return this.loginId;
}

public String getDisplayName() {
return this.displayName;
}

public UserRole getRole() {

  return this.role;
}

public boolean isActive(){
  return this.isActive;
}

public LocalDateTime getLastLoginAt() {
  return this.LastLoginAt;
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

public void setActive(boolean isActive){
  this.isActive = isActive;
}

public void setLastLoginAt(LocalDateTime lastLoginAt){
  this.LastLoginAt = lastLoginAt;
}
}
