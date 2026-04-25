package com.example.manual.dto;

import java.util.ArrayList;
import java.util.List;

public class MyPageDto {
  public MyPageDto() {

  }

  private List<ManualResponseDto> rollbackManualList = new ArrayList<>();
  private List<ManualResponseDto> createdManualList = new ArrayList<>();
  private List<ManualResponseDto> pendingManualList = new ArrayList<>();
  private int rollbackCount = 0;
  private int pendingUnCreatedCount = 0;
  private String userRoleLabel;
  private UserResponseDto userDto;
  private boolean canGuest;

  // getter
  public List<ManualResponseDto> getRollbackManualList() {
    return this.rollbackManualList;
  }

  public List<ManualResponseDto> getCreatedManualList() {
    return this.createdManualList;
  }

  public List<ManualResponseDto> getPendingManualList() {
    return this.pendingManualList;
  }

  public int getRollbackCount() {
    return this.rollbackCount;
  }

  public int getPendingUnCreatedCount() {
    return this.pendingUnCreatedCount;
  }

  public String getUserRoleLabel() {
    return userRoleLabel;
  }

  public UserResponseDto getUserDto() {
    return userDto;
  }

  public boolean isCanGuest() {
    return canGuest;
  }

  // setter
  public void setRollbackManualList(List<ManualResponseDto> rollbackManualList) {
    this.rollbackManualList = rollbackManualList;
  }

  public void setCreatedManualList(List<ManualResponseDto> createdManualList) {
    this.createdManualList = createdManualList;
  }

  public void setPendingManualList(List<ManualResponseDto> pendingManualList) {
    this.pendingManualList = pendingManualList;
  }

  public void setRollbackCount(int rollbackCount) {
    this.rollbackCount = rollbackCount;
  }

  public void setPendingUnCreatedCount(int pendingUnCreatedCount) {
    this.pendingUnCreatedCount = pendingUnCreatedCount;
  }

  public void setUserRoleLabel(String userRoleLabel) {
    this.userRoleLabel = userRoleLabel;
  }

  public void setUserDto(UserResponseDto userDto) {
    this.userDto = userDto;
  }

  public void setCanGuest(boolean canGuest) {
    this.canGuest = canGuest;
  }

}
