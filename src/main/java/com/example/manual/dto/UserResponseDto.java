package com.example.manual.dto;

import com.example.manual.enums.UserRole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {

private String loginId;

private String password;

private String displayName;

private  UserRole role;

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

}
