package com.example.manual.dto;

import java.util.List;

import com.example.manual.enums.UserRole;
import com.example.manual.enums.ViewMode;

public class UserFormDto {

  public UserFormDto() {

  }

  private List<UserDetailDto> allUserDto;
  private ViewMode mode;
  private UserResponseDto playUser;
  private UserDetailDto targetUser = new UserDetailDto();
  private List<UserRole> allRole;
  private Long userCount;

  // getter
  public List<UserDetailDto> getAllUserDto() {
    return allUserDto;
  }

  public ViewMode getMode() {
    return mode;
  }

  public UserResponseDto getPlayUser() {
    return playUser;
  }

  public UserDetailDto getTargetUser() {
    return targetUser;
  }

  public List<UserRole> getAllRole() {
    return allRole;
  }

  public Long getUserCount() {
    return userCount;
  }

  // setter
  public void setTargetUser(UserDetailDto targetUser) {
    this.targetUser = targetUser;
  }

  public void setPlayUser(UserResponseDto playUser) {
    this.playUser = playUser;
  }

  public void setMode(ViewMode mode) {
    this.mode = mode;
  }

  public void setAllUserDto(List<UserDetailDto> allUserDto) {
    this.allUserDto = allUserDto;
  }

  public void setAllRole(List<UserRole> allRole) {
    this.allRole = allRole;
  }

  public void setUserCount(Long userCount) {
    this.userCount = userCount;
  }
}
