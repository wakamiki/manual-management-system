package com.example.manual.dto;


import java.time.LocalDateTime;

import com.example.manual.enums.UserRole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class UserRequestDto {

@NotNull
private String loginId;

@NotBlank
@Size(max=50)
private String displayName;

@NotNull
private UserRole role;

@NotNull
private Boolean isActive;

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

}
