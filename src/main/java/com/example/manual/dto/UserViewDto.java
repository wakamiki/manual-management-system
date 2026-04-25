package com.example.manual.dto;

import java.util.List;

import com.example.manual.enums.UserRole;
import com.example.manual.enums.ViewMode;

public class UserViewDto {

  public UserViewDto() {

  }

  private List<UserDetailDto> allUserDto;
  private PagingDto pagingDto;
  private ViewMode mode = ViewMode.CREATE;
  private UserResponseDto playUser;
  private List<UserRole> allRole;
  private Long userCount;
  private boolean canGuest;

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

  public List<UserRole> getAllRole() {
    return allRole;
  }

  public Long getUserCount() {
    return userCount;
  }

  public PagingDto getPagingDto() {
    return pagingDto;
  }

  public boolean isCanGuest() {
    return canGuest;
  }

  // setter

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

  public void setPagingDto(PagingDto pagingDto) {
    this.pagingDto = pagingDto;
  }

  public void setCanGuest(boolean canGuest) {
    this.canGuest = canGuest;
  }
}
