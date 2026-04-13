package com.example.manual.dto;

import java.util.List;

public class MyPageDto {
  public MyPageDto() {

  }

  private List<ManualListDto> rollbackManualList;

  private List<ManualListDto> createdManualList;

  private List<ManualListDto> pendingManualList;

  private int rollbackCount;

  private int pendingCount;


  //getter
  public List<ManualListDto> getRollbackManualList() {
    return this.rollbackManualList;
  }

  public List<ManualListDto> getCreatedManualList() {
    return this.createdManualList;
  }

  public List<ManualListDto> getPendingManualList() {
    return this.pendingManualList;
  }

  public int getRollbackCount(){
    return this.rollbackCount;
  }

  public int getPendingCount(){
    return this.pendingCount;
  }



  //setter
  public void setRollbackManualList(List<ManualListDto> rollbackManualList) {
    this.rollbackManualList = rollbackManualList;
  }

  public void setCreatedManualList(List<ManualListDto> createdManualList) {
    this.createdManualList = createdManualList;
  }

  public void setPendeingManualList(List<ManualListDto> pendingManualList) {
    this.pendingManualList = pendingManualList;
  }

  public void setRollbackCount(int rollbackCount){
    this.rollbackCount = rollbackCount;
  }

  public void setPendingCount(int pendingCount){
    this.pendingCount = pendingCount;
  }

}
