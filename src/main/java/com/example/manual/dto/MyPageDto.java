package com.example.manual.dto;

import java.util.List;

public class MyPageDto {
  public MyPageDto() {

  }

  private List<ManualResponseDto> rollbackManualList;

  private List<ManualResponseDto> createdManualList;

  private List<ManualResponseDto> pendingManualList;

  private Long rollbackCount;

  private Long pendingUnCreatedCount;


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

  public Long getRollbackCount(){
    return this.rollbackCount;
  }

  public Long getPendingUnCreatedCount(){
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

  public void setRollbackCount(Long rollbackCount){
    this.rollbackCount = rollbackCount;
  }

  public void setPendingUnCreatedCount(Long pendingUnCreatedCount){
    this.pendingUnCreatedCount = pendingUnCreatedCount;
  }

}
