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

private String displayName;

private  UserRole role;

}
