package com.example.manual.dto;

import com.example.manual.enums.UserRole;

public class UserResponseDto {
  public UserResponseDto() {

  }
private Long id;
private String displayName;
private UserRole userRole;

  //getter
public  Long getId() {
return this.id;
}

public String getDisplayName() {
return this.displayName;
}

public UserRole getUserRole(){
return this.userRole;
}

// setter
public void setId(Long id) {
  this.id = id;
}

public void setDisplayName(String displayName) {
  this.displayName = displayName;
}
public void setUserRole(UserRole userRole){
  this.userRole = userRole;
}
}
