package com.example.manual.dto;

import java.util.List;

public class MyPageDto {
  public MyPageDto() {

  }

  private List<ManualResponseDto> rollbackManualList;
  private List<ManualResponseDto> createdManualList;
  private List<ManualResponseDto> pendingManualList;
  private int rollbackCount;
  private int pendingUnCreatedCount;


  //getter
  public List<ManualResponseDto> getRollbackManualList() {
    return this.rollbackManualList;
  }

  public List<ManualResponseDto> getCreatedManualList() {
    return this.createdManualList;
  }

  public List<ManualResponseDto> getPendingManualList() {
    return this.pendingManualList;
  }

  public int getRollbackCount(){
    return this.rollbackCount;
  }

  public int getPendingUnCreatedCount() {
    return this.pendingUnCreatedCount;
  }


  //setter
  public void setRollbackManualList(List<ManualResponseDto> rollbackManualList) {
    this.rollbackManualList = rollbackManualList;
  }

  public void setCreatedManualList(List<ManualResponseDto> createdManualList) {
    this.createdManualList = createdManualList;
  }

  public void setPendingManualList(List<ManualResponseDto> pendingManualList) {
    this.pendingManualList = pendingManualList;
  }

  public void setRollbackCount(int rollbackCount){
    this.rollbackCount = rollbackCount;
  }

  public void setPendingUnCreatedCount(int pendingUnCreatedCount) {
    this.pendingUnCreatedCount = pendingUnCreatedCount;
  }
}
