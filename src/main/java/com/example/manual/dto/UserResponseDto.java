package com.example.manual.dto;

public class UserResponseDto {
  public UserResponseDto() {

  }
private Long id;
private String displayName;

  //getter
public  Long getId() {
return this.id;
}

public String getDisplayName() {
return this.displayName;
}

// setter
public void setId(Long id) {
  this.id = id;
}

public void setDisplayName(String displayName) {
  this.displayName = displayName;
}
}
