package com.example.manual.dto;

import com.example.manual.enums.UserRole;

public class UserResponseDto {
  public UserResponseDto() {

  }
private String loginId;
private String password;
private String displayName;
private UserRole role;

//ゲッター
public  String getLoginId() {
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

//セッター
public void setLoginId(String loginId) {
  this.loginId = loginId;
}

public String setPassword() {
  return this.password;
}

public void setDisplayName(String displayName) {
  this.displayName = displayName;
}

public void setRole(UserRole role) {
  this.role = role;
}

}
